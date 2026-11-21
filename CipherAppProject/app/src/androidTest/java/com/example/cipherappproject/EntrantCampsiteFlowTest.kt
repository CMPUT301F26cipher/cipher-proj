package com.example.cipherappproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.campsite.EntrantCampsiteView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * UI tests for what an entrant sees: searching, the campsite list and summary, and campsite details.
 * Covers US 01.01.01 to 01.01.05. Campsites are seeded with a unique word in the name so the tests do
 * not depend on whatever else is in the database.
 */
@RunWith(AndroidJUnit4::class)
class EntrantCampsiteFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val repository = RepositoryDatabase.campsiteRepository
    private val campsitesRef = Firebase.firestore.collection("campsites")
    private val createdIds = mutableListOf<String>()

    /** unique word in every seeded campsite's name */
    private val token = "Zq${UUID.randomUUID().toString().take(6)}"

    @After
    fun cleanUp() {
        createdIds.forEach { Tasks.await(campsitesRef.document(it).delete(), 15, TimeUnit.SECONDS) }
    }

    private fun seed(name: String, location: String, guests: Int, price: Double = 45.5): Campsite {
        val id = "test-${UUID.randomUUID()}"
        val campsite = Campsite(
            campsiteId = id,
            ownerId = "test-owner",
            campsiteName = "$name $token",
            description = "Seeded description",
            mapLocation = listOf("52.87", "-118.08", location),
            amenities = mutableListOf("Fire pit", "Toilets"),
            tags = mutableListOf("lake"),
            rules = mutableListOf("No smoking"),
            policies = mutableListOf("No refunds"),
            maxGuests = guests,
            pricePerNight = price,
        )
        Tasks.await(campsitesRef.document(id).set(campsite), 15, TimeUnit.SECONDS)
        createdIds.add(id)
        composeRule.waitUntil(15_000) { repository.campsites.value.any { it.campsiteId == id } }
        return campsite
    }

    private fun seedThree() {
        seed("Pine", "Jasper, AB", 8)
        seed("Lake", "Banff, AB", 4)
        seed("Aspen", "Jasper, AB", 6)
    }

    private fun search(name: String = token, location: String = "", guests: String = "") {
        composeRule.onNodeWithTag("searchNameBar").performTextInput(name)
        if (location.isNotEmpty()) composeRule.onNodeWithTag("searchLocationBar").performTextInput(location)
        if (guests.isNotEmpty()) composeRule.onNodeWithTag("searchGuestsField").performTextInput(guests)
        composeRule.onNodeWithTag("searchButton").performClick()
    }

    // ---------- list and summary (01.01.03, 01.01.04) ----------

    @Test
    fun search_showsSummaryWithNameLocationGuestsAndPrice() {
        seed("Pine", "Jasper, AB", 8, price = 65.0)
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search()

        composeRule.onNodeWithText("Pine $token").assertIsDisplayed()
        composeRule.onNodeWithText("Jasper, AB").assertIsDisplayed()
        composeRule.onNodeWithText("Max Guests: 8").assertIsDisplayed()
        composeRule.onNodeWithText("$65.00 / night").assertIsDisplayed()
    }

    @Test
    fun search_tappingACampsiteReportsIt() {
        val seeded = seed("Tap", "Jasper, AB", 4)
        var tapped: Campsite? = null
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = { tapped = it }) }
        search()

        composeRule.onNodeWithText("Tap $token").performClick()
        assertEquals(seeded.campsiteId, tapped?.campsiteId)
    }

    // ---------- search and filters (01.01.01, 01.01.02) ----------

    @Test
    fun search_byName_isCaseInsensitive() {
        seedThree()
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search(name = "pine $token".lowercase())

        composeRule.onNodeWithText("Pine $token").assertIsDisplayed()
        composeRule.onNodeWithText("Lake $token").assertDoesNotExist()
        composeRule.onNodeWithText("Aspen $token").assertDoesNotExist()
    }

    @Test
    fun search_byLocation_listsOnlyThatLocation() {
        seedThree()
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search(location = "jasper")

        composeRule.onNodeWithText("Pine $token").assertIsDisplayed()
        composeRule.onNodeWithText("Aspen $token").assertIsDisplayed()
        composeRule.onNodeWithText("Lake $token").assertDoesNotExist()
    }

    @Test
    fun search_byGuests_listsOnlyCampsitesThatFitEveryone() {
        seedThree()
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search(guests = "7")

        composeRule.onNodeWithText("Pine $token").assertIsDisplayed()
        composeRule.onNodeWithText("Aspen $token").assertDoesNotExist()
        composeRule.onNodeWithText("Lake $token").assertDoesNotExist()
    }

    @Test
    fun search_combinedFilters_mustAllMatch() {
        seedThree()
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search(location = "jasper", guests = "7")

        composeRule.onNodeWithText("Pine $token").assertIsDisplayed()
        composeRule.onNodeWithText("Aspen $token").assertDoesNotExist()
    }

    @Test
    fun search_noMatches_showsMessage() {
        seedThree()
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search(name = "nothing-called-this-$token")

        composeRule.onNodeWithText("No campsites match your search.").assertIsDisplayed()
    }

    @Test
    fun search_invalidGuests_showsErrorAndKeepsList() {
        seedThree()
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        search(guests = "0")

        composeRule.onNodeWithTag("searchError").assertIsDisplayed()
        composeRule.onNodeWithText("Number of guests must be a whole number above 0.").assertIsDisplayed()
    }

    @Test
    fun search_invalidDates_showError() {
        composeRule.setContent { CampsiteSearch(onNavigateToCampsite = {}) }
        composeRule.onNodeWithTag("searchCheckInField").performTextInput("2026-12-05")
        composeRule.onNodeWithTag("searchCheckOutField").performTextInput("2026-12-02")
        composeRule.onNodeWithTag("searchButton").performClick()

        composeRule.onNodeWithText("Check-out must be after check-in.").assertIsDisplayed()
    }

    // ---------- details (01.01.05) ----------

    @Test
    fun entrantCampsiteView_showsEveryDetail() {
        val seeded = seed("Detail", "Jasper, AB", 6)
        composeRule.setContent { EntrantCampsiteView(campsiteId = seeded.campsiteId, entrantId = "test-entrant") }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        composeRule.onNodeWithText("Detail $token").assertIsDisplayed()
        composeRule.onNodeWithText("Seeded description").assertIsDisplayed()
        composeRule.onNodeWithText("Maximum Guests: 6").assertIsDisplayed()
        composeRule.onNodeWithText("Price per Night: $45.50").assertIsDisplayed()
        composeRule.onNodeWithText("Jasper, AB", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Amenities: Fire pit, Toilets").assertIsDisplayed()
        composeRule.onNodeWithText("Tags: lake").assertIsDisplayed()
        composeRule.onNodeWithText("Rules: No smoking").assertIsDisplayed()
        composeRule.onNodeWithText("Policies: No refunds").assertIsDisplayed()
    }

    @Test
    fun entrantCampsiteView_hasNoEditButton() {
        val seeded = seed("NoEdit", "Jasper, AB", 6)
        composeRule.setContent { EntrantCampsiteView(campsiteId = seeded.campsiteId, entrantId = "test-entrant") }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        composeRule.onNodeWithText("NoEdit $token").assertIsDisplayed()
        composeRule.onNodeWithText("Edit Campsite").assertDoesNotExist()
    }

    @Test
    fun entrantCampsiteView_unknownCampsite_showsNotFound() {
        composeRule.setContent { EntrantCampsiteView(campsiteId = "does-not-exist", entrantId = "test-entrant") }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        composeRule.onNodeWithText("This campsite could not be found.").assertIsDisplayed()
    }
}
