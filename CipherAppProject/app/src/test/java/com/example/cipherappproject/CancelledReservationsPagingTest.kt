package com.example.cipherappproject

import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.reservations.ReservationList
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test

class CancelledReservationsPagingTest {
    @Test fun pageCountIsAtLeastOne() {
        assertEquals(1, ReservationList.pageCount(0))
        assertEquals(1, ReservationList.pageCount(10))
        assertEquals(2, ReservationList.pageCount(11))
    }

    @Test fun pageOfSlicesAndClamps() {
        val items = (1..25).toList()
        assertEquals((1..10).toList(), ReservationList.pageOf(items, 0))
        assertEquals((21..25).toList(), ReservationList.pageOf(items, 2))
        assertEquals((21..25).toList(), ReservationList.pageOf(items, 99))
        assertEquals((1..10).toList(), ReservationList.pageOf(items, -3))
    }

    @Test fun cancellationTimeFallsBackToCreatedAt() {
        val created = Timestamp(1000, 0)
        val cancelled = Timestamp(2000, 0)
        assertEquals(created, ReservationList.cancellationTime(Reservation(createdAt = created)))
        assertEquals(cancelled, ReservationList.cancellationTime(Reservation(createdAt = created, cancelledAt = cancelled)))
    }
}
