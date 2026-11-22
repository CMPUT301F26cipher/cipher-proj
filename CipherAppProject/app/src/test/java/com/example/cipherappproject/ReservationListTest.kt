package com.example.cipherappproject

import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.reservations.ReservationList
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Date

/**
 * US 01.06.01 / 02.07.01 (active), 01.06.02 / 02.07.02 (past), 02.07.03 (cancelled):
 * which list each reservation belongs to.
 */
class ReservationListTest {
    private val today = LocalDate.of(2026, 11, 21)

    private fun res(id: String, status: ReservationStatus, start: String, end: String, cancelledAtSec: Long? = null) =
        Reservation(
            reservationId = id, status = status.name, bookStartDate = start, bookEndDate = end,
            cancelledAt = cancelledAtSec?.let { Timestamp(Date(it * 1000)) }
        )

    private val upcoming = res("upcoming", ReservationStatus.ACTIVE, "2026-12-01", "2026-12-03")
    private val endsToday = res("endsToday", ReservationStatus.ACTIVE, "2026-11-19", "2026-11-21")
    private val ended = res("ended", ReservationStatus.ACTIVE, "2026-11-14", "2026-11-16")
    private val markedPast = res("markedPast", ReservationStatus.PAST, "2026-10-01", "2026-10-02")
    private val cancelled = res("cancelled", ReservationStatus.CANCELED, "2026-12-10", "2026-12-11", 100)
    private val rejected = res("rejected", ReservationStatus.REJECTED, "2026-12-20", "2026-12-21", 200)
    private val cancelledEndedStay = res("cancelledOld", ReservationStatus.CANCELED, "2026-11-01", "2026-11-02", 50)
    private val all = listOf(upcoming, endsToday, ended, markedPast, cancelled, rejected, cancelledEndedStay)

    @Test fun activeList_onlyConfirmedStaysThatHaventEnded() {
        assertEquals(listOf("upcoming", "endsToday"), ReservationList.activeOf(all, today).map { it.reservationId })
    }

    @Test fun pastList_endedStaysThatWentAhead() {
        assertEquals(listOf("ended", "markedPast"), ReservationList.pastOf(all, today).map { it.reservationId })
    }

    @Test fun cancelledList_includesRejected_newestFirst() {
        assertEquals(
            listOf("rejected", "cancelled", "cancelledOld"),
            ReservationList.cancelledOf(all).map { it.reservationId }
        )
    }

    @Test fun cancelledOrRejected_neverActiveOrPast() {
        val active = ReservationList.activeOf(all, today)
        val past = ReservationList.pastOf(all, today)
        listOf(cancelled, rejected, cancelledEndedStay).forEach {
            assertFalse(it in active)
            assertFalse(it in past)
        }
    }

    @Test fun eachReservationIsInExactlyOneList() {
        all.forEach { r ->
            val count = listOf(
                ReservationList.isActive(r, today),
                ReservationList.isPast(r, today),
                ReservationList.isCancelled(r)
            ).count { it }
            assertEquals("${r.reservationId} should be in one list", 1, count)
        }
    }

    @Test fun stayMovesFromActiveToPastTheDayAfterItEnds() {
        assertTrue(ReservationList.isActive(endsToday, today))
        assertFalse(ReservationList.isPast(endsToday, today))
        assertFalse(ReservationList.isActive(endsToday, today.plusDays(1)))
        assertTrue(ReservationList.isPast(endsToday, today.plusDays(1)))
    }

    @Test fun unreadableEndDate_isNotActive() {
        val broken = res("broken", ReservationStatus.ACTIVE, "2026-12-01", "not a date")
        assertTrue(ReservationList.hasEnded(broken, today))
        assertFalse(ReservationList.isActive(broken, today))
    }

    @Test fun emptyList_givesEmptyLists() {
        assertTrue(ReservationList.activeOf(emptyList(), today).isEmpty())
        assertTrue(ReservationList.pastOf(emptyList(), today).isEmpty())
        assertTrue(ReservationList.cancelledOf(emptyList()).isEmpty())
    }

    @Test fun entrantActiveList_soonestStayFirst() {
        val later = res("later", ReservationStatus.ACTIVE, "2027-07-13", "2027-07-14")
        val sooner = res("sooner", ReservationStatus.ACTIVE, "2027-07-11", "2027-07-12")
        assertEquals(listOf("sooner", "later"), ReservationList.soonestFirst(listOf(later, sooner)).map { it.reservationId })
    }

    @Test fun cancellationTime_fallsBackToCreatedAt() {
        val noDate = Reservation(createdAt = Timestamp(Date(5000)))
        assertEquals(Timestamp(Date(5000)), ReservationList.cancellationTime(noDate))
    }
}
