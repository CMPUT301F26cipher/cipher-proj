package com.example.cipherappproject.database.provider

import com.example.cipherappproject.database.repositories.CampsiteAvailabilityRepository
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.example.cipherappproject.database.repositories.ConversationRepository
import com.example.cipherappproject.database.repositories.LotteryEntryRepository
import com.example.cipherappproject.database.repositories.LotteryRepository
import com.example.cipherappproject.database.repositories.MessageRepository
import com.example.cipherappproject.database.repositories.NotificationRepository
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.database.repositories.RatingRepository
import com.example.cipherappproject.database.repositories.ReservationRepository
import com.example.cipherappproject.database.services.ProfileDeletion

/*
--------------------------------------------------------------------------------------
Program Name: RepositoryDatabase.kt
Date Written: November 15, 2026
Author: Sofia Arturo Gomez
Purpose: Having one object that holds all the database repositories
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-15  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

object RepositoryDatabase {
    val profileRepository = ProfileRepository()

    val campsiteRepository = CampsiteRepository()
    val reservationRepository = ReservationRepository()

    val lotteryRepository = LotteryRepository()
    val lotteryEntryRepository = LotteryEntryRepository()

    val campsiteAvailabilityRepository = CampsiteAvailabilityRepository(reservationRepository)

    val ratingRepository = RatingRepository()
    val notificationRepository = NotificationRepository()


    val messageRepository = MessageRepository()

    val conversationRepository = ConversationRepository()

    val profileDeletion = ProfileDeletion(
        profileRepository,
        reservationRepository,
        lotteryRepository,
        lotteryEntryRepository,
        campsiteRepository,
        campsiteAvailabilityRepository
    )
}