package com.example.cipherappproject

import com.example.cipherappproject.database.enums.RatingStatus
import com.example.cipherappproject.database.models.Rating
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests the rating shown on a campsite's summary card (US 01.01.04).
 */
class CampsiteRatingTest {

    private fun rating(value: Double, status: RatingStatus = RatingStatus.ACTIVE) =
        Rating(ratingId = "r$value", campsiteId = "c1", rating = value, status = status.name)

    @Test
    fun noRatings_hasNoAverage() {
        assertNull(averageRating(emptyList()))
        assertEquals("No ratings yet", ratingText(emptyList()))
    }

    @Test
    fun singleRating_isItsOwnAverage() {
        assertEquals(5.0, averageRating(listOf(rating(5.0)))!!, 0.0)
        assertEquals("★ 5.0 (1)", ratingText(listOf(rating(5.0))))
    }

    @Test
    fun averagesSeveralRatings() {
        val ratings = listOf(rating(5.0), rating(4.0), rating(3.0), rating(4.0))
        assertEquals(4.0, averageRating(ratings)!!, 0.0)
        assertEquals("★ 4.0 (4)", ratingText(ratings))
    }

    @Test
    fun roundsToOneDecimal() {
        assertEquals("★ 4.7 (3)", ratingText(listOf(rating(5.0), rating(5.0), rating(4.0))))
    }

    @Test
    fun hiddenAndRemovedRatings_areNotCounted() {
        val ratings = listOf(
            rating(5.0),
            rating(1.0, RatingStatus.HIDDEN),
            rating(1.0, RatingStatus.REMOVED),
        )
        assertEquals(5.0, averageRating(ratings)!!, 0.0)
        assertEquals("★ 5.0 (1)", ratingText(ratings))
    }

    @Test
    fun onlyHiddenRatings_countsAsNoRatings() {
        val ratings = listOf(rating(2.0, RatingStatus.HIDDEN))
        assertNull(averageRating(ratings))
        assertEquals("No ratings yet", ratingText(ratings))
    }
}
