package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: RatingStatus.kt
Date Written: November 12, 2026
Author: Sofia Arturo Gomez
Purpose: Hold the main status for Rating
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-12  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents the possible statuses of Rating (a campsite rating):
 *
 * - ACTIVE
 * - HIDDEN
 * - REMOVED
 */

enum class RatingStatus {
    ACTIVE,
    HIDDEN,
    REMOVED
}