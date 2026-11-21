package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: LotteryStatus.kt
Date Written: November 09, 2026
Author: Sofia Arturo Gomez
Purpose: Have a global status for firebase
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia      1.01     added skeleton status
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents the possible statuses of a Lottery:
 *
 * - OPEN
 * - CANCELLED
 * - COMPLETED
 * - PROCESSING
 */

enum class LotteryStatus {
    OPEN,
    CANCELLED,
    COMPLETED,
    PROCESSING
}