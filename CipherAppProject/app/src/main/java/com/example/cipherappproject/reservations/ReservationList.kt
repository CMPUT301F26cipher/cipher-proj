package com.example.cipherappproject.reservations

/*
--------------------------------------------------------------------------------------
Program Name: ReservationList.kt
Date Written: November 5, 2026
Author: Shira Li
Purpose: Reservation display screen for both past and active reservations, and for
both Entrants and Owners
US: 02.07.01, 02.07.02 (owners), 01.06.01, 01.06.02 (entrants)
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Shira      1.01     Modifying to fit DB variables
2026-11-06  Shira      1.02     Fixed Date mismatch types in updating list
2026-11-21  Nethmini   1.03     Pure list filters, rejected shown with cancelled
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.util.Date

// This file contains sections that uses formatting generate by the following prompt:
// Anthropic, Claude Opus 5.5, “clarify if i pull a copy of a list from a firebase database,
// should that be in a model then the model call the view to update it? or should it only be
// some times if the data needs to be sorted into different lists?”, 2026-11-05
// "what if the repo already has getters and setters?"
// "if i don't want to keep updating it constantly by time is there a way that i can update it
// only if i read that the date has passed?"

// Additional AI Contribution: Provided the state-tracking pattern using derivedStateOf paired with
// the lastTimeChecked watchdog trigger to auto-filter time-dependent lists.
class ReservationList(
    userID: String = "",
    isOwner: Boolean = false
) {
    // Shared repository instances (see Database_Documentation.md)
    private val reservationRepo = RepositoryDatabase.reservationRepository
    private val campsiteRepo = RepositoryDatabase.campsiteRepository

    ////////////////////////////////////// UI STATE //////////////////////////////////////////
    enum class Status {ACTIVE, CANCELLED, PAST}

    companion object {
        const val PAGE_SIZE = 10

        /** Reservations by the date reserved for (start date), newest first. */
        fun newestFirst(list: List<Reservation>): List<Reservation> =
            list.sortedByDescending { it.bookStartDate } // yyyy-MM-dd sorts correctly as text

        /** Reservations by start date, soonest first (the entrant's upcoming stays). */
        fun soonestFirst(list: List<Reservation>): List<Reservation> =
            list.sortedBy { it.bookStartDate }

        /** When the reservation was cancelled or rejected, falling back to when it was made. */
        fun cancellationTime(r: Reservation): Timestamp = r.cancelledAt ?: r.createdAt

        /** True if the stay ended before [today] (unparseable dates count as ended). */
        fun hasEnded(r: Reservation, today: LocalDate): Boolean =
            DateUtils.parse(r.bookEndDate)?.isBefore(today) ?: true

        /** Confirmed stays that haven't ended yet. */
        fun isActive(r: Reservation, today: LocalDate): Boolean =
            r.status == ReservationStatus.ACTIVE.name && !hasEnded(r, today)

        /** Stays that went ahead and are over (not cancelled or rejected). */
        fun isPast(r: Reservation, today: LocalDate): Boolean =
            r.status == ReservationStatus.PAST.name ||
                    (r.status == ReservationStatus.ACTIVE.name && hasEnded(r, today))

        /** Cancelled by the entrant or rejected by the owner (US 02.07.03). */
        fun isCancelled(r: Reservation): Boolean =
            r.status == ReservationStatus.CANCELED.name || r.status == ReservationStatus.REJECTED.name

        fun activeOf(list: List<Reservation>, today: LocalDate) = list.filter { isActive(it, today) }
        fun pastOf(list: List<Reservation>, today: LocalDate) = list.filter { isPast(it, today) }

        /** Cancelled and rejected reservations, most recently cancelled first. */
        fun cancelledOf(list: List<Reservation>) =
            list.filter { isCancelled(it) }.sortedByDescending { cancellationTime(it) }

        /** Number of pages for [itemCount] items (at least 1, so an empty list is "page 1 of 1"). */
        fun pageCount(itemCount: Int, pageSize: Int = PAGE_SIZE): Int =
            maxOf(1, (itemCount + pageSize - 1) / pageSize)

        /** The items on [page] (0-based); out-of-range pages are clamped to the last page. */
        fun <T> pageOf(items: List<T>, page: Int, pageSize: Int = PAGE_SIZE): List<T> {
            val p = page.coerceIn(0, pageCount(items.size, pageSize) - 1)
            return items.drop(p * pageSize).take(pageSize)
        }
    }


    ////////////////////////////////////// UPDATING LISTS //////////////////////////////////////////

    // keeps state of last time updated status
    var lastTimeChecked by mutableStateOf(LocalDate.now())
    /**
     * Makes the lists (reservations, activeReservations, pastReservations, cancelledReservations)
     * update when
     */
    fun refreshTime() { lastTimeChecked = LocalDate.now() }

    // derivedStateOf will remember results and only re-filter when there is change
    // filter is read only and returns a copy of the original list with condition applied
    // lastTimeChecked lets derivedStateOf know it is time dependent

    // filtering user's list of reservations by that individual reservation status (active, past,
    // cancelled)
    private val reservations by derivedStateOf {
        lastTimeChecked
        if (isOwner) {
            val campsiteIds = campsiteRepo.getCampsiteFromListByOwnerId(userID).map { it.campsiteId }
            reservationRepo.reservations.value.filter { it.campsiteId in campsiteIds }
        } else {
            reservationRepo.getReservationFromListByUserId(userID)
        }

    }
    private val _activeReservations by derivedStateOf {
        lastTimeChecked
        activeOf(reservations, lastTimeChecked)
    }
    private val _pastReservations by derivedStateOf {
        lastTimeChecked
        pastOf(reservations, lastTimeChecked)
    }

    private val _nextExpiry by derivedStateOf { _activeReservations.mapNotNull { DateUtils.parse(it.bookEndDate) }.minOrNull() }
    fun getNextExpiry(): LocalDate? = _nextExpiry

    // cancelled and rejected reservations, newest first (older ones without a cancel date use createdAt)
    private val _cancelledReservations by derivedStateOf { cancelledOf(reservations) }
    fun getCancelledReservations() = _cancelledReservations

    fun getActiveReservations() = _activeReservations
    fun getPastReservations() = _pastReservations



}

class ReservationListHelpers(
    private val reservation: Reservation,
    private val isOwner: Boolean
) {
    private val campsiteRepo = RepositoryDatabase.campsiteRepository
    private val profileRepo = RepositoryDatabase.profileRepository


    ////////////////////////////////////// CAMPSITES RESERVATION HELPERS //////////////////////////////////////////
    /**
     * @return bookingPeriod formatted string of booking period
     */
    fun formattedBookingPeriod(): String {
        return DateUtils.display(DateUtils.parse(reservation.bookStartDate)!!) + " - " + DateUtils.display(
            DateUtils.parse(reservation.bookEndDate)!!)
    }

    /**
     * Gets the name of the user who made the reservation
     */
    fun formattedUserName(): String {
        // first returns first campsite where conditions are true
        val campsite = campsiteRepo.getCampsiteFromListById(reservation.campsiteId)
        val nameId = if (isOwner) reservation.userId else campsite?.ownerId
        return nameId?.let { profileRepo.getProfileFromListById(it)?.name } ?: "Unknown"
    }

    fun formattedCampsiteName(): String {
        return campsiteRepo.getCampsiteFromListById(reservation.campsiteId)?.campsiteName ?: "Unknown campsite"
    }

    fun getCampsiteImg() = ""

}
