package com.example.cipherappproject

import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.reservations.ReservationController
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ReservationRefundTest {
    private val today = LocalDate.of(2026, 11, 21)
    private val r = Reservation(bookStartDate = "2026-12-01", bookEndDate = "2026-12-03", totalPrice = 150.0)

    @Test fun fullRefundWhenWellBeforeStay() {
        assertEquals(150.0, ReservationController.refundFor(r, emptyList(), today), 0.0)
    }

    @Test fun noRefundWhenTooCloseToStay() {
        assertEquals(0.0, ReservationController.refundFor(r, emptyList(), LocalDate.of(2026, 11, 28)), 0.0)
    }

    @Test fun fullRefundExactlySevenDaysBefore() {
        assertEquals(150.0, ReservationController.refundFor(r, emptyList(), LocalDate.of(2026, 11, 24)), 0.0)
    }

    @Test fun noRefundPolicyWins() {
        assertEquals(0.0, ReservationController.refundFor(r, listOf("No refunds after check-in"), today), 0.0)
    }
}
