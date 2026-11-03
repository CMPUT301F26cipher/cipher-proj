package com.example.cipherappproject

/**
 * Stores lottery ownership, campsite IDs, registration period, winner count,
 * optional waitlist capacity, and lifecycle information.
 *
 * Supports US 02.04.01, 02.04.05, and 02.04.07.
 * Outstanding issues: implement accessors and validation; agree on date format,
 * period semantics, status values, and Firestore mapping with the team.
 *
 * @property lotteryId unique lottery identifier
 * @property ownerId owner responsible for this lottery
 * @property campsiteIds campsites included in this lottery
 * @property numWinner number of entrants to select as winners
 * @property startDate registration opening date, pending team agreement
 * @property endDate registration closing date, pending team agreement
 * @property maxParticipants waitlist capacity; null means unlimited
 * @property status lifecycle status values, pending team agreement
 * @property createdAt creation date/time in the agreed representation
 */
data class Lottery(
    private val lotteryId: String = "",
    private val ownerId: String = "",
    private val campsiteIds: List<String> = emptyList(),
    private var numWinner: Int = 0,
    private var startDate: String = "",
    private var endDate: String = "",
    private var maxParticipants: Int? = null,
    private val status: List<String> = emptyList(),
    private val createdAt: String = ""
) {
    /** Returns the lottery ID. */
    fun getLotteryId(): String {
        // TODO: Return lotteryId
    }

    /** Returns the owner ID. */
    fun getOwnerId(): String {
        // TODO: Return ownerId
    }

    /** Returns the campsite IDs included in this lottery. */
    fun getCampsiteIds(): List<String> {
        // TODO: Return campsiteIds
    }

    /** Returns the number of winners. */
    fun getNumWinner(): Int {
        // TODO: Return numWinner
    }

    /** Sets a positive winner count. */
    fun setNumWinner(number: Int) {
       // TODO: Validate and update numWinner
    }

    /** Returns the registration start date. */
    fun getStartDate(): String {
        // TODO: Return startDate
    }

    /** Returns the registration end date. */
    fun getEndDate(): String {
        // TODO: Return endDate
    }

    /** Sets a valid registration period. */
    fun setRegistrationPeriod(start: String, end: String) {
        // TODO: Validate dates and update startDate and endDate
    }

    /** Returns the waitlist capacity, or null for unlimited. */
    fun getMaxParticipants(): Int? {
        // TODO: Return maxParticipants
    }

    /** Sets the optional capacity; null means unlimited. */
    fun setMaxParticipants(capacity: Int?) {
        // TODO: Validate and update maxParticipants
    }

    /** Returns the lottery status values. */
    fun getStatus(): List<String> {
        // TODO: Return status
    }

    /** Returns the creation date/time. */
    fun getCreatedAt(): String {
        // TODO: Return createdAt
    }
}
