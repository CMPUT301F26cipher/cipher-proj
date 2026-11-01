package com.example.cipherappproject.database.models
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
    val createdAt: String = ""
)