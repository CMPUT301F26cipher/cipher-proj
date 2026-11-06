package com.example.cipherappproject.database.models

import kotlin.String

/*
--------------------------------------------------------------------------------------
Program Name: LotteryEntry.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as LotteryEntry
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia      1.01     added skeleton
2026-11-03  Sofia      1.02     fixed getters and setters
2026-11-03  Sofia      1.03     fixed getters and setters names
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

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
    private val lotteryEntryId: String = "",
    private val lotteryId: String = "",
    private val userId: String = "",
    // registered, winner, offerPending, notSelected, accepted, declined, expired, withdrawn, etc
    private var status: String = "",
    // Defined after the list users are in random order so that we can pick the next person in the list if needed and know their status too
    private var positionLottery: Int? = null // means not added yet
){
    fun getLotteryEntryId(): String {
        return lotteryEntryId
    }

    fun getLotteryId(): String {
        return lotteryId
    }

    fun getUserId(): String {
        return userId
    }

    fun getStatus(): String {
        return status
    }

    fun getPositionLottery(): Int? {
        return positionLottery
    }
}
