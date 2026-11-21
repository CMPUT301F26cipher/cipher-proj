package com.example.cipherappproject.reservations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.NotificationController
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.exceptions.OperationException
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.reservations.ReservationList.Status
import java.time.LocalDate

/*
--------------------------------------------------------------------------------------
Program Name: ReservationController.kt
Date Written: November 5, 2026
Author: Shira Li
Purpose: Reservation display screen for both past and active reservations, and for
both Entrants and Owners
US: 02.07.01, 02.07.02 (owners), 01.06.01, 01.06.02 (entrants)
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-06  Shira      1.01     Moved button Status related code from ReservationList.kt
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

class ReservationController {
    private val reservationRepository get() = RepositoryDatabase.reservationRepository
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository
    private val notificationController = NotificationController()

    companion object {
        /** Full refund if cancelled at least this many days before the stay starts. */
        const val FULL_REFUND_DAYS = 7L

        /**
         * Refund owed for cancelling [reservation] on [today], from the campsite's policies.
         * A policy saying "no refund(s)" means nothing is refunded; otherwise the whole total is
         * refunded when cancelling at least [FULL_REFUND_DAYS] days before the stay, else nothing.
         */
        fun refundFor(reservation: Reservation, policies: List<String>, today: LocalDate = LocalDate.now()): Double {
            if (policies.any { it.contains("no refund", ignoreCase = true) }) return 0.0
            val start = DateUtils.parse(reservation.bookStartDate) ?: return 0.0
            return if (!today.plusDays(FULL_REFUND_DAYS).isAfter(start)) reservation.totalPrice else 0.0
        }
    }

    /**
     * Cancels an entrant's active reservation: marks it CANCELED in the database (the live
     * lists and calendars update from that), then tells the entrant and the campsite owner.
     * No payment system exists yet, so the refund is calculated and reported, not paid out.
     */
    fun cancel(reservation: Reservation, onResult: (Result<Double>) -> Unit) {
        if (reservation.status != ReservationStatus.ACTIVE.name) {
            onResult(Result.failure(OperationException("Only active reservations can be cancelled.")))
            return
        }
        val policies = campsiteRepository.getCampsiteFromListById(reservation.campsiteId)?.policies ?: emptyList()
        val refund = refundFor(reservation, policies)
        val cancelled = reservation.copy(status = ReservationStatus.CANCELED.name)
        reservationRepository.updateReservation(cancelled) { result ->
            result
                .onSuccess {
                    notificationController.notifyReservationCancelled(cancelled, refund)
                    notificationController.notifyOwnerReservationCancelled(cancelled)
                    onResult(Result.success(refund))
                }
                .onFailure { onResult(Result.failure(it)) }
        }
    }

    /** Holds the current button status of which reservation list to be displayed */
    var currentStatus: Status by mutableStateOf(Status.ACTIVE)


}
