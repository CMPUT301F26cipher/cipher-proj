
package com.example.cipherappproject.reservations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.database.repositories.ReservationRepository

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
2026-11-20  Shira      1.03     Added functions (
2026-11-19  Shira      1.03     Added functions (changeScreenStatus)
2026-11-18  Shira      1.02     Added functions (isReservationsLoaded, getReservationListByStatus)
2026-11-06  Shira      1.01     Moved button Status related code from ReservationList.kt
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

class ReservationController (
    private val reservationRepo: ReservationRepository = RepositoryDatabase.reservationRepository,
){
    fun isReservationsLoaded() = reservationRepo.isReservationsLoaded.value
    enum class ScreenStatus {ACTIVE, CANCELLED, PAST}         // FOR LOCAL UI STATE
    /** Holds the current button status of which filtered reservation list to be displayed */
    private val _currentStatus = mutableStateOf(ScreenStatus.ACTIVE)
    val currentStatus: State<ScreenStatus>
        get() = _currentStatus

    fun changeScreenStatus(newStatus: ScreenStatus) {
        _currentStatus.value = newStatus
    }

    /**
     * create local list of firebase reservations that have match requested reservation's status
     * and filters list to only include current user's reservation
     *
     * @param status reservation's status
     * @param userId current user ID
     *
     * @return reservationList
     */
    fun getReservationListByStatus(status: ReservationStatus, userId: String = ""): List<Reservation> {
        if (!isReservationsLoaded()) return emptyList<Reservation>()

        return reservationRepo.getReservationsFromListByStatus(status.name).filter { it.userId == userId}
    }

    /**
     * update local user reservation lists for when booking status change and when new reservation
     * is added
     */
    fun updateReservations() {

    }



    // Log.v(reservationRepo.isReservationsLoaded)





}

