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
2026-10-27  Sofia      1.01     added skeleton
2026-11-03  Sofia      1.02     fixed getters and setters
2026-11-06   Sofia     1.03     delete getters and setters. Set the attributes to public.
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
    val reservationId: String = "",
    val userId: String = "",
    val campsiteId: String = "",
    val lotteryId: String = "",
    var status: String = "",
    val createdAt: String = "",
    val bookStartDate: String = "",
    val bookEndDate: String = ""
)