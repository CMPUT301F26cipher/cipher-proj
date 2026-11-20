package com.example.cipherappproject

import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests a winning entrant's campsite/date selection (US 01.04.01, 01.04.02, 01.04.03).
 * Uses the overloads that take the available dates directly, so no Firestore is needed.
 */
class LotteryBookingControllerTest {
    private val controller = LotteryBookingController()

    private val lottery = Lottery(
        lotteryId = "lot1",
        ownerId = "owner1",
        campsiteIds = mutableListOf("siteA", "siteB")
    )
    private val available = listOf("2027-05-01", "2027-05-02", "2027-05-03", "2027-05-06")

    private fun entry(status: LotteryEntryStatus) =
        LotteryEntry(lotteryEntryId = "e1", lotteryId = "lot1", userId = "u1", status = status.name)

    @Test
    fun ineligibleEntrantCannotBook() {
        assertTrue(controller.canBook(entry(LotteryEntryStatus.WINNER)))
        for (status in LotteryEntryStatus.entries - LotteryEntryStatus.WINNER) {
            assertFalse(status.name, controller.canBook(entry(status)))
        }
        assertFalse(controller.canBook(null))
    }

    @Test
    fun multipleCampsiteLotteryProvidesAllChoices() {
        assertEquals(listOf("siteA", "siteB"), controller.getCampsiteChoices(lottery))
    }

    @Test
    fun campsiteOutsideLotteryCannotBeSelected() {
        controller.selectCampsite(lottery, "siteZ")
        assertNull(controller.getSelectedCampsiteId())
        assertEquals("Choose a campsite.", controller.validateSelection(lottery, available))
    }

    @Test
    fun availableNightCanBeSelected() {
        controller.selectCampsite(lottery, "siteA")
        assertTrue(controller.selectDate("2027-05-02", available))
        assertTrue(controller.selectDate("2027-05-01", available)) // extends backwards
        assertTrue(controller.selectDate("2027-05-03", available)) // extends forwards
        assertEquals(setOf("2027-05-01", "2027-05-02", "2027-05-03"), controller.getSelectedDates())
        assertEquals(Pair("2027-05-01", "2027-05-03"), controller.getStay())
        assertNull(controller.validateSelection(lottery, available))
    }

    @Test
    fun unavailableNightCannotBeSelected() {
        controller.selectCampsite(lottery, "siteA")
        assertFalse(controller.selectDate("2027-05-04", available))
        assertTrue(controller.getSelectedDates().isEmpty())
    }

    @Test
    fun nonConsecutiveNightCannotBeSelected() {
        controller.selectCampsite(lottery, "siteA")
        controller.selectDate("2027-05-03", available)
        assertFalse(controller.selectDate("2027-05-06", available))
        assertFalse(controller.selectDate("2027-05-01", available))
        assertEquals(setOf("2027-05-03"), controller.getSelectedDates())
    }

    @Test
    fun onlyEndsOfSelectionCanBeRemoved() {
        controller.selectCampsite(lottery, "siteA")
        controller.selectDate("2027-05-01", available)
        controller.selectDate("2027-05-02", available)
        controller.selectDate("2027-05-03", available)
        assertFalse(controller.deselectDate("2027-05-02"))
        assertTrue(controller.deselectDate("2027-05-01"))
        assertEquals(setOf("2027-05-02", "2027-05-03"), controller.getSelectedDates())
    }

    @Test
    fun selectionNoLongerAvailableIsRejected() {
        controller.selectCampsite(lottery, "siteA")
        controller.selectDate("2027-05-01", available)
        // Someone else booked May 1 meanwhile
        assertNotNull(controller.validateSelection(lottery, available - "2027-05-01"))
        assertNotNull(controller.validateSelection(lottery, null)) // still loading
    }

    @Test
    fun emptySelectionIsRejected() {
        controller.selectCampsite(lottery, "siteA")
        assertEquals("Select at least one date.", controller.validateSelection(lottery, available))
        assertNull(controller.getStay())
    }

    // ---------- calendar range picking ----------

    @Test
    fun calendarRangeSelectsEveryNight() {
        controller.selectCampsite(lottery, "siteA")
        assertNull(controller.selectRange("2027-05-01", "2027-05-03", available))
        assertEquals(setOf("2027-05-01", "2027-05-02", "2027-05-03"), controller.getSelectedDates())
        assertNull(controller.validateSelection(lottery, available))
    }

    @Test
    fun calendarStartOnlyIsOneNight() {
        controller.selectCampsite(lottery, "siteA")
        assertNull(controller.selectRange("2027-05-06", null, available))
        assertEquals(Pair("2027-05-06", "2027-05-06"), controller.getStay())
    }

    @Test
    fun calendarRangeOverUnavailableNightIsRejected() {
        controller.selectCampsite(lottery, "siteA")
        controller.selectRange("2027-05-01", "2027-05-02", available)
        // May 4 and 5 are not available
        assertNotNull(controller.selectRange("2027-05-03", "2027-05-06", available))
        assertTrue(controller.getSelectedDates().isEmpty())
    }

    @Test
    fun calendarRangeNeedsCampsiteAndValidOrder() {
        assertNotNull(controller.selectRange("2027-05-01", "2027-05-02", available)) // no campsite
        controller.selectCampsite(lottery, "siteA")
        assertNotNull(controller.selectRange("2027-05-03", "2027-05-01", available))
        assertNotNull(controller.selectRange("bad", null, available))
    }

    @Test
    fun utcMillisRoundTrip() {
        val date = java.time.LocalDate.of(2027, 5, 1)
        assertEquals(date, DateUtils.fromUtcMillis(DateUtils.toUtcMillis(date)))
    }

    @Test
    fun switchingCampsitesClearsSelectedNights() {
        controller.selectCampsite(lottery, "siteA")
        controller.selectDate("2027-05-01", available)
        controller.selectCampsite(lottery, "siteB")
        assertEquals("siteB", controller.getSelectedCampsiteId())
        assertTrue(controller.getSelectedDates().isEmpty())

        // Re-selecting the same campsite keeps the selection
        controller.selectDate("2027-05-01", available)
        controller.selectCampsite(lottery, "siteB")
        assertEquals(setOf("2027-05-01"), controller.getSelectedDates())
    }
}
