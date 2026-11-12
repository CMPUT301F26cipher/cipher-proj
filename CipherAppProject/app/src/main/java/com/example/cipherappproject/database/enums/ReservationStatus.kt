package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: ReservationStatus.kt
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
 * Represents the possible statuses of Reservation
 *
 * - CANCELED
 * - PAST
 * - ACTIVE
 * - PENDING
 * - REJECTED --> if the owner or admin cancel reservation
 */

enum class ReservationStatus {
    CANCELED,
    PAST,
    ACTIVE,
    PENDING,
    REJECTED
}