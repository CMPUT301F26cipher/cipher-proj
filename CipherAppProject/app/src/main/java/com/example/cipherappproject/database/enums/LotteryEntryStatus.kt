package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: LotteryEntryStatus.kt
Date Written: November 09, 2026
Author: Sofia Arturo Gomez
Purpose: Have a global status for firebase
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia      1.01     added skeleton status
2026-11-12  Sofia      1.02     added commments
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents the possible statuses of a LotteryEntry:
 *
 * - REGISTERED
 * - WINNER
 * - OFFER_PENDING
 * - NOT_SELECTED
 * - ACCEPTED
 * - DECLINED
 * - EXPIRED
 * - WITHDRAWN
 */

enum class LotteryEntryStatus {
    REGISTERED,
    WINNER,
    OFFER_PENDING,
    NOT_SELECTED,
    ACCEPTED,
    DECLINED,
    EXPIRED,
    WITHDRAWN
}