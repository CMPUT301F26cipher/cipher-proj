package com.example.cipherappproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Rating
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ratings.CampsiteReviewsSection
import com.example.cipherappproject.ratings.RateCampsiteView
import com.example.cipherappproject.reservations.ReservationScreen
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * UI tests for rating and reviewing a campsite after a stay.
 * US 01.13.01 (give a rating), US 01.13.02 (post a review).
 * Seeds its own test- documents and deletes them (and any rating it submitted) afterwards.
 */
@RunWith(AndroidJUnit4::class)
class RatingReviewFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val db = Firebase.firestore
    private val ratingRepo = RepositoryDatabase.ratingRepository
    private val reservationRepo = RepositoryDatabase.reservationRepository

    private val userId = "test-rater-${UUID.randomUUID().toString().take(8)}"
    private val created = mutableListOf<Pair<String, String>>() // collection to doc id
    private val reservationIds = mutableListOf<String>()

    @After
    fun cleanUp() {
        // ratings submitted through the UI get random ids, so find them by reservation
        reservationIds.forEach { resId ->
            ratingRepo.getRatingsFromListByReservationId(resId).forEach {
                Tasks.await(db.collection("ratings").document(it.ratingId).delete(), 15, TimeUnit.SECONDS)
            }
        }
        created.forEach { (collection, id) ->
            Tasks.await(db.collection(collection).document(id).delete(), 15, TimeUnit.SECONDS)
        }
    }

    private fun seedCampsite(): Campsite {
        val id = "test-${UUID.randomUUID()}"
        val campsite = Campsite(campsiteId = id, ownerId = "test-owner", campsiteName = "Review Camp $id", maxGuests = 4)
        Tasks.await(db.collection("campsites").document(id).set(campsite), 15, TimeUnit.SECONDS)
        created.add("campsites" to id)
        composeRule.waitUntil(15_000) { RepositoryDatabase.campsiteRepository.getCampsiteFromListById(id) != null }
        return campsite
    }

    private fun seedReservation(campsiteId: String, endDaysFromToday: Long): Reservation {
        val id = "test-${UUID.randomUUID()}"
        val today = LocalDate.now()
        val reservation = Reservation(
            reservationId = id, userId = userId, campsiteId = campsiteId, lotteryId = "test-lottery",
            status = ReservationStatus.ACTIVE.name,
            bookStartDate = today.plusDays(endDaysFromToday - 2).toString(),
            bookEndDate = today.plusDays(endDaysFromToday).toString()
        )
        Tasks.await(db.collection("reservations").document(id).set(reservation), 15, TimeUnit.SECONDS)
        created.add("reservations" to id)
        reservationIds.add(id)
        composeRule.waitUntil(15_000) { reservationRepo.getReservationFromListById(id) != null }
        return reservation
    }

    private fun seedRating(campsiteId: String, reservationId: String, value: Double, review: String, status: String = "ACTIVE"): Rating {
        val id = "test-${UUID.randomUUID()}"
        val rating = Rating(ratingId = id, userId = userId, campsiteId = campsiteId, reservationId = reservationId,
            review = review, rating = value, status = status)
        Tasks.await(db.collection("ratings").document(id).set(rating), 15, TimeUnit.SECONDS)
        created.add("ratings" to id)
        composeRule.waitUntil(15_000) { ratingRepo.getRatingFromListById(id) != null }
        return rating
    }

    private fun waitForRatingsLoaded() =
        composeRule.waitUntil(15_000) { ratingRepo.isRatingsLoaded.value }

    // ---------- give a rating and post a review ----------

    @Test
    fun pastStay_submitStarsAndReview_savesRating() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        waitForRatingsLoaded()
        var done = false
        composeRule.setContent { RateCampsiteView(reservation.reservationId, userId, onDone = { done = true }) }

        composeRule.onNodeWithText("Submit").assertIsDisplayed()
        composeRule.onNodeWithTag("star4").performClick()
        composeRule.onNodeWithTag("reviewField").performTextInput("  Quiet and clean  ")
        composeRule.onNodeWithTag("submitRatingButton").performClick()

        composeRule.waitUntil(15_000) { ratingRepo.getRatingsFromListByReservationId(reservation.reservationId).isNotEmpty() }
        val saved = ratingRepo.getRatingsFromListByReservationId(reservation.reservationId).single()
        assertEquals(4.0, saved.rating, 0.0)
        assertEquals("Quiet and clean", saved.review)
        assertEquals(campsite.campsiteId, saved.campsiteId)
        assertEquals(userId, saved.userId)
        assertEquals("ACTIVE", saved.status)
        composeRule.waitUntil(5_000) { done }
    }

    @Test
    fun ratingWithoutReview_isAllowed() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        waitForRatingsLoaded()
        composeRule.setContent { RateCampsiteView(reservation.reservationId, userId, onDone = {}) }

        composeRule.onNodeWithTag("star5").performClick()
        composeRule.onNodeWithTag("submitRatingButton").performClick()

        composeRule.waitUntil(15_000) { ratingRepo.getRatingsFromListByReservationId(reservation.reservationId).isNotEmpty() }
        val saved = ratingRepo.getRatingsFromListByReservationId(reservation.reservationId).single()
        assertEquals(5.0, saved.rating, 0.0)
        assertEquals("", saved.review)
    }

    @Test
    fun submitWithoutStars_showsErrorAndSavesNothing() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        waitForRatingsLoaded()
        composeRule.setContent { RateCampsiteView(reservation.reservationId, userId, onDone = {}) }

        composeRule.onNodeWithTag("reviewField").performTextInput("No stars")
        composeRule.onNodeWithTag("submitRatingButton").performClick()

        composeRule.onNodeWithTag("ratingError").assertIsDisplayed()
        assertTrue(ratingRepo.getRatingsFromListByReservationId(reservation.reservationId).isEmpty())
    }

    @Test
    fun existingRating_isPrefilledAndUpdatedInPlace() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        val existing = seedRating(campsite.campsiteId, reservation.reservationId, 3.0, "It was ok")
        composeRule.setContent { RateCampsiteView(reservation.reservationId, userId, onDone = {}) }

        composeRule.onNodeWithText("Update").assertIsDisplayed()
        composeRule.onNodeWithText("It was ok").assertIsDisplayed()
        composeRule.onNodeWithTag("star2").performClick()
        composeRule.onNodeWithTag("reviewField").performTextClearance()
        composeRule.onNodeWithTag("reviewField").performTextInput("Too noisy")
        composeRule.onNodeWithTag("submitRatingButton").performClick()

        composeRule.waitUntil(15_000) { ratingRepo.getRatingFromListById(existing.ratingId)?.rating == 2.0 }
        val ratings = ratingRepo.getRatingsFromListByReservationId(reservation.reservationId)
        assertEquals(1, ratings.size)
        assertEquals("Too noisy", ratings.single().review)
    }

    @Test
    fun upcomingStay_cannotBeRated() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = 5)
        waitForRatingsLoaded()
        composeRule.setContent { RateCampsiteView(reservation.reservationId, userId, onDone = {}) }

        composeRule.onNodeWithText("You can only rate a campsite after your stay.").assertIsDisplayed()
        composeRule.onNodeWithTag("submitRatingButton").assertDoesNotExist()
    }

    @Test
    fun removedRating_cannotBeEdited() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        seedRating(campsite.campsiteId, reservation.reservationId, 1.0, "Bad words", status = "REMOVED")
        composeRule.setContent { RateCampsiteView(reservation.reservationId, userId, onDone = {}) }

        composeRule.onNodeWithText("Your rating was removed by an admin and can't be changed.").assertIsDisplayed()
    }

    // ---------- entry point from past reservations ----------

    @Test
    fun pastReservation_showsRateButton() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        waitForRatingsLoaded()
        var rated: Reservation? = null
        composeRule.setContent { ReservationScreen(userID = userId, isOwner = false, onRateClick = { rated = it }) }

        composeRule.onNodeWithText("PAST").performClick()
        composeRule.onNodeWithTag("rateButton").assertTextEquals("Rate").performClick()
        assertEquals(reservation.reservationId, rated?.reservationId)
    }

    @Test
    fun ratedPastReservation_showsEditRating() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        seedRating(campsite.campsiteId, reservation.reservationId, 4.0, "Nice")
        composeRule.setContent { ReservationScreen(userID = userId, isOwner = false) }

        composeRule.onNodeWithText("PAST").performClick()
        composeRule.onNodeWithTag("rateButton").assertTextEquals("Edit Rating")
    }

    @Test
    fun activeReservation_hasNoRateButton() {
        val campsite = seedCampsite()
        seedReservation(campsite.campsiteId, endDaysFromToday = 5)
        waitForRatingsLoaded()
        composeRule.setContent { ReservationScreen(userID = userId, isOwner = false) }

        composeRule.onNodeWithText(campsite.campsiteName).assertIsDisplayed()
        composeRule.onNodeWithTag("rateButton").assertDoesNotExist()
    }

    // ---------- reviews on the campsite page ----------

    @Test
    fun campsitePage_showsAverageAndActiveReviewsOnly() {
        val campsite = seedCampsite()
        val res = seedReservation(campsite.campsiteId, endDaysFromToday = -3)
        seedRating(campsite.campsiteId, res.reservationId, 5.0, "Loved the lake")
        seedRating(campsite.campsiteId, res.reservationId, 3.0, "")
        seedRating(campsite.campsiteId, res.reservationId, 1.0, "Hidden review", status = "HIDDEN")
        composeRule.setContent { CampsiteReviewsSection(campsite.campsiteId) }

        composeRule.onNodeWithTag("campsiteAverageRating").assertTextEquals("★ 4.0 (2)")
        composeRule.onNodeWithText("Loved the lake").assertIsDisplayed()
        composeRule.onNodeWithText("Hidden review").assertDoesNotExist()
        assertEquals(1, composeRule.onAllNodesWithTag("reviewItem").fetchSemanticsNodes().size)
    }

    @Test
    fun campsitePage_withoutReviews_saysSo() {
        val campsite = seedCampsite()
        waitForRatingsLoaded()
        composeRule.setContent { CampsiteReviewsSection(campsite.campsiteId) }

        composeRule.onNodeWithText("No reviews yet").assertIsDisplayed()
        composeRule.onNodeWithTag("campsiteAverageRating").assertTextEquals("No ratings yet")
    }
}
