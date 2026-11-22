package com.example.cipherappproject.ratings

/*
--------------------------------------------------------------------------------------
Program Name: RatingRules.kt
Date Written: November 21, 2026
Author: Nethmini Mackenzy
Purpose: Rules for who can rate a campsite and what a valid rating/review is
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

import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.database.enums.RatingStatus
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Rating
import com.example.cipherappproject.database.models.Reservation
import java.time.LocalDate

/**
 * Pure rating/review logic with no Firebase access, so it can be unit tested.
 *
 * An entrant rates a campsite through one of their reservations: one rating per reservation,
 * and only once the stay is over. A rating is 1-5 stars with an optional written review.
 */
object RatingRules {
    const val MIN_STARS = 1
    const val MAX_STARS = 5
    const val MAX_REVIEW_LENGTH = 500

    /** @return an error message, or null when [stars] is a valid rating */
    fun validateStars(stars: Int): String? =
        if (stars in MIN_STARS..MAX_STARS) null else "Please choose a rating from $MIN_STARS to $MAX_STARS stars."

    /** @return an error message, or null when [review] is a valid (possibly empty) review */
    fun validateReview(review: String): String? =
        if (review.trim().length <= MAX_REVIEW_LENGTH) null
        else "Reviews can be at most $MAX_REVIEW_LENGTH characters."

    /**
     * Whether [userId] may rate the campsite of [reservation]: it must be their reservation,
     * not cancelled or rejected, and the stay must be over (marked PAST or end date before [today]).
     */
    fun canRate(reservation: Reservation, userId: String, today: LocalDate = LocalDate.now()): Boolean {
        if (userId.isBlank() || reservation.userId != userId) return false
        if (reservation.status == ReservationStatus.CANCELED.name ||
            reservation.status == ReservationStatus.REJECTED.name
        ) return false
        if (reservation.status == ReservationStatus.PAST.name) return true
        val end = DateUtils.parse(reservation.bookEndDate) ?: return false
        return end.isBefore(today)
    }

    /** The rating [userId] already gave for [reservationId], if any (including removed ones). */
    fun existingRating(ratings: List<Rating>, reservationId: String, userId: String): Rating? =
        ratings.firstOrNull { it.reservationId == reservationId && it.userId == userId }

    /** A rating an admin removed can't be edited or replaced by the entrant. */
    fun isLocked(rating: Rating?): Boolean = rating?.status == RatingStatus.REMOVED.name

    /** Active ratings that have a written review, newest first. */
    fun visibleReviews(ratings: List<Rating>): List<Rating> =
        ratings
            .filter { it.status == RatingStatus.ACTIVE.name && it.review.isNotBlank() }
            .sortedByDescending { it.createdAt }

    /** Star string for a rating, e.g. 4.0 -> "★★★★☆" */
    fun stars(value: Double): String {
        val filled = value.toInt().coerceIn(0, MAX_STARS)
        return "★".repeat(filled) + "☆".repeat(MAX_STARS - filled)
    }
}
