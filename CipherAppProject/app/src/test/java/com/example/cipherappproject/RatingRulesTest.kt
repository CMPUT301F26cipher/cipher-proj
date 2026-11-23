package com.example.cipherappproject

import com.example.cipherappproject.database.enums.RatingStatus
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Rating
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.ratings.RatingRules
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Tests rating/review rules: valid input, who can rate, and which reviews are shown.
 * US 01.13.01 (give a rating), US 01.13.02 (post a review).
 */
class RatingRulesTest {

    private val today = LocalDate.of(2026, 11, 21)

    private fun reservation(
        status: ReservationStatus = ReservationStatus.ACTIVE,
        end: String = "2026-11-10",
        userId: String = "u1"
    ) = Reservation(
        reservationId = "res1", userId = userId, campsiteId = "c1",
        status = status.name, bookStartDate = "2026-11-08", bookEndDate = end
    )

    private fun rating(
        id: String,
        review: String = "Great",
        status: RatingStatus = RatingStatus.ACTIVE,
        seconds: Long = 0,
        reservationId: String = "res1",
        userId: String = "u1"
    ) = Rating(
        ratingId = id, userId = userId, campsiteId = "c1", reservationId = reservationId,
        review = review, rating = 4.0, status = status.name, createdAt = Timestamp(seconds, 0)
    )

    // ---------- stars (01.13.01) ----------

    @Test
    fun stars_oneToFive_areValid() {
        for (s in 1..5) assertNull(RatingRules.validateStars(s))
    }

    @Test
    fun stars_outsideRange_areInvalid() {
        assertNotNull(RatingRules.validateStars(0))
        assertNotNull(RatingRules.validateStars(6))
        assertNotNull(RatingRules.validateStars(-1))
    }

    @Test
    fun starString_showsFilledAndEmpty() {
        assertEquals("★★★★☆", RatingRules.stars(4.0))
        assertEquals("★★★★★", RatingRules.stars(5.0))
        assertEquals("★☆☆☆☆", RatingRules.stars(1.0))
    }

    // ---------- review (01.13.02) ----------

    @Test
    fun review_emptyIsAllowed() {
        assertNull(RatingRules.validateReview(""))
        assertNull(RatingRules.validateReview("   "))
    }

    @Test
    fun review_atLimitIsAllowed_overLimitIsNot() {
        assertNull(RatingRules.validateReview("a".repeat(RatingRules.MAX_REVIEW_LENGTH)))
        assertNotNull(RatingRules.validateReview("a".repeat(RatingRules.MAX_REVIEW_LENGTH + 1)))
    }

    @Test
    fun review_surroundingSpacesDoNotCount() {
        assertNull(RatingRules.validateReview("  " + "a".repeat(RatingRules.MAX_REVIEW_LENGTH) + "  "))
    }

    // ---------- who can rate ----------

    @Test
    fun canRate_afterStayEnds() {
        assertTrue(RatingRules.canRate(reservation(end = "2026-11-10"), "u1", today))
    }

    @Test
    fun canRate_markedPast_evenIfEndDateMissing() {
        assertTrue(RatingRules.canRate(reservation(ReservationStatus.PAST, end = ""), "u1", today))
    }

    @Test
    fun cannotRate_beforeOrOnLastDay() {
        assertFalse(RatingRules.canRate(reservation(end = "2026-11-21"), "u1", today))
        assertFalse(RatingRules.canRate(reservation(end = "2026-12-01"), "u1", today))
    }

    @Test
    fun cannotRate_cancelledOrRejected() {
        assertFalse(RatingRules.canRate(reservation(ReservationStatus.CANCELED), "u1", today))
        assertFalse(RatingRules.canRate(reservation(ReservationStatus.REJECTED), "u1", today))
    }

    @Test
    fun cannotRate_someoneElsesReservation() {
        assertFalse(RatingRules.canRate(reservation(), "u2", today))
        assertFalse(RatingRules.canRate(reservation(), "", today))
    }

    // ---------- one rating per reservation ----------

    @Test
    fun existingRating_findsTheUsersRatingForThatReservation() {
        val ratings = listOf(
            rating("other-res", reservationId = "res2"),
            rating("other-user", userId = "u2"),
            rating("mine"),
        )
        assertEquals("mine", RatingRules.existingRating(ratings, "res1", "u1")?.ratingId)
        assertNull(RatingRules.existingRating(ratings, "res3", "u1"))
    }

    @Test
    fun removedRating_isLocked() {
        assertTrue(RatingRules.isLocked(rating("r", status = RatingStatus.REMOVED)))
        assertFalse(RatingRules.isLocked(rating("r")))
        assertFalse(RatingRules.isLocked(null))
    }

    // ---------- shown reviews ----------

    @Test
    fun visibleReviews_onlyActiveWithText_newestFirst() {
        val ratings = listOf(
            rating("old", seconds = 100),
            rating("new", seconds = 300),
            rating("starsOnly", review = "  ", seconds = 400),
            rating("hidden", status = RatingStatus.HIDDEN, seconds = 500),
            rating("removed", status = RatingStatus.REMOVED, seconds = 600),
        )
        assertEquals(listOf("new", "old"), RatingRules.visibleReviews(ratings).map { it.ratingId })
    }
}
