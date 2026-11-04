package com.example.cipherappproject.database.models

import kotlin.String

/**
 * Represents a LotteryEntry main attributes and status in relation with the Firebase database.
 *
 * A LotteryEntry is meant to act as a list of records such that we know all participants and
 * their entries status, after the list entries are order in randomly.
 * This also includes their position so that if a participant reservation is either
 * canceled or not taken within eh time frame, the opportunity to reserve will be
 * passed onto the next person in the list.
 *
 * @property lotteryId use to uniquely identify each lottery within the database
 * @property userId use to identify to which user/participant details from the lottery results
 * @property status to help determine if the user is registered, winner, offerPending, notSelected, accepted, declined, expired, withdrawn
 * @property positionLottery defined after the randomization so that we can pick the next person in the list if needed
 *
 *
 */
data class LotteryEntry (
    val lotteryId: String = "",
    val userId: String = "",
    // registered, winner, offerPending, notSelected, accepted, declined, expired, withdrawn, etc
    var status: String = "registered",
    // Defined after the list users are in random order so that we can pick the next person in the list if needed and know their status too
    var positionLottery: Int = -1 // means not added yet
)