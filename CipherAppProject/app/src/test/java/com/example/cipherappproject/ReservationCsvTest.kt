package com.example.cipherappproject

import com.example.cipherappproject.reservations.ReservationCsv
import com.example.cipherappproject.database.models.Reservation
import org.junit.Assert.assertEquals
import org.junit.Test


// appying some unit tests for producing csv
class ReservationCsvTest {
    @Test
    fun emptyListGivesHeaderOnly() {
        val csv = ReservationCsv.build(emptyList()) { it }
        assertEquals(ReservationCsv.HEADER.joinToString(","), csv)
    }
    @Test
    fun oneReservationGivesHeaderAndRow() {
        val r = Reservation(reservationId = "r1", campsiteId = "c1", status = "ACTIVE",
            bookStartDate = "2026-12-01", bookEndDate = "2026-12-03",
            pricePerNight = 12.5, totalPrice = 37.5)
        val lines = ReservationCsv.build(listOf(r)) { "Pine Camp" }.lines()
        assertEquals(2, lines.size)
        assertEquals("r1,Pine Camp,2026-12-01,2026-12-03,ACTIVE,12.50,37.50", lines[1])
    }

    /** Commas, quotes and newlines are quoted so the columns don't shift. */
    @Test
    fun specialCharactersAreEscaped() {
        assertEquals("\"Lake, North\"", ReservationCsv.escape("Lake, North"))
        assertEquals("\"say \"\"hi\"\"\"", ReservationCsv.escape("say \"hi\""))
        assertEquals("\"line1\nline2\"", ReservationCsv.escape("line1\nline2"))
        assertEquals("plain", ReservationCsv.escape("plain"))
    }
}