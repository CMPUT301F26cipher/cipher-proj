package com.example.cipherappproject

import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.reservations.ReservationConfirmation
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

/** US 01.05.01: the reservation shown as confirmed after reserving through a lottery. */
class ReservationConfirmationTest {
    private fun res(id: String, user: String, lottery: String, createdSec: Long) =
        Reservation(reservationId = id, userId = user, lotteryId = lottery, createdAt = Timestamp(Date(createdSec * 1000)))

    @Test fun findsTheEntrantsReservationForThatLottery() {
        val list = listOf(res("mine", "u1", "L1", 10), res("otherUser", "u2", "L1", 20), res("otherLottery", "u1", "L2", 30))
        assertEquals("mine", ReservationConfirmation.find(list, "u1", "L1")?.reservationId)
    }

    @Test fun newestReservationWinsWhenThereAreSeveral() {
        val list = listOf(res("old", "u1", "L1", 10), res("new", "u1", "L1", 99))
        assertEquals("new", ReservationConfirmation.find(list, "u1", "L1")?.reservationId)
    }

    @Test fun nullWhenNoReservation() {
        assertNull(ReservationConfirmation.find(emptyList(), "u1", "L1"))
    }

    @Test fun nightsCountBothDates() {
        assertEquals(2, ReservationConfirmation.nights(Reservation(bookStartDate = "2026-11-22", bookEndDate = "2026-11-23")))
        assertEquals(1, ReservationConfirmation.nights(Reservation(bookStartDate = "2026-11-22", bookEndDate = "2026-11-22")))
        assertEquals(0, ReservationConfirmation.nights(Reservation(bookStartDate = "", bookEndDate = "2026-11-22")))
    }
}
