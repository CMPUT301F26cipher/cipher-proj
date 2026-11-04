package com.example.cipherappproject.database.models
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
 * @property numWinner use to uniquely identify each profile within the database
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
    val campsiteIds: List<String> = emptyList(),
    var numWinner: Int = 0,
    var startDate: String = "", // Format can be yyyy-mm-hh-ss or even shorter
    var endDate: String = "",
    var maxParticipants: Int, // is optional for the owner
    val status: String,
    val createdAt: String = "" // also the yyyy-mm-hh format --> useful for the notifications!
) {
        /** Returns the lottery ID. */
        fun getLotteryId(): String {
            return lotteryId
        }

        /** Returns the owner ID. */
        fun getOwnerId(): String {
            return ownerId
        }

        /** Returns the campsite IDs included in this lottery. */
        fun getCampsiteIds(): List<String> {
            return campsiteIds
        }

        /** Returns the number of winners. */
        fun getNumWinner(): Int {
            return numWinner
        }

        /** Sets a positive winner count. */
        fun setNumWinner(number: Int) {
            // TODO: Validate and update numWinner
        }

        /** Returns the registration start date. */
        fun getStartDate(): String {
            return startDate
        }

        /** Returns the registration end date. */
        fun getEndDate(): String {
            return endDate
        }

        /** Sets a valid registration period. */
        fun setRegistrationPeriod(start: String, end: String) {
            // TODO: Validate dates and update startDate and endDate
        }

        /** Returns the waitlist capacity, or null for unlimited. */
        fun getMaxParticipants(): Int? {
            return maxParticipants
        }

        /** Sets the optional capacity; null means unlimited. */
        fun setMaxParticipants(capacity: Int?) {
            // TODO: Validate and update maxParticipants
        }

        /** Returns the lottery status values. */
        fun getStatus(): String {
            return status
        }

        /** Returns the creation date/time. */
        fun getCreatedAt(): String {
            return createdAt
        }
    }