package com.example.cipherappproject

import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.reservations.ReservationList
import org.junit.Assert.assertEquals
import org.junit.Test

class RejectReservationTest {
    @Test fun ownerListIsNewestReservationDateFirst() {
        val a = Reservation(reservationId = "a", bookStartDate = "2026-12-01")
        val b = Reservation(reservationId = "b", bookStartDate = "2027-01-15")
        val c = Reservation(reservationId = "c", bookStartDate = "2026-12-20")
        assertEquals(listOf("b", "c", "a"), ReservationList.newestFirst(listOf(a, b, c)).map { it.reservationId })
    }
}
