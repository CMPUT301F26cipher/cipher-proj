package com.example.cipherappproject.ratings

/*
--------------------------------------------------------------------------------------
Program Name: RatingController.kt
Date Written: November 21, 2026
Author: Nethmini Mackenzy
Purpose: Saves an entrant's rating and review for a campsite they stayed at
US: 01.13.01, 01.13.02
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-21  Nethmini   1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import com.example.cipherappproject.database.enums.RatingStatus
import com.example.cipherappproject.database.models.Rating
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Creates or updates the rating attached to a reservation, through RatingRepository.
 */
class RatingController {
    private val ratingRepository get() = RepositoryDatabase.ratingRepository

    /** The rating [userId] already gave for [reservationId], if any. */
    fun existingRating(reservationId: String, userId: String): Rating? =
        RatingRules.existingRating(
            ratingRepository.getRatingsFromListByReservationId(reservationId), reservationId, userId
        )

    /**
     * Saves [stars] and [review] for [reservation]: adds a new rating, or updates the one
     * the entrant already gave for this reservation.
     *
     * @param result Result.success(Unit) when saved, or Result.failure with a readable message
     */
    fun submit(
        reservation: Reservation,
        userId: String,
        stars: Int,
        review: String,
        result: (Result<Unit>) -> Unit
    ) {
        val error = RatingRules.validateStars(stars)
            ?: RatingRules.validateReview(review)
            ?: if (!RatingRules.canRate(reservation, userId)) "You can only rate a campsite after your stay." else null
        if (error != null) {
            result(Result.failure(IllegalArgumentException(error)))
            return
        }

        val existing = existingRating(reservation.reservationId, userId)
        if (RatingRules.isLocked(existing)) {
            result(Result.failure(IllegalStateException("Your rating was removed by an admin and can't be changed.")))
            return
        }

        val text = review.trim()
        if (existing == null) {
            ratingRepository.addRating(
                userId = userId,
                campsiteId = reservation.campsiteId,
                reservationId = reservation.reservationId,
                review = text,
                rating = stars.toDouble(),
                status = RatingStatus.ACTIVE.name
            ) { added -> result(added.map { }) }
        } else {
            ratingRepository.updateRating(
                existing.copy(rating = stars.toDouble(), review = text)
            ) { updated -> result(updated) }
        }
    }
}
