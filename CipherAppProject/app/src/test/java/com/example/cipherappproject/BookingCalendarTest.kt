package com.example.cipherappproject

import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.reservations.BookingCalendar
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookingCalendarTest {
    private val dec = YearMonth.of(2026, 12)
    private fun res(camp: String, start: String, end: String, status: String = "ACTIVE") =
        Reservation(campsiteId = camp, bookStartDate = start, bookEndDate = end, status = status)

    @Test fun stayCoversStartAndEndInclusive() {
        val c = BookingCalendar.bookedCounts(listOf(res("a", "2026-12-01", "2026-12-03")), setOf("a"), dec)
        assertEquals(1, c[LocalDate.of(2026, 12, 1)])
        assertEquals(1, c[LocalDate.of(2026, 12, 3)])
        assertNull(c[LocalDate.of(2026, 12, 4)])
    }

    @Test fun staySpanningMonthsIsClippedToTheMonth() {
        val c = BookingCalendar.bookedCounts(listOf(res("a", "2026-11-29", "2027-01-02")), setOf("a"), dec)
        assertEquals(31, c.size)
    }

    @Test fun cancelledAndRejectedDoNotCount() {
        val list = listOf(res("a", "2026-12-01", "2026-12-02", "CANCELED"), res("a", "2026-12-05", "2026-12-06", "REJECTED"))
        assertEquals(0, BookingCalendar.bookedCounts(list, setOf("a"), dec).size)
    }

    @Test fun countsCampsitesNotReservationsAndIgnoresOthers() {
        val list = listOf(res("a", "2026-12-01", "2026-12-01"), res("b", "2026-12-01", "2026-12-01"), res("z", "2026-12-01", "2026-12-01"))
        assertEquals(2, BookingCalendar.bookedCounts(list, setOf("a", "b"), dec)[LocalDate.of(2026, 12, 1)])
    }
}
