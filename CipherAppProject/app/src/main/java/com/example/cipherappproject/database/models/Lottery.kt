package com.example.cipherappproject.database.models

/*
--------------------------------------------------------------------------------------
Program Name: Lottery.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Lottery
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia      1.01     added skeleton
2026-11-03  Sofia      1.02     fixed getters and setters
2026-11-03  Sofia      1.03     fixed getters, getters, and default values
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents a Lottery main attributes and status in relation with the Firebase database.
 *
 * A Lottery is meant to specify to which owner the lottery was created/managed
 * and to which campsite can be applicable to reserve for the winners.
 * It also specifies the number of winners, the start and end date, maximum of participants (optional),
 * the status of the lottery, and when it was created for log proposes mainly.
 *
 * @property lotteryId use to uniquely identify each lottery within the database
 * @property ownerId use to identify to which owner is related to
 * @property campsiteIds use to identify to which campsite or campsites is related to
 * @property numWinners use to uniquely identify each profile within the database
 * @property startDate the opening date of the lottery
 * @property endDate the close date of the lottery
 * @property maxParticipants if owner decides a maximum number of participants
 * @property status use to help determine if the lottery is open, drawn, finished
 * @property createdAt when lottery was created
 *
 *
 */
data class Lottery (
    val lotteryId: String = "",
    val ownerId: String = "",
    // one lottery can be applicable to many lotteries
    val campsiteIds: MutableList<String> = mutableListOf(),
    var numWinners: Int = 0,
    var startDate: String = "", // Format can be yyyy-mm-hh-ss or even shorter
    var endDate: String = "",
    var maxParticipants: Int? = null, // is optional for the owner
    var status: String = "",
    val createdAt: String = "" // also the yyyy-mm-hh format --> useful for the notifications!
) {

    fun addCampsiteIds(campsiteId: String) {
        campsiteIds.add(campsiteId)
    }

    fun removeCampsiteIds(campsiteId: String) {
        campsiteIds.remove(campsiteId)
    }

}