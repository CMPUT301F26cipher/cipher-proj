package com.example.cipherappproject

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.reservations.ReservationScreen
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * UI tests for the reservation lists, cancelling and rejecting.
 * Entrant: US 01.06.01 (active), 01.06.02 (past), 01.07.01 (cancel), 01.05.02 (CSV button).
 * Owner: US 02.07.01 (active), 02.07.02 (past), 02.07.03 (cancelled), 02.08.01 (reject),
 * 02.06.01 (booked dates).
 * Seeds its own test- documents and deletes them (and the notifications they trigger) afterwards.
 */
@RunWith(AndroidJUnit4::class)
class ReservationFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val db = Firebase.firestore
    private val reservationRepo = RepositoryDatabase.reservationRepository

    private val suffix = UUID.randomUUID().toString().take(8)
    private val entrantId = "test-entrant-$suffix"
    private val ownerId = "test-owner-$suffix"
    private val created = mutableListOf<Pair<String, String>>() // collection to doc id
    private val reservationIds = mutableListOf<String>()

    @After
    fun cleanUp() {
        // cancelling/rejecting sends notifications about the reservation
        reservationIds.forEach { resId ->
            val sent = Tasks.await(
                db.collection("notifications").whereEqualTo("eventId", resId).get(), 15, TimeUnit.SECONDS
            )
            sent.documents.forEach { Tasks.await(it.reference.delete(), 15, TimeUnit.SECONDS) }
        }
        created.forEach { (collection, id) ->
            Tasks.await(db.collection(collection).document(id).delete(), 15, TimeUnit.SECONDS)
        }
    }

    private fun seedCampsite(name: String, policies: List<String> = emptyList()): Campsite {
        val id = "test-${UUID.randomUUID()}"
        val campsite = Campsite(
            campsiteId = id, ownerId = ownerId, campsiteName = name, maxGuests = 4,
            policies = policies.toMutableList()
        )
        Tasks.await(db.collection("campsites").document(id).set(campsite), 15, TimeUnit.SECONDS)
        created.add("campsites" to id)
        composeRule.waitUntil(15_000) { RepositoryDatabase.campsiteRepository.getCampsiteFromListById(id) != null }
        return campsite
    }

    /** A stay from [startDays] to [endDays] days from today (both inclusive). */
    private fun seedReservation(
        campsiteId: String, startDays: Long, endDays: Long,
        status: ReservationStatus = ReservationStatus.ACTIVE, total: Double = 100.0
    ): Reservation {
        val id = "test-${UUID.randomUUID()}"
        val today = LocalDate.now()
        val reservation = Reservation(
            reservationId = id, userId = entrantId, campsiteId = campsiteId, lotteryId = "test-lottery",
            status = status.name,
            bookStartDate = today.plusDays(startDays).toString(),
            bookEndDate = today.plusDays(endDays).toString(),
            pricePerNight = 50.0, totalPrice = total
        )
        Tasks.await(db.collection("reservations").document(id).set(reservation), 15, TimeUnit.SECONDS)
        created.add("reservations" to id)
        reservationIds.add(id)
        composeRule.waitUntil(15_000) { reservationRepo.getReservationFromListById(id) != null }
        return reservation
    }

    private fun waitForStatus(reservationId: String, status: ReservationStatus) =
        composeRule.waitUntil(15_000) { reservationRepo.getReservationFromListById(reservationId)?.status == status.name }

    // ---------- entrant ----------

    @Test
    fun entrant_tabsShowActivePastAndCancelled() {
        val camp = seedCampsite("Active Camp $suffix")
        val pastCamp = seedCampsite("Past Camp $suffix")
        val cancelledCamp = seedCampsite("Cancelled Camp $suffix")
        val rejectedCamp = seedCampsite("Rejected Camp $suffix")
        seedReservation(camp.campsiteId, 10, 12)
        seedReservation(pastCamp.campsiteId, -5, -3)
        seedReservation(cancelledCamp.campsiteId, 20, 21, ReservationStatus.CANCELED)
        seedReservation(rejectedCamp.campsiteId, 30, 31, ReservationStatus.REJECTED)

        composeRule.setContent { ReservationScreen(userID = entrantId, isOwner = false) }

        // Active: only the upcoming stay, with cancel and the CSV download
        composeRule.onNodeWithText(camp.campsiteName).assertIsDisplayed()
        composeRule.onAllNodesWithText(pastCamp.campsiteName).assertCountEquals(0)
        composeRule.onAllNodesWithText(cancelledCamp.campsiteName).assertCountEquals(0)
        composeRule.onAllNodesWithTag("removeReservation").assertCountEquals(1)
        composeRule.onNodeWithTag("downloadCsv").assertIsDisplayed()

        // Past: the ended stay, which can be rated but not cancelled
        composeRule.onNodeWithText("PAST").performClick()
        composeRule.onNodeWithText(pastCamp.campsiteName).assertIsDisplayed()
        composeRule.onAllNodesWithText(camp.campsiteName).assertCountEquals(0)
        composeRule.onAllNodesWithTag("removeReservation").assertCountEquals(0)

        // Cancelled: cancelled and rejected reservations, labelled
        composeRule.onNodeWithText("CANCELLED").performClick()
        composeRule.onNodeWithText(cancelledCamp.campsiteName).assertIsDisplayed()
        composeRule.onNodeWithText(rejectedCamp.campsiteName).assertIsDisplayed()
        composeRule.onNodeWithText("(cancelled", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("(rejected", substring = true).assertIsDisplayed()
        composeRule.onAllNodesWithTag("removeReservation").assertCountEquals(0)
        composeRule.onAllNodesWithText(camp.campsiteName).assertCountEquals(0)
    }

    @Test
    fun entrant_noReservations_showsEmptyMessage() {
        composeRule.setContent { ReservationScreen(userID = entrantId, isOwner = false) }
        composeRule.onNodeWithText("No reservations").assertIsDisplayed()
        composeRule.onNodeWithText("PAST").performClick()
        composeRule.onNodeWithText("No reservations").assertIsDisplayed()
        composeRule.onNodeWithText("CANCELLED").performClick()
        composeRule.onNodeWithText("No reservations").assertIsDisplayed()
    }

    @Test
    fun entrant_cancelActiveReservation_movesToCancelled() {
        val camp = seedCampsite("Cancel Camp $suffix")
        val reservation = seedReservation(camp.campsiteId, 14, 15)
        composeRule.setContent { ReservationScreen(userID = entrantId, isOwner = false) }

        // backing out of the dialog keeps the reservation
        composeRule.onNodeWithTag("removeReservation").performClick()
        composeRule.onNodeWithText("Cancel reservation?").assertIsDisplayed()
        composeRule.onNodeWithTag("dismissDialog").performClick()
        assertEquals(ReservationStatus.ACTIVE.name, reservationRepo.getReservationFromListById(reservation.reservationId)?.status)

        composeRule.onNodeWithTag("removeReservation").performClick()
        composeRule.onNodeWithTag("confirmCancel").performClick()
        waitForStatus(reservation.reservationId, ReservationStatus.CANCELED)
        assertNotNull(reservationRepo.getReservationFromListById(reservation.reservationId)?.cancelledAt)

        composeRule.waitUntil(5_000) { composeRule.onAllNodesWithText(camp.campsiteName).fetchSemanticsNodes().isEmpty() }
        composeRule.onNodeWithText("CANCELLED").performClick()
        composeRule.onNodeWithText(camp.campsiteName).assertIsDisplayed()
    }

    @Test
    fun entrant_activeListIsSoonestFirst() {
        val later = seedCampsite("Later Camp $suffix")
        val sooner = seedCampsite("Sooner Camp $suffix")
        seedReservation(later.campsiteId, 40, 41)
        seedReservation(sooner.campsiteId, 10, 11)
        composeRule.setContent { ReservationScreen(userID = entrantId, isOwner = false) }

        val laterTop = composeRule.onNodeWithText(later.campsiteName).fetchSemanticsNode().boundsInRoot.top
        val soonerTop = composeRule.onNodeWithText(sooner.campsiteName).fetchSemanticsNode().boundsInRoot.top
        assert(soonerTop < laterTop) { "the sooner stay should be listed first" }
    }

    // ---------- owner ----------

    @Test
    fun owner_tabsShowReservationsAtTheirCampsites() {
        val camp = seedCampsite("Owner Camp $suffix")
        val active = seedReservation(camp.campsiteId, 5, 6)
        val past = seedReservation(camp.campsiteId, -10, -8)
        seedReservation(camp.campsiteId, 25, 26, ReservationStatus.CANCELED)

        composeRule.setContent { ReservationScreen(userID = ownerId, isOwner = true) }

        val display = { r: Reservation -> DateUtils.display(DateUtils.parse(r.bookStartDate)!!) }
        composeRule.onNodeWithText(display(active), substring = true).assertIsDisplayed()
        composeRule.onAllNodesWithText(display(past), substring = true).assertCountEquals(0)
        composeRule.onNodeWithTag("viewBookedCalendar").assertIsDisplayed()

        composeRule.onNodeWithText("PAST").performClick()
        composeRule.onNodeWithText(display(past), substring = true).assertIsDisplayed()
        composeRule.onAllNodesWithText(display(active), substring = true).assertCountEquals(0)

        composeRule.onNodeWithText("CANCELLED").performClick()
        composeRule.onNodeWithText("(cancelled", substring = true).assertIsDisplayed()
    }

    @Test
    fun owner_rejectReservation_movesToCancelledAsRejected() {
        val camp = seedCampsite("Reject Camp $suffix")
        val reservation = seedReservation(camp.campsiteId, 30, 31)
        composeRule.setContent { ReservationScreen(userID = ownerId, isOwner = true) }

        composeRule.onNodeWithText(camp.campsiteName).performClick()
        composeRule.onNodeWithTag("reservationDetail").assertIsDisplayed()
        composeRule.onNodeWithText("Reservation ID: ${reservation.reservationId}").assertIsDisplayed()

        // backing out keeps it active
        composeRule.onNodeWithTag("rejectReservation").performClick()
        composeRule.onNodeWithTag("dismissDialog").performClick()
        assertEquals(ReservationStatus.ACTIVE.name, reservationRepo.getReservationFromListById(reservation.reservationId)?.status)

        composeRule.onNodeWithTag("rejectReservation").performClick()
        composeRule.onNodeWithTag("confirmReject").performClick()
        waitForStatus(reservation.reservationId, ReservationStatus.REJECTED)
        assertNotNull(reservationRepo.getReservationFromListById(reservation.reservationId)?.cancelledAt)

        composeRule.onNodeWithText("CANCELLED").performClick()
        composeRule.onNodeWithText(camp.campsiteName).assertIsDisplayed()
        composeRule.onNodeWithText("(rejected", substring = true).assertIsDisplayed()
    }

    @Test
    fun owner_rejectSendsEntrantNotificationWithRefund() {
        // notifications only go to entrants with a profile that has them turned on
        Tasks.await(
            db.collection("profiles").document(entrantId).set(
                Profile(profileId = entrantId, name = "TEST_Entrant $suffix", notificationsEnabled = true)
            ), 15, TimeUnit.SECONDS
        )
        created.add("profiles" to entrantId)
        composeRule.waitUntil(15_000) { RepositoryDatabase.profileRepository.getProfileFromListById(entrantId) != null }
        val camp = seedCampsite("Refund Camp $suffix")
        val reservation = seedReservation(camp.campsiteId, 30, 31, total = 120.0)
        composeRule.setContent { ReservationScreen(userID = ownerId, isOwner = true) }

        composeRule.onNodeWithText(camp.campsiteName).performClick()
        composeRule.onNodeWithTag("rejectReservation").performClick()
        composeRule.onNodeWithTag("confirmReject").performClick()
        waitForStatus(reservation.reservationId, ReservationStatus.REJECTED)

        var messages = emptyList<String>()
        composeRule.waitUntil(15_000) {
            messages = Tasks.await(
                db.collection("notifications").whereEqualTo("eventId", reservation.reservationId).get(),
                15, TimeUnit.SECONDS
            ).documents.filter { it.getString("toUserId") == entrantId }.mapNotNull { it.getString("message") }
            messages.isNotEmpty()
        }
        assert(messages.any { it.contains("rejected") && it.contains("120.00") }) { messages.toString() }
    }

    @Test
    fun owner_viewBookedDatesOpensCalendar() {
        val camp = seedCampsite("Calendar Camp $suffix")
        seedReservation(camp.campsiteId, 1, 2)
        composeRule.setContent { ReservationScreen(userID = ownerId, isOwner = true) }

        composeRule.onNodeWithTag("viewBookedCalendar").performClick()
        composeRule.onNodeWithText("Booked dates").assertIsDisplayed()
        composeRule.onNodeWithText(camp.campsiteName).assertIsDisplayed() // campsite filter chip
        composeRule.onNodeWithTag("calendarBack").performClick()
        composeRule.onNodeWithTag("reservationsMenu").assertIsDisplayed()
    }
}
