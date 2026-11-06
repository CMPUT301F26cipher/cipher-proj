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
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.database.repositories.ReservationRepository
import java.time.LocalDate
import java.util.Date

// This file contains sections that uses formatting generate by the following prompt:
// Anthropic, Claude Opus 5.5, “clarify if i pull a copy of a list from a firebase database,
// should that be in a model then the model call the view to update it? or should it only be
// some times if the data needs to be sorted into different lists?”, 2026-11-05
// "what if the repo already has getters and setters?"
// ...
// "if i don't want to keep updating it constantly by time is there a way that i can update it
// only if i read that the date has passed?"
class ReservationList(
    userID: String = ""
) {
    private val reservationRepo: ReservationRepository = ReservationRepository()

    ////////////////////////////////////// UI STATE //////////////////////////////////////////
    enum class Status {ACTIVE, CANCELLED, PAST}


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
        reservationRepo.reservations.filter { it.getUserId() == userID }
    }
    private val _activeReservations by derivedStateOf {
        lastTimeChecked
        reservations.filter { it.getStatus() == "active" && !DateUtils.parse(it.getBookEndDate())!!.isBefore(lastTimeChecked) }
    }
    private val _pastReservations by derivedStateOf {
        lastTimeChecked
        reservations.filter { it.getStatus() == "past" || (it.getStatus() != "cancelled" && DateUtils.parse(it.getBookEndDate())!!.isBefore(lastTimeChecked)) }
    }

    private val _nextExpiry by derivedStateOf { _activeReservations.minOfOrNull { DateUtils.parse(it.getBookEndDate())!! } }
    fun getNextExpiry(): LocalDate? = _nextExpiry

    fun getActiveReservations() = _activeReservations
    fun getPastReservations() = _pastReservations



    ////////////////////////////////////// PART 4 STUFF DO LATER //////////////////////////////////////////
    //    private val cancelledReservation by derivedStateOf  {
    //        lastTimeChecked
    //        reservations.filter { it.getStatus() == "cancelled" } }
    //
    //    fun getCancelledReservations() = cancelledReservation

    // TODO: use derivatedstate for cancelled reservaation status update with the controller

}

class ReservationListHelpers() {
    private val reservationRepo: ReservationRepository = ReservationRepository()
    private val campsiteRepo: CampsiteRepository = CampsiteRepository()
    private val profileRepo: ProfileRepository = ProfileRepository()

    //private val campsite = campsiteRepo.getCampsiteById()
    ////////////////////////////////////// CAMPSITES RESERVATION HELPERS //////////////////////////////////////////
    /**
     *
     * @return bookingPeriod formatted string of booking period
     */
    fun formattedBookingPeriod() = ""

    fun formattedUserName() = "Campsite"

    fun formattedCampsiteName() = "user"

    fun getCampsiteImg() = "Date"

}