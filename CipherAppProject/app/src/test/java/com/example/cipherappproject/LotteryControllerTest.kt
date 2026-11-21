package com.example.cipherappproject

import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Tests the parts of LotteryController that don't need Firestore
 * (US 02.04.01, 02.04.02, 02.04.05, 02.04.06, 02.04.07).
 */
class LotteryControllerTest {
    private val controller = LotteryController()
    private val today = LocalDate.of(2026, 11, 19)

    private fun validInput() = LotteryFormInput(
        campsiteIds = setOf("siteA"),
        registrationStart = "2026-11-20",
        registrationEnd = "2026-11-30",
        numWinners = "2",
        capacity = ""
    )

    // ---------- create (02.04.01 / 02.04.02 / 02.04.05 / 02.04.07) ----------

    @Test
    fun validLotteryCanBeCreated() {
        assertNull(controller.validate(validInput(), today))

        val lottery = controller.createLottery("owner1", listOf("siteA"), "2026-11-20", "2026-11-30", 2)
        assertEquals("owner1", lottery.ownerId)
        assertEquals(listOf("siteA"), lottery.campsiteIds)
        assertEquals(2, lottery.numWinners)
        assertEquals(LotteryStatus.OPEN.name, lottery.status)
    }

    @Test
    fun multipleCampsitesAreKept() {
        val input = validInput().copy(campsiteIds = setOf("siteA", "siteB"))
        assertNull(controller.validate(input, today))

        val lottery = controller.createLottery("owner1", listOf("siteA", "siteB"), "2026-11-20", "2026-11-30", 1)
        assertEquals(listOf("siteA", "siteB"), lottery.campsiteIds)
    }

    @Test
    fun noCampsiteIsRejected() {
        assertNotNull(controller.validate(validInput().copy(campsiteIds = emptySet()), today))
        assertThrows(IllegalArgumentException::class.java) {
            controller.createLottery("owner1", emptyList(), "2026-11-20", "2026-11-30", 1)
        }
    }

    @Test
    fun invalidRegistrationPeriodIsRejected() {
        assertFalse(controller.isValidRegistrationPeriod("2026/11/20", "2026-11-30"))
        assertFalse(controller.isValidRegistrationPeriod("2026-12-05", "2026-11-30"))
        assertTrue(controller.isValidRegistrationPeriod("2026-11-30", "2026-11-30"))
        assertNotNull(controller.validate(validInput().copy(registrationEnd = "2026-11-10"), today))
    }

    @Test
    fun registrationEndingInThePastIsRejected() {
        val input = validInput().copy(registrationStart = "2026-11-01", registrationEnd = "2026-11-18")
        assertEquals("Registration cannot end in the past", controller.validate(input, today))
    }

    @Test
    fun nonPositiveWinnerCountIsRejected() {
        assertFalse(controller.isValidWinnerCount(0))
        assertFalse(controller.isValidWinnerCount(-1))
        for (bad in listOf("", "0", "-2", "1.5", "abc")) {
            assertNotNull(bad, controller.validate(validInput().copy(numWinners = bad), today))
        }
    }

    @Test
    fun nullCapacityMeansUnlimited() {
        val lottery = controller.createLottery("owner1", listOf("siteA"), "2026-11-20", "2026-11-30", 2, null)
        assertEquals(LotteryConstants.UNLIMITED_PARTICIPANTS, lottery.maxParticipants)
        assertNull(controller.parseCapacity("  "))
    }

    @Test
    fun invalidCapacityIsRejected() {
        assertNotNull(controller.validate(validInput().copy(capacity = "1"), today)) // below winners
        assertNotNull(controller.validate(validInput().copy(capacity = "abc"), today))
        assertNotNull(controller.validate(validInput().copy(capacity = "-5"), today))
        assertNull(controller.validate(validInput().copy(capacity = "2"), today))
        assertNull(controller.validate(validInput().copy(capacity = "50"), today))
    }

    // ---------- waitlist capacity enforcement (02.04.07) ----------

    @Test
    fun unlimitedWaitlistIsNeverFull() {
        assertFalse(controller.isFull(null, 1000))
        assertFalse(controller.isFull(LotteryConstants.UNLIMITED_PARTICIPANTS, 1000))
    }

    @Test
    fun limitedWaitlistIsFullAtCapacity() {
        assertFalse(controller.isFull(3, 2))
        assertTrue(controller.isFull(3, 3))
        assertTrue(controller.isFull(3, 4))
    }

    @Test
    fun capacityCannotDropBelowCurrentWaitlist() {
        assertNotNull(controller.capacityBelowWaitlistError(2, 3))
        assertNull(controller.capacityBelowWaitlistError(3, 3))
    }

    // ---------- edit (02.04.06) ----------

    private val saved = Lottery(
        lotteryId = "lot1",
        ownerId = "owner1",
        campsiteIds = mutableListOf("siteA"),
        numWinners = 3,
        startDate = "2026-11-01",
        endDate = "2026-11-30",
        maxParticipants = LotteryConstants.UNLIMITED_PARTICIPANTS,
        status = LotteryStatus.OPEN.name
    )

    @Test
    fun formIsFilledFromSavedLottery() {
        val input = controller.toFormInput(saved)
        assertEquals(setOf("siteA"), input.campsiteIds)
        assertEquals("2026-11-01", input.registrationStart)
        assertEquals("2026-11-30", input.registrationEnd)
        assertEquals("3", input.numWinners)
        assertEquals("", input.capacity) // unlimited shows as blank
    }

    @Test
    fun changesKeepIdOwnerAndStatus() {
        val input = controller.toFormInput(saved).copy(
            campsiteIds = setOf("siteA", "siteC"),
            registrationEnd = "2026-12-10",
            numWinners = "4",
            capacity = "20"
        )
        val updated = controller.applyChanges(saved, input)
        assertEquals("lot1", updated.lotteryId)
        assertEquals("owner1", updated.ownerId)
        assertEquals(LotteryStatus.OPEN.name, updated.status)
        assertEquals(listOf("siteA", "siteC"), updated.campsiteIds)
        assertEquals("2026-12-10", updated.endDate)
        assertEquals(4, updated.numWinners)
        assertEquals(20, updated.maxParticipants)

        val cleared = controller.applyChanges(updated, controller.toFormInput(updated).copy(capacity = ""))
        assertEquals(LotteryConstants.UNLIMITED_PARTICIPANTS, cleared.maxParticipants)
    }

    @Test
    fun onlyOpenLotteriesCanBeEdited() {
        assertTrue(controller.canEdit(saved))
        assertFalse(controller.canEdit(saved.copy(status = LotteryStatus.COMPLETED.name)))
        assertNotNull(controller.updateLottery(saved.copy(status = LotteryStatus.COMPLETED.name), validInput()))
    }

    // ---------- view / filter ----------

    @Test
    fun filterByStatus() {
        val drawn = saved.copy(lotteryId = "lot2", status = LotteryStatus.COMPLETED.name)
        val all = listOf(saved, drawn)
        assertEquals(all, controller.filterByStatus(all, null))
        assertEquals(listOf(saved), controller.filterByStatus(all, LotteryStatus.OPEN.name))
        assertEquals(listOf(drawn), controller.filterByStatus(all, LotteryStatus.COMPLETED.name))
        assertTrue(controller.filterByStatus(all, LotteryStatus.CANCELLED.name).isEmpty())
    }
}
