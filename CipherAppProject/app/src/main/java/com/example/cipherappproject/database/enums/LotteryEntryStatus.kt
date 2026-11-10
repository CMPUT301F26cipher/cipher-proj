package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: CampsiteStatus.kt
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