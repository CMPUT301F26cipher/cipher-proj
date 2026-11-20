package com.example.cipherappproject

/**
 * Lottery-screen constants that are not already covered by the database enums.
 *
 * Statuses are NOT defined here any more: use the enums in database/enums
 * (LotteryStatus, LotteryEntryStatus, CampsiteAvailabilityStatus, ReservationStatus)
 * and store them with `.name`, as described in Database_Documentation.md.
 */
object LotteryConstants {
    /** Lottery.maxParticipants value meaning "no waitlist limit". */
    const val UNLIMITED_PARTICIPANTS = 0
}
