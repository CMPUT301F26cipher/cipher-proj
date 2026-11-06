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
2026-11-03  Sofia      1.02     fixed getters, getters, and default values
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
    private val lotteryId: String = "",
    private val ownerId: String = "",
    // one lottery can be applicable to many lotteries
    private val campsiteIds: MutableList<String> = mutableListOf(),
    private var numWinners: Int = 0,
    private var startDate: String = "", // Format can be yyyy-mm-hh-ss or even shorter
    private var endDate: String = "",
    private var maxParticipants: Int? = null, // is optional for the owner
    private var status: String = "",
    private val createdAt: String = "" // also the yyyy-mm-hh format --> useful for the notifications!
) {
    fun getLotteryId(): String {
        return lotteryId
    }

    fun getLotteryOwnerId(): String {
        return ownerId
    }

    fun getLotteryCampsitesId(): List<String> {
        return campsiteIds
    }

    fun getLotteryNumWinners(): Int {
        return numWinners
    }

    fun setLotteryNumWinner(number: Int) {
        numWinners = number
    }

    fun getLotteryStartDate(): String {
        return startDate
    }

    fun setLotteryStartDate(date: String) {
        startDate = date
    }

    fun getLotteryEndDate(): String {
        return endDate
    }

    fun setLotteryEndDate(date: String) {
        endDate = date
    }

    fun getLotteryMaxParticipants(): Int? {
        return maxParticipants
    }

    fun setLotteryMaxParticipants(numParticipants: Int?) {
        maxParticipants = numParticipants
    }

    fun getLotteryStatus(): String {
        return status
    }

    fun setLotteryStatus(newStatus: String){
        status = newStatus
    }

    fun getLotteryCreatedAt(): String {
        return createdAt
    }
}