package com.example.cipherappproject

import androidx.compose.runtime.Composable
import java.util.Date

class ReservationModel(
    bookingStart: Date,
    bookingEnd: Date,
    ) {
    enum class Status {ACTIVE, CANCELLED, PAST}

    /**
     * Sort reservations into active and past and by
     *
     * @param
     */
    fun sortReservationStatus() {

    }

    // Check if entrants accepted booking
    @Composable
    fun checkAcceptedReservation() {

    }

    @Composable
    fun updateReservationStatus() {

        // checkAcceptedReservation


        // if current date ≥ booking end date, then add reservation to past reservation and
        // update reservation view to past

        // else: Add reservation to active, update reservation view to active
    }
}