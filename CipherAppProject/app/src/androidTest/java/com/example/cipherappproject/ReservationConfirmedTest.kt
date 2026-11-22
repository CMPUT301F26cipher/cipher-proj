package com.example.cipherappproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.reservations.ReservationConfirmedView
import com.example.cipherappproject.ui.lottery.RulesAcceptanceView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * US 01.05.01: the entrant sees their reservation confirmed, with its details.
 * US 01.05.02: the confirmation offers the reservation as a downloadable CSV.
 * US 02.11.01: the entrant must confirm the campsite's rules and policies before reserving.
 * Seeds its own test- documents and deletes them afterwards.
 */
@RunWith(AndroidJUnit4::class)
class ReservationConfirmedTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val db = Firebase.firestore
    private val suffix = UUID.randomUUID().toString().take(8)
    private val entrantId = "test-entrant-$suffix"
    private val lotteryId = "test-lottery-$suffix"
    private val created = mutableListOf<Pair<String, String>>()

    @After
    fun cleanUp() {
        created.forEach { (collection, id) ->
            Tasks.await(db.collection(collection).document(id).delete(), 15, TimeUnit.SECONDS)
        }
    }

    private fun seedCampsite(): Campsite {
        val id = "test-${UUID.randomUUID()}"
        val campsite = Campsite(campsiteId = id, ownerId = "test-owner", campsiteName = "Confirm Camp $suffix", maxGuests = 4)
        Tasks.await(db.collection("campsites").document(id).set(campsite), 15, TimeUnit.SECONDS)
        created.add("campsites" to id)
        composeRule.waitUntil(15_000) { RepositoryDatabase.campsiteRepository.getCampsiteFromListById(id) != null }
        return campsite
    }

    private fun seedReservation(campsiteId: String, status: ReservationStatus = ReservationStatus.ACTIVE): Reservation {
        val id = "test-${UUID.randomUUID()}"
        val reservation = Reservation(
            reservationId = id, userId = entrantId, campsiteId = campsiteId, lotteryId = lotteryId,
            status = status.name, bookStartDate = "2027-05-01", bookEndDate = "2027-05-03",
            pricePerNight = 65.0, totalPrice = 195.0
        )
        Tasks.await(db.collection("reservations").document(id).set(reservation), 15, TimeUnit.SECONDS)
        created.add("reservations" to id)
        composeRule.waitUntil(15_000) { RepositoryDatabase.reservationRepository.getReservationFromListById(id) != null }
        return reservation
    }

    @Test
    fun confirmation_showsReservationDetailsAndCsv() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId)
        var viewed = false
        composeRule.setContent { ReservationConfirmedView(lotteryId, entrantId, onViewReservations = { viewed = true }) }

        composeRule.onNodeWithTag("confirmationTitle").assertTextEquals("Reservation confirmed!")
        composeRule.onNodeWithText("Campsite: ${campsite.campsiteName}").assertIsDisplayed()
        composeRule.onNodeWithText("Dates: Sat, May 1, 2027 - Mon, May 3, 2027").assertIsDisplayed()
        composeRule.onNodeWithText("Nights: 3").assertIsDisplayed()
        composeRule.onNodeWithText("Total: $195.00").assertIsDisplayed()
        composeRule.onNodeWithText("Reservation ID: ${reservation.reservationId}").assertIsDisplayed()
        composeRule.onNodeWithTag("downloadCsv").assertIsDisplayed()

        composeRule.onNodeWithTag("viewReservations").performClick()
        assertTrue(viewed)
    }

    @Test
    fun confirmation_showsWhenOwnerLaterRejectsIt() {
        val campsite = seedCampsite()
        val reservation = seedReservation(campsite.campsiteId)
        composeRule.setContent { ReservationConfirmedView(lotteryId, entrantId, onViewReservations = {}) }
        composeRule.onNodeWithTag("confirmationTitle").assertTextEquals("Reservation confirmed!")

        Tasks.await(
            db.collection("reservations").document(reservation.reservationId)
                .update("status", ReservationStatus.REJECTED.name), 15, TimeUnit.SECONDS
        )
        composeRule.waitUntil(15_000) {
            RepositoryDatabase.reservationRepository.getReservationFromListById(reservation.reservationId)?.status ==
                    ReservationStatus.REJECTED.name
        }
        composeRule.onNodeWithTag("confirmationTitle").assertTextEquals("Reservation rejected")
    }

    @Test
    fun rules_mustBeAcceptedBeforeConfirming() {
        var confirmed = false
        composeRule.setContent {
            RulesAcceptanceView(
                rules = listOf("No smoking"),
                policies = listOf("No refunds after check-in"),
                isWorking = false,
                message = null,
                onConfirm = { confirmed = true },
                onBack = {}
            )
        }
        composeRule.onNodeWithText("• No smoking").assertIsDisplayed()
        composeRule.onNodeWithText("• No refunds after check-in").assertIsDisplayed()

        // confirming without ticking the box does nothing
        composeRule.onNodeWithText("Confirm reservation").performClick()
        assertFalse(confirmed)

        composeRule.onNodeWithTag("rulesCheckbox").performClick()
        composeRule.onNodeWithText("Confirm reservation").performClick()
        assertTrue(confirmed)
    }
}
