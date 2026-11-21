package com.example.cipherappproject

import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

// Writing tests for the lottery draw (US 02.05.01)

class LotteryDrawControllerTest {
    private val controller = LotteryDrawController()
    private val today = LocalDate.of(2026, 11, 19)

    private fun entry(id: String, status: String =
    LotteryEntryStatus.REGISTERED.name) = LotteryEntry(lotteryEntryId = id, lotteryId = "lottery1", userId =
    "user$id", status = status)

    private fun lottery(endDate: String, status: String = LotteryStatus.OPEN.name)
    = Lottery(lotteryId = "lottery1", ownerId = "owner1", numWinners = 2,
    endDate = endDate, status = status)

    private fun countStatus(entries: List<LotteryEntry>, status:
    LotteryEntryStatus) = entries.count { it.status == status.name }

    // picking winners test

    @Test
    fun drawsExactlyTheNumberOfWinners() {
        val entries = (1..5).map { entry("$it") }
        val drawn = controller.pickWinners(entries, 2, Random(42))

        assertEquals(5, drawn.size)
        assertEquals(2, countStatus(drawn, LotteryEntryStatus.WINNER))
        assertEquals(3, countStatus(drawn, LotteryEntryStatus.NOT_SELECTED))
    }

    @Test
    fun everyoneWinsWhenFewerEntrantsThanWinners() {
        val drawn = controller.pickWinners(listOf(entry("1")), 3, Random(42))

        assertEquals(1, drawn.size)
        assertEquals(LotteryEntryStatus.WINNER.name, drawn[0].status)
    }

    @Test
    fun noEntrantsGivesEmptyResult() {
        assertTrue(controller.pickWinners(emptyList(), 2, Random(42)).isEmpty())
    }

    @Test
    fun onlyRegisteredEntriesAreDrawn() {
        val entries = listOf(
            entry("1"),
            entry("2", LotteryEntryStatus.WITHDRAWN.name),
            entry("3")
        )
        val drawn = controller.pickWinners(entries, 1, Random(42))

        assertEquals(2, drawn.size)
        assertFalse(drawn.any { it.lotteryEntryId == "2" })
    }

    @Test
    fun positionsAreOneToNWithoutDuplicates() {
        val entries = (1..6).map { entry("$it") }
        val positions = controller.pickWinners(entries, 2, Random(42)).map {it.positionLottery }

        assertEquals((1..6).toList(), positions.sortedBy { it })
    }

    @Test
    fun winnersHaveTheLowestPositions() {
        val entries = (1..5).map { entry("$it") }
        val drawn = controller.pickWinners(entries, 2, Random(42))

        drawn.filter { it.status == LotteryEntryStatus.WINNER.name }
            .forEach { assertTrue(it.positionLottery!! <= 2) }

    }

    @Test
    fun drawKeepsEntryIdsAndUsers() {
          val entries = (1..4).map { entry("$it") }
          val drawn = controller.pickWinners(entries, 2, Random(42))

          assertEquals(entries.map { it.lotteryEntryId }.toSet(), drawn.map {it.lotteryEntryId }.toSet())
          drawn.forEach { assertEquals("user${it.lotteryEntryId}", it.userId) }
    }

    // isReadytoDraw tests

    @Test
    fun readyTheDayAfterRegistrationEnds() {
        assertTrue(controller.isReadyToDraw(lottery("2026-11-18"), today))
    }

    @Test
    fun notReadyOnTheLastRegistrationDay() {
        assertFalse(controller.isReadyToDraw(lottery("2026-11-19"), today))
    }

    @Test
    fun notReadyIfNotOpen() {
        assertFalse(controller.isReadyToDraw(lottery("2026-11-18", LotteryStatus.COMPLETED.name), today))
        assertFalse(controller.isReadyToDraw(lottery("2026-11-18", LotteryStatus.PROCESSING.name), today))
    }

    @Test
    fun notReadyWithBadEndDate() {
        assertFalse(controller.isReadyToDraw(lottery("not a date"), today))

    }
}