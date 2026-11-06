package com.example.cipherappproject

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cipherappproject.database.repositories.ReservationRepository
import java.util.Date

/*
--------------------------------------------------------------------------------------
Program Name: ReservationListModel.kt
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
2026-
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

//TODO: mod to account for db reservation, change to only handle the logic side of things?


class ReservationListModel(
    userID: String = "",
    // START OF AI GEN CONTENT
    // This section uses formatting generate by the following prompt:
    // Anthropic, Claude Opus 5.5, “clarify if i pull a copy of a list from a firebase database,
    // should that be in a model then the model call the view to update it? or should it only be
    // some times if the data needs to be sorted into different lists?”, 2026-11-05
    // "what if the repo already has getters and setters?"
    // ...
    // "if i don't want to keep updating it constantly by time is there a way that i can update it
    // only if i read that the date has passed?"
    private val repo: ReservationRepository = ReservationRepository()
) {
    // keeps state of last time updated status
    var lastTimeChecked by mutableStateOf(Date())

    /**
     * Makes the lists (reservations, activeReservations, pastReservations, cancelledReservations)
     * update when
     */
    fun refreshTime() { lastTimeChecked = Date() }

    // derivedStateOf will remember results and only re-filter when there is change
    // filter is read only and returns a copy of the original list with condition applied
    // lastTimeChecked lets derivedStateOf know it is time dependent

    // filtering user's list of reservations by that individual reservation status (active, past,
    // cancelled)
    private val reservations by derivedStateOf {
        lastTimeChecked
        repo.reservations.filter { it.getUserId() == userID } }
    private val activeReservations by derivedStateOf {
        lastTimeChecked
        reservations.filter { it.getStatus() == "active" } }
    private val pastReservations by derivedStateOf {
        lastTimeChecked
        reservations.filter { it.getStatus() == "past" } }
    private val cancelledReservation by derivedStateOf  {
        lastTimeChecked
        reservations.filter { it.getStatus() == "cancelled" } }

    private val nextExpiry by derivedStateOf { activeReservations.minOfOrNull { it.getBookEndDate() } }

    fun getNextExpiry(): Date? = nextExpiry
    // END OF AI GEN CONTENT
    enum class Status {ACTIVE, CANCELLED, PAST}

    fun getActiveReservations() = activeReservations
    fun getPastReservation() = pastReservations
    fun getCancelledReservations() = cancelledReservation



    // TODO: use derivatedstate for cancelled reservaation status update with the controller
    /**
     * Update status of current user's reservations for active
     */
    fun updateReservationListStatus() {

        for (r in reservations){
            r.getStatus() = when {

            }
        }
    }

    ////////////////////////////////////// UI STATE FUNC //////////////////////////////////////////

    /**
     * Holds the current status of current reservation, note it is
     * calculated each time it is read
     *
     */
    val status: Status get() = when {
        ReservationRepository.getCancel() -> Status.CANCELLED
        bookingEnd.before(Date()) -> Status.PAST
        else -> Status.ACTIVE.name()
    }


}