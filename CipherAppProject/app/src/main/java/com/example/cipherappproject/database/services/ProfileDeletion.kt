package com.example.cipherappproject.database.services

import com.example.cipherappproject.database.repositories.CampsiteAvailabilityRepository
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.example.cipherappproject.database.repositories.LotteryEntryRepository
import com.example.cipherappproject.database.repositories.LotteryRepository
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.database.repositories.ReservationRepository


/*
--------------------------------------------------------------------------------------
Program Name: ProfileDeletion.kt
Date Written: November 15, 2026
Author: Sofia Arturo Gomez
Purpose: Manage the logic of soft delete for delete profile
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-15  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

class ProfileDeletion(
    private val profileRepository: ProfileRepository,
    private val reservationRepository: ReservationRepository,
    private val lotteryRepository: LotteryRepository,
    private val lotteryEntryRepository: LotteryEntryRepository,
    private val campsiteRepository: CampsiteRepository,
    private val campsiteAvailabilityRepository: CampsiteAvailabilityRepository
) {
    // do later
}