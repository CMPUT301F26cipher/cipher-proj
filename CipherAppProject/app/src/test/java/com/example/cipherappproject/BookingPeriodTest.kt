package com.example.cipherappproject

import com.example.cipherappproject.ui.campsite.BookingPeriod
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BookingPeriodTest {
    private val today = LocalDate.of(2026, 11, 21)

    @Test fun validPeriodIsTrimmedAndKept() {
        val p = BookingPeriod.fromText(" 2026-12-01 ", "2026-12-31", today)
        assertEquals(BookingPeriod("2026-12-01", "2026-12-31"), p)
    }

    @Test fun singleDayPeriodIsAllowed() {
        BookingPeriod.fromText("2026-12-05", "2026-12-05", today)
    }

    @Test fun invalidDatesAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { BookingPeriod.fromText("", "2026-12-31", today) }
        assertThrows(IllegalArgumentException::class.java) { BookingPeriod.fromText("2026-12-01", "12/31/2026", today) }
    }

    @Test fun endBeforeStartIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { BookingPeriod.fromText("2026-12-31", "2026-12-01", today) }
    }

    @Test fun periodEntirelyInPastIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { BookingPeriod.fromText("2026-10-01", "2026-11-01", today) }
    }
}
