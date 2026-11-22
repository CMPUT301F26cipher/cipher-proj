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

/**
 * US 01.01.06 / 01.02.01: which lotteries an entrant can join from the campsite page.
 * US 02.09.01: who shows on the owner's waitlist.
 */
class WaitlistRulesTest {
    private val today = LocalDate.of(2026, 11, 21)

    private fun lottery(id: String, start: String, end: String, status: LotteryStatus = LotteryStatus.OPEN, campsites: List<String> = listOf("camp")) =
        Lottery(lotteryId = id, startDate = start, endDate = end, status = status.name, campsiteIds = campsites.toMutableList())

    @Test fun registrationOpen_whenTodayInsidePeriod_inclusive() {
        assertTrue(WaitlistController.isRegistrationOpen(lottery("a", "2026-11-20", "2026-11-30"), today))
        assertTrue(WaitlistController.isRegistrationOpen(lottery("b", "2026-11-21", "2026-11-21"), today))
    }

    @Test fun registrationClosed_beforeOrAfterPeriod() {
        assertFalse(WaitlistController.isRegistrationOpen(lottery("a", "2026-11-22", "2026-11-30"), today))
        assertFalse(WaitlistController.isRegistrationOpen(lottery("b", "2026-11-01", "2026-11-20"), today))
    }

    @Test fun registrationClosed_whenNotOpenStatus() {
        LotteryStatus.entries.filter { it != LotteryStatus.OPEN }.forEach {
            assertFalse(it.name, WaitlistController.isRegistrationOpen(lottery("a", "2026-11-01", "2026-12-01", it), today))
        }
    }

    @Test fun registrationClosed_whenDatesUnreadable() {
        assertFalse(WaitlistController.isRegistrationOpen(lottery("a", "", "2026-12-01"), today))
        assertFalse(WaitlistController.isRegistrationOpen(lottery("b", "2026-11-01", "soon"), today))
    }

    @Test fun openLotteriesFor_onlyThisCampsitesJoinableLotteries_closingSoonestFirst() {
        val lotteries = listOf(
            lottery("later", "2026-11-01", "2026-12-20"),
            lottery("sooner", "2026-11-19", "2026-11-25"),
            lottery("otherCampsite", "2026-11-01", "2026-12-20", campsites = listOf("other")),
            lottery("closed", "2026-11-01", "2026-11-10"),
            lottery("notYet", "2026-12-01", "2026-12-10"),
            lottery("drawn", "2026-11-01", "2026-12-20", LotteryStatus.COMPLETED),
            lottery("shared", "2026-11-01", "2026-12-01", campsites = listOf("other", "camp"))
        )
        assertEquals(
            listOf("sooner", "shared", "later"),
            WaitlistController.openLotteriesFor(lotteries, "camp", today).map { it.lotteryId }
        )
    }

    @Test fun openLotteriesFor_doesNotDependOnStayDates() {
        // the stay could be months later (the campsite's booking period); joining only needs open registration
        val lottery = lottery("may", "2026-11-15", "2026-11-30")
        assertEquals(listOf("may"), WaitlistController.openLotteriesFor(listOf(lottery), "camp", today).map { it.lotteryId })
    }

    @Test fun joinedEntrants_dropThoseWhoLeft_keepDrawResults() {
        val entries = LotteryEntryStatus.entries.map { LotteryEntry(lotteryEntryId = it.name, status = it.name) }
        val joined = WaitlistController().joinedOf(entries).map { it.status }
        assertFalse(LotteryEntryStatus.WITHDRAWN.name in joined)
        assertEquals(LotteryEntryStatus.entries.size - 1, joined.size)
    }

    @Test fun statusLabels_areReadable() {
        val c = WaitlistController()
        assertEquals("Waiting for the draw", c.statusLabel(LotteryEntry(status = LotteryEntryStatus.REGISTERED.name)))
        assertEquals("Won and reserved", c.statusLabel(LotteryEntry(status = LotteryEntryStatus.ACCEPTED.name)))
        assertEquals("Not selected", c.statusLabel(LotteryEntry(status = LotteryEntryStatus.NOT_SELECTED.name)))
        assertEquals("Won, declined", c.statusLabel(LotteryEntry(status = LotteryEntryStatus.DECLINED.name)))
    }
}
