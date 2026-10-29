package com.example.cipherappproject
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
    private val lotteryId: String = "",
    private val ownerId: String = "",
    // one lottery can be applicable to many lotteries
    private val campsiteIds: List<String> = emptyList(),
    private var numWinner: Int = 0,
    private var startDate: String = "", // Format can be yyyy-mm-hh-ss or even shorter
    private var endDate: String = "",
    private var maxParticipants: Int, // is optional for the owner
    private val status: List<String> = emptyList(),
    private val createdAt: String = "" // also the yyyy-mm-hh format --> useful for the notifications!
    ){
    // WILL FOLLOW A SIMILAR FORMAT TO PROFILE
}