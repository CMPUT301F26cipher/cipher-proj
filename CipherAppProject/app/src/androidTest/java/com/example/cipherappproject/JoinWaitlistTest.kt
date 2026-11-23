package com.example.cipherappproject

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.WaitlistController.JoinResult
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * US 01.01.06 / 01.02.01: an entrant can join a campsite lottery's waiting list.
 * Runs against the real Firestore like the other androidTests; cleans up after itself.
 */
@RunWith(AndroidJUnit4::class)
class JoinWaitlistTest {
    private val controller = WaitlistController()

    private fun waitUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 15_000
        while (!condition() && System.currentTimeMillis() < deadline) Thread.sleep(100)
        assert(condition()) { "Timed out waiting for the repository to sync" }
    }

    private fun join(lotteryId: String, userId: String): JoinResult {
        val latch = CountDownLatch(1)
        var outcome: JoinResult? = null
        controller.joinWaitlist(lotteryId, userId) { outcome = it; latch.countDown() }
        assert(latch.await(15, TimeUnit.SECONDS)) { "joinWaitlist never answered" }
        return outcome!!
    }

    @Test
    fun entrantJoinsOnceAndCannotJoinTwice() {
        val db = Firebase.firestore
        val lotteryId = "test-${UUID.randomUUID()}"
        val userId = "test-entrant-${UUID.randomUUID()}"
        val campsiteId = "test-campsite"
        val lotteryRef = db.collection("lotteries").document(lotteryId)

        try {
            Tasks.await(
                lotteryRef.set(
                    Lottery(
                        lotteryId = lotteryId,
                        ownerId = "test-owner",
                        campsiteIds = mutableListOf(campsiteId),
                        startDate = java.time.LocalDate.now().minusDays(1).toString(),
                        endDate = java.time.LocalDate.now().plusDays(7).toString(),
                        status = LotteryStatus.OPEN.name
                    )
                ), 15, TimeUnit.SECONDS
            )
            waitUntil {
                RepositoryDatabase.lotteryRepository.getLotteryFromListById(lotteryId) != null &&
                        RepositoryDatabase.lotteryEntryRepository.isLotteriesEntriesLoaded.value
            }

            // The campsite page lists this lottery: its registration period covers today
            assertEquals(true, controller.getOpenLotteries(campsiteId).any { it.lotteryId == lotteryId })

            assertEquals(JoinResult.JOINED, join(lotteryId, userId))

            // Entry shows up in "My Lotteries" (live list), so a second join is refused
            waitUntil {
                RepositoryDatabase.lotteryEntryRepository
                    .getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, userId) != null
            }
            assertNotNull(
                RepositoryDatabase.lotteryEntryRepository
                    .getLotteryEntriesFromListByUserId(userId).firstOrNull()
            )
            assertEquals(JoinResult.ALREADY_JOINED, join(lotteryId, userId))

            // US 01.01.07: leaving frees the spot and lets the entrant rejoin
            val entry = RepositoryDatabase.lotteryEntryRepository
                .getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, userId)!!
            val left = CountDownLatch(1)
            var leftOk = false
            controller.leaveWaitlist(entry) { leftOk = it; left.countDown() }
            assert(left.await(15, TimeUnit.SECONDS))
            assertEquals(true, leftOk)
            waitUntil {
                RepositoryDatabase.lotteryEntryRepository
                    .getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, userId)?.status ==
                        com.example.cipherappproject.database.enums.LotteryEntryStatus.WITHDRAWN.name
            }
            assertEquals(0, LotteryController().getWaitlistSize(lotteryId))
            assertEquals(JoinResult.JOINED, join(lotteryId, userId))
        } finally {
            val entries = Tasks.await(lotteryRef.collection("entries").get(), 15, TimeUnit.SECONDS)
            entries.documents.forEach { Tasks.await(it.reference.delete(), 15, TimeUnit.SECONDS) }
            Tasks.await(lotteryRef.delete(), 15, TimeUnit.SECONDS)
        }
    }
}
