package com.example.cipherappproject.database.enums

/*
--------------------------------------------------------------------------------------
Program Name: ProfileRole.kt
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
 * Represents the possible roles of a Profile:
 *
 * - USER
 * - OWNER
 * - ADMIN
 */


enum class ProfileRole {
    ENTRANT,
    OWNER,
    ADMIN
}