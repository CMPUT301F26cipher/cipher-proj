package com.example.cipherappproject

import com.example.cipherappproject.database.models.Campsite
import java.util.Date

class ReservationModel(
    val campsite: Campsite,
    val bookingStart: Date,
    val bookingEnd: Date,
    val isCancelled: Boolean
    ) {
    enum class Status {ACTIVE, CANCELLED, PAST}

    /**
     * Holds the current status of current reservation, note it is
     * calculated each time it is read
     */
    val status: Status get() = when {
        isCancelled -> Status.CANCELLED
        bookingEnd.before(Date()) -> Status.PAST
        else -> Status.ACTIVE
    }
}