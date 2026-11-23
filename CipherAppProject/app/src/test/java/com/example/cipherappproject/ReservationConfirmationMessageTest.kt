package com.example.cipherappproject

import com.example.cipherappproject.database.models.Reservation
import org.junit.Assert.assertEquals
import org.junit.Test

class ReservationConfirmationMessageTest {
    private val controller = NotificationController()

    @Test fun messageHasCampsiteDatesAndReservationId() {
        val r = Reservation(reservationId = "res1", bookStartDate = "2026-12-01", bookEndDate = "2026-12-03")
        assertEquals("Pine Camp 2026-12-01 to 2026-12-03 res1", controller.reservationConfirmedMessage("Pine Camp", r))
    }

    @Test fun oneNightShowsSingleDate() {
        val r = Reservation(reservationId = "res2", bookStartDate = "2026-12-01", bookEndDate = "2026-12-01")
        assertEquals("Pine Camp 2026-12-01 res2", controller.reservationConfirmedMessage("Pine Camp", r))
    }

    @Test fun titleIsReservationConfirmation() {
        assertEquals("RESERVATION CONFIRMATION", NotificationController.RESERVATION_CONFIRMATION_TITLE)
    }
}
