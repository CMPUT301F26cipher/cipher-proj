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
import com.google.firebase.Timestamp
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
2026-11-21  Nethmini   1.02     Block ended stays, timestamp rejections, refund policy fix
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
         * True if a policy rules out refunds altogether, e.g. "No refunds". A policy that only
         * limits them in time ("No refunds after check-in") doesn't: the [FULL_REFUND_DAYS]
         * rule below already gives nothing once the stay is close or has started.
         */
        fun forbidsRefunds(policy: String): Boolean {
            val text = policy.lowercase()
            return text.contains("no refund") &&
                    listOf("after", "within", "once", "less than").none { text.contains(it) }
        }

        /**
         * Refund owed for cancelling [reservation] on [today], from the campsite's policies.
         * A policy ruling out refunds ([forbidsRefunds]) means nothing is refunded; otherwise the
         * whole total is refunded when cancelling at least [FULL_REFUND_DAYS] days before the
         * stay, else nothing.
         */
        fun refundFor(reservation: Reservation, policies: List<String>, today: LocalDate = LocalDate.now()): Double {
            if (policies.any { forbidsRefunds(it) }) return 0.0
            val start = DateUtils.parse(reservation.bookStartDate) ?: return 0.0
            return if (!today.plusDays(FULL_REFUND_DAYS).isAfter(start)) reservation.totalPrice else 0.0
        }
    }

    /**
     * Owner rejects an active reservation: marks it REJECTED (its dates open up again), then
     * notifies the entrant with the refund the campsite's policy gives. Like cancel, the refund
     * is calculated and reported only; no payment system exists yet.
     */
    fun reject(reservation: Reservation, onResult: (Result<Double>) -> Unit) {
        if (!ReservationList.isActive(reservation, LocalDate.now())) {
            onResult(Result.failure(OperationException("Only active reservations can be rejected.")))
            return
        }
        val policies = campsiteRepository.getCampsiteFromListById(reservation.campsiteId)?.policies ?: emptyList()
        val refund = refundFor(reservation, policies)
        // cancelledAt records when it was rejected, for the cancelled list's ordering and label
        val rejected = reservation.copy(status = ReservationStatus.REJECTED.name, cancelledAt = Timestamp.now())
        reservationRepository.updateReservation(rejected) { result ->
            result
                .onSuccess {
                    notificationController.notifyReservationRejected(rejected, refund)
                    onResult(Result.success(refund))
                }
                .onFailure { onResult(Result.failure(it)) }
        }
    }

    /**
     * Cancels an entrant's active reservation: marks it CANCELED in the database (the live
     * lists and calendars update from that), then tells the entrant and the campsite owner.
     * No payment system exists yet, so the refund is calculated and reported, not paid out.
     */
    fun cancel(reservation: Reservation, onResult: (Result<Double>) -> Unit) {
        // a stay that has already ended is a past reservation and can't be cancelled
        if (!ReservationList.isActive(reservation, LocalDate.now())) {
            onResult(Result.failure(OperationException("Only active reservations can be cancelled.")))
            return
        }
        val policies = campsiteRepository.getCampsiteFromListById(reservation.campsiteId)?.policies ?: emptyList()
        val refund = refundFor(reservation, policies)
        val cancelled = reservation.copy(status = ReservationStatus.CANCELED.name, cancelledAt = Timestamp.now())
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
