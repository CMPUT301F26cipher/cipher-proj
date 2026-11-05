package com.example.cipherappproject

/**
 * Status values the lottery screens read and write, kept in one place.
 *
 * NOTE: WaitlistModel.kt has its own LotteryStatus enum;
 */
object LotteryConstants {
    // Lottery.status
    const val LOTTERY_OPEN = "open"
    const val LOTTERY_DRAWN = "drawn"
    const val LOTTERY_FINISHED = "finished"

    /** Lottery.maxParticipants value meaning "no waitlist limit". */
    const val UNLIMITED_PARTICIPANTS = 0

    // LotteryEntry status
    const val ENTRY_REGISTERED = "registered"
    const val ENTRY_WINNER = "winner"
    const val ENTRY_NOT_SELECTED = "notSelected"
    const val ENTRY_ACCEPTED = "accepted"
    const val ENTRY_DECLINED = "declined"

    // CampsiteAvailability.status meaning the owner opened those dates
    const val AVAILABILITY_OPEN = "available"

    /** Reservation statuses whose nights become free again. */
    val FREED_RESERVATION_STATUSES = setOf("cancelled", "canceled", "rejected")
}
