package com.example.cipherappproject.database.models

/*
--------------------------------------------------------------------------------------
Program Name: Reservation.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Reservation
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-03-27  Sofia      1.01     added skeleton
2026-03-10  Sofia      1.02     fixed getters and setters
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents a Reservation main attributes and status in relation with the Firebase database.
 *
 * A Reservation is meant to provide the main reservations details such as which user the campsite
 * was booked from, from which lottery the user won, the status of their reservation, which
 * may change, and when it was created.
 *
 * @property reservationId use to uniquely identify each reservation within the database
 * @property userId use to identify to which user comes from
 * @property campsiteId use to identify to which campsite is the reservation
 * @property lotteryId use to identify to which lottery the user won to have the chance to reserve
 * @property status determine if the reservation is canceled, past, active *
 * @property createdAt when reservation was made
 *
 *
 */
data class Reservation (
    private val reservationId: String = "",
    private val userId: String = "",
    private val campsiteId: String = "",
    private val lotteryId: String = "",
    private var status: String = "",
    private val createdAt: String = "",
    private val bookStartDate: String = "",
    private val bookEndDate: String = ""
){
    fun getBookEndDate(): String {
        return bookEndDate
    }

    fun getBookStartDate(): String {
        return bookStartDate
    }

    fun getCreatedAt(): String {
        return createdAt
    }

    fun getStatus(): String {
        return status
    }
    fun setStatus(updateStatus: String){
        status = updateStatus
    }

    fun getCampsiteId(): String {
        return campsiteId
    }

    fun getLotteryId(): String {
        return lotteryId
    }

    fun getReservationId(): String {
        return reservationId
    }

    fun getUserId(): String {
        return userId
    }


}