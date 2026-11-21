package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: MessageStatus.kt
Date Written: November 12, 2026
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
 * Represents the possible statuses of a Message:
 *
 * - SENT
 * - REMOVED
 */

enum class MessageStatus {
    SENT,
    READ,
    REMOVED
}