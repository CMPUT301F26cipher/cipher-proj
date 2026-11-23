package com.example.cipherappproject

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.campsite.CampsiteLotterySection
import com.example.cipherappproject.waitlist.WaitlistView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * UI tests for lottery waiting lists.
 * Entrant (campsite details screen): US 01.01.06 / 01.02.01 (join), 01.02.02 (leave).
 * Owner: US 02.09.01 (view entrants who joined the waitlist).
 * Seeds its own test- lotteries (with their entries) and deletes them afterwards.
 */
@RunWith(AndroidJUnit4::class)
class WaitlistFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val db = Firebase.firestore
    private val entryRepo = RepositoryDatabase.lotteryEntryRepository

    private val suffix = UUID.randomUUID().toString().take(8)
    private val campsiteId = "test-campsite-$suffix"
    private val entrantId = "test-entrant-$suffix"
    private val lotteryIds = mutableListOf<String>()

    @After
    fun cleanUp() {
        lotteryIds.forEach { id ->
            val ref = db.collection("lotteries").document(id)
            Tasks.await(ref.collection("entries").get(), 15, TimeUnit.SECONDS)
                .documents.forEach { Tasks.await(it.reference.delete(), 15, TimeUnit.SECONDS) }
            Tasks.await(ref.delete(), 15, TimeUnit.SECONDS)
        }
    }

    /** A lottery for the test campsite whose registration runs [startDays] to [endDays] from today. */
    private fun seedLottery(
        startDays: Long, endDays: Long,
        capacity: Int? = null, status: LotteryStatus = LotteryStatus.OPEN
    ): Lottery {
        val id = "test-${UUID.randomUUID()}"
        val today = LocalDate.now()
        val lottery = Lottery(
            lotteryId = id, ownerId = "test-owner-$suffix", campsiteIds = mutableListOf(campsiteId),
            numWinners = 1, startDate = today.plusDays(startDays).toString(),
            endDate = today.plusDays(endDays).toString(), maxParticipants = capacity, status = status.name
        )
        Tasks.await(db.collection("lotteries").document(id).set(lottery), 15, TimeUnit.SECONDS)
        lotteryIds.add(id)
        composeRule.waitUntil(15_000) { RepositoryDatabase.lotteryRepository.getLotteryFromListById(id) != null }
        return lottery
    }

    private fun seedEntry(lotteryId: String, userId: String, status: LotteryEntryStatus): LotteryEntry {
        val id = "test-${UUID.randomUUID()}"
        val entry = LotteryEntry(lotteryEntryId = id, lotteryId = lotteryId, userId = userId, status = status.name, positionLottery = -1)
        Tasks.await(
            db.collection("lotteries").document(lotteryId).collection("entries").document(id).set(entry),
            15, TimeUnit.SECONDS
        )
        composeRule.waitUntil(15_000) { entryRepo.getLotteryEntriesFromListByLotteryId(lotteryId).any { it.lotteryEntryId == id } }
        return entry
    }

    private fun entryStatus(lotteryId: String) =
        entryRepo.getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, entrantId)?.status

    private fun showLotterySection() {
        composeRule.waitUntil(15_000) { entryRepo.isLotteriesEntriesLoaded.value }
        composeRule.setContent { CampsiteLotterySection(campsiteId, entrantId, WaitlistController()) }
    }

    // ---------- entrant: campsite details screen ----------

    @Test
    fun campsitePage_listsOnlyLotteriesOpenForRegistration() {
        val open = seedLottery(-1, 5)
        val notYetOpen = seedLottery(3, 10)
        val closed = seedLottery(-10, -2)
        val drawn = seedLottery(-1, 5, status = LotteryStatus.COMPLETED)
        showLotterySection()

        composeRule.onNodeWithTag("openLottery_${open.lotteryId}").assertIsDisplayed()
        listOf(notYetOpen, closed, drawn).forEach {
            composeRule.onAllNodesWithTag("openLottery_${it.lotteryId}").assertCountEquals(0)
        }
    }

    @Test
    fun campsitePage_noOpenLottery_saysSo() {
        seedLottery(-10, -2)
        showLotterySection()
        composeRule.onNodeWithTag("noOpenLottery").assertIsDisplayed()
        composeRule.onAllNodesWithTag("joinWaitlistButton").assertCountEquals(0)
    }

    @Test
    fun campsitePage_joinThenLeaveThenRejoin() {
        val lottery = seedLottery(-1, 5, capacity = 10)
        showLotterySection()

        composeRule.onNodeWithText("On the waiting list: 0 / 10").assertIsDisplayed()
        composeRule.onNodeWithTag("joinWaitlistButton").performClick()
        composeRule.waitUntil(15_000) { entryStatus(lottery.lotteryId) == LotteryEntryStatus.REGISTERED.name }
        composeRule.onNodeWithTag("lotteryMessage").assertTextEquals("Successfully joined the waiting list! See My Lotteries.")
        composeRule.onNodeWithText("You're on the waiting list.").assertIsDisplayed()
        composeRule.onNodeWithText("On the waiting list: 1 / 10").assertIsDisplayed()
        composeRule.onAllNodesWithTag("joinWaitlistButton").assertCountEquals(0)

        composeRule.onNodeWithTag("leaveWaitlistButton").performClick()
        composeRule.waitUntil(15_000) { entryStatus(lottery.lotteryId) == LotteryEntryStatus.WITHDRAWN.name }
        composeRule.onNodeWithTag("lotteryMessage").assertTextEquals("You left the waiting list.")
        composeRule.onNodeWithText("On the waiting list: 0 / 10").assertIsDisplayed()

        // leaving doesn't stop them coming back while registration is open
        composeRule.onNodeWithTag("joinWaitlistButton").performClick()
        composeRule.waitUntil(15_000) { entryStatus(lottery.lotteryId) == LotteryEntryStatus.REGISTERED.name }
    }

    @Test
    fun campsitePage_fullWaitlist_cannotJoin() {
        val full = seedLottery(-1, 5, capacity = 1)
        seedEntry(full.lotteryId, "test-other-$suffix", LotteryEntryStatus.REGISTERED)
        showLotterySection()

        composeRule.onNodeWithText("On the waiting list: 1 / 1").assertIsDisplayed()
        composeRule.onNodeWithText("This lottery's waiting list is full.").assertIsDisplayed()
        composeRule.onAllNodesWithTag("joinWaitlistButton").assertCountEquals(0)
    }

    @Test
    fun campsitePage_choosesBetweenSeveralOpenLotteries() {
        val full = seedLottery(-1, 3, capacity = 1)
        seedEntry(full.lotteryId, "test-other-$suffix", LotteryEntryStatus.REGISTERED)
        val roomy = seedLottery(-1, 8)
        showLotterySection()

        // a full lottery doesn't hide another open one with space
        composeRule.onNodeWithTag("openLottery_${full.lotteryId}").assertIsDisplayed()
        composeRule.onNodeWithTag("openLottery_${roomy.lotteryId}").assertIsDisplayed()
        composeRule.onNodeWithTag("joinWaitlistButton").performClick()
        composeRule.waitUntil(15_000) { entryStatus(roomy.lotteryId) == LotteryEntryStatus.REGISTERED.name }
    }

    @Test
    fun campsitePage_alreadyDrawnEntry_cannotJoinAgain() {
        val lottery = seedLottery(-1, 5)
        seedEntry(lottery.lotteryId, entrantId, LotteryEntryStatus.DECLINED)
        showLotterySection()

        composeRule.onNodeWithText("You've already taken part in this lottery.").assertIsDisplayed()
        composeRule.onAllNodesWithTag("joinWaitlistButton").assertCountEquals(0)
        composeRule.onAllNodesWithTag("leaveWaitlistButton").assertCountEquals(0)
    }

    // ---------- owner: waitlist ----------

    @Test
    fun ownerWaitlist_showsEveryoneWhoJoined_withStatus() {
        val lottery = seedLottery(-1, 5)
        seedEntry(lottery.lotteryId, "test-a-$suffix", LotteryEntryStatus.REGISTERED)
        seedEntry(lottery.lotteryId, "test-b-$suffix", LotteryEntryStatus.REGISTERED)
        seedEntry(lottery.lotteryId, "test-left-$suffix", LotteryEntryStatus.WITHDRAWN)
        composeRule.setContent { WaitlistView(lotteryId = lottery.lotteryId) }

        composeRule.onNodeWithTag("waitlistTotal").assertTextEquals("Total Entrants: 2")
        composeRule.onNodeWithText("Entrant: test-a-$suffix").assertIsDisplayed()
        composeRule.onNodeWithText("Entrant: test-b-$suffix").assertIsDisplayed()
        composeRule.onAllNodesWithText("Entrant: test-left-$suffix").assertCountEquals(0)
        composeRule.onAllNodesWithText("Status: Waiting for the draw").assertCountEquals(2)
    }

    @Test
    fun ownerWaitlist_updatesLiveWhenSomeoneJoins() {
        val lottery = seedLottery(-1, 5)
        composeRule.waitUntil(15_000) { entryRepo.isLotteriesEntriesLoaded.value }
        composeRule.setContent { WaitlistView(lotteryId = lottery.lotteryId) }
        composeRule.onNodeWithText("No entrants have joined this lottery yet.").assertIsDisplayed()

        seedEntry(lottery.lotteryId, "test-late-$suffix", LotteryEntryStatus.REGISTERED)
        composeRule.waitUntil(15_000) {
            composeRule.onAllNodesWithText("Entrant: test-late-$suffix").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("waitlistTotal").assertTextEquals("Total Entrants: 1")
    }

    @Test
    fun ownerWaitlist_stillListsEntrantsAfterTheDraw() {
        val lottery = seedLottery(-10, -1, status = LotteryStatus.COMPLETED)
        seedEntry(lottery.lotteryId, "test-won-$suffix", LotteryEntryStatus.ACCEPTED)
        seedEntry(lottery.lotteryId, "test-lost-$suffix", LotteryEntryStatus.NOT_SELECTED)
        composeRule.setContent { WaitlistView(lotteryId = lottery.lotteryId) }

        composeRule.onNodeWithTag("waitlistTotal").assertTextEquals("Total Entrants: 2")
        composeRule.onNodeWithText("Status: Won and reserved").assertIsDisplayed()
        composeRule.onNodeWithText("Status: Not selected").assertIsDisplayed()
    }
}
