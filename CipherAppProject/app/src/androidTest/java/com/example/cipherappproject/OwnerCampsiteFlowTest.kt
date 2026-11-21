package com.example.cipherappproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.reservations.OwnerOverview
import com.example.cipherappproject.ui.campsite.EditCampsiteView
import com.example.cipherappproject.ui.campsite.OwnerCampsiteView
import com.example.cipherappproject.ui.campsite.RegisterCampsiteView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * UI tests for the owner campsite screens, run against Firestore with throwaway owners.
 * Covers US 02.01.01 (register), 02.01.02 (details), 02.01.03 (update),
 * 02.01.04 (view) and 02.03.01 (list).
 */
@RunWith(AndroidJUnit4::class)
class OwnerCampsiteFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val repository = RepositoryDatabase.campsiteRepository
    private val campsitesRef = Firebase.firestore.collection("campsites")
    private val createdIds = mutableListOf<String>()

    /** a new owner id for each test so nothing else's data is touched */
    private val ownerId = "test-owner-${UUID.randomUUID()}"

    @After
    fun cleanUp() {
        createdIds.forEach { Tasks.await(campsitesRef.document(it).delete(), 15, TimeUnit.SECONDS) }
        // campsites saved through the register screen
        repository.campsites.value.filter { it.ownerId == ownerId }.forEach {
            Tasks.await(campsitesRef.document(it.campsiteId).delete(), 15, TimeUnit.SECONDS)
        }
    }

    private fun seed(name: String, owner: String = ownerId, guests: Int = 4): Campsite {
        val id = "test-${UUID.randomUUID()}"
        val campsite = Campsite(
            campsiteId = id,
            ownerId = owner,
            campsiteName = name,
            description = "Seeded description",
            mapLocation = listOf("52.87", "-118.08", "Jasper, AB"),
            amenities = mutableListOf("Fire pit", "Toilets"),
            tags = mutableListOf("lake"),
            maxGuests = guests,
            pricePerNight = 45.5,
        )
        Tasks.await(campsitesRef.document(id).set(campsite), 15, TimeUnit.SECONDS)
        createdIds.add(id)
        composeRule.waitUntil(15_000) { repository.campsites.value.any { it.campsiteId == id } }
        return campsite
    }

    private fun type(tag: String, text: String) {
        composeRule.onNodeWithTag(tag).performScrollTo().performTextInput(text)
    }

    private fun replace(tag: String, text: String) {
        composeRule.onNodeWithTag(tag).performScrollTo().performTextClearance()
        composeRule.onNodeWithTag(tag).performTextInput(text)
    }

    // ---------- list (02.03.01) ----------

    @Test
    fun ownerOverview_withNoCampsites_showsEmptyMessage() {
        composeRule.setContent { OwnerOverview(ownerId = ownerId) }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }
        composeRule.onNodeWithText("You have no campsites yet.", substring = true).assertIsDisplayed()
    }

    @Test
    fun ownerOverview_listsOnlyThisOwnersCampsites() {
        seed("Mine One")
        seed("Someone Elses", owner = "test-other-${UUID.randomUUID()}")
        composeRule.setContent { OwnerOverview(ownerId = ownerId) }

        composeRule.onNodeWithText("Mine One").assertIsDisplayed()
        composeRule.onNodeWithText("Jasper, AB").assertIsDisplayed()
        composeRule.onNodeWithText("Max Guests: 4").assertIsDisplayed()
        composeRule.onNodeWithText("Someone Elses").assertDoesNotExist()
    }

    @Test
    fun ownerOverview_tappingCampsiteReportsIt() {
        val seeded = seed("Tap Me")
        var tapped: Campsite? = null
        composeRule.setContent { OwnerOverview(ownerId = ownerId, onCampsiteClick = { tapped = it }) }

        composeRule.onNodeWithText("Tap Me").performClick()
        assertEquals(seeded.campsiteId, tapped?.campsiteId)
    }

    @Test
    fun ownerOverview_registerButtonReportsClick() {
        var clicked = false
        composeRule.setContent { OwnerOverview(ownerId = ownerId, onRegisterClick = { clicked = true }) }
        composeRule.onNodeWithTag("openRegisterCampsiteButton").performClick()
        assertTrue(clicked)
    }

    // ---------- register (02.01.01, 02.01.02) ----------

    @Test
    fun register_emptyForm_showsErrorAndDoesNotSave() {
        var saved = false
        composeRule.setContent {
            RegisterCampsiteView(ownerId = ownerId, onSaveSuccess = { saved = true })
        }
        composeRule.onNodeWithTag("registerCampsiteButton").performScrollTo().performClick()

        composeRule.onNodeWithTag("registerCampsiteError").assertIsDisplayed()
        composeRule.onNodeWithText("Campsite name cannot be empty.").assertIsDisplayed()
        assertTrue(!saved)
    }

    @Test
    fun register_invalidGuests_showsError() {
        composeRule.setContent { RegisterCampsiteView(ownerId = ownerId, onSaveSuccess = {}) }
        type("campsiteNameField", "Bad Guests")
        type("campsiteLocationField", "Banff, AB")
        type("campsiteLatitudeField", "51.17")
        type("campsiteLongitudeField", "-115.57")
        type("campsiteMaxGuestsField", "0")
        type("campsitePriceField", "10")
        composeRule.onNodeWithTag("registerCampsiteButton").performScrollTo().performClick()

        composeRule.onNodeWithText("Maximum guests must be a whole number above 0.").assertIsDisplayed()
    }

    @Test
    fun register_cancelReportsCancel() {
        var cancelled = false
        composeRule.setContent {
            RegisterCampsiteView(ownerId = ownerId, onSaveSuccess = {}, onCancel = { cancelled = true })
        }
        composeRule.onNodeWithText("Cancel").performScrollTo().performClick()
        assertTrue(cancelled)
    }

    @Test
    fun register_validForm_savesCampsiteForOwner() {
        var saved = false
        composeRule.setContent {
            RegisterCampsiteView(ownerId = ownerId, onSaveSuccess = { saved = true })
        }
        type("campsiteNameField", "Owl Creek Camp")
        type("campsiteLocationField", "Jasper, AB")
        type("campsiteLatitudeField", "52.87")
        type("campsiteLongitudeField", "-118.08")
        type("campsiteDescriptionField", "Quiet lakeside site")
        type("campsiteAmenitiesField", "Fire pit, Toilets")
        type("campsiteMaxGuestsField", "6")
        type("campsitePriceField", "45.50")
        type("campsiteImageField", "img/owl.jpg")
        type("campsiteTagsField", "lake, quiet")
        composeRule.onNodeWithTag("registerCampsiteButton").performScrollTo().performClick()

        composeRule.waitUntil(15_000) { saved }
        composeRule.waitUntil(15_000) { repository.getCampsiteFromListByOwnerId(ownerId).isNotEmpty() }

        val campsite = repository.getCampsiteFromListByOwnerId(ownerId).single()
        assertEquals("Owl Creek Camp", campsite.campsiteName)
        assertEquals(listOf("52.87", "-118.08", "Jasper, AB"), campsite.mapLocation)
        assertEquals("Quiet lakeside site", campsite.description)
        assertEquals(listOf("Fire pit", "Toilets"), campsite.amenities)
        assertEquals(6, campsite.maxGuests)
        assertEquals(45.5, campsite.pricePerNight, 0.0)
        assertEquals("img/owl.jpg", campsite.imagePath)
        assertEquals(listOf("lake", "quiet"), campsite.tags)
    }

    @Test
    fun register_imageIsOptional() {
        var saved = false
        composeRule.setContent {
            RegisterCampsiteView(ownerId = ownerId, onSaveSuccess = { saved = true })
        }
        type("campsiteNameField", "No Image Camp")
        type("campsiteLocationField", "Banff, AB")
        type("campsiteLatitudeField", "51.17")
        type("campsiteLongitudeField", "-115.57")
        type("campsiteMaxGuestsField", "2")
        type("campsitePriceField", "0")
        composeRule.onNodeWithTag("registerCampsiteButton").performScrollTo().performClick()

        composeRule.waitUntil(15_000) { saved }
        composeRule.waitUntil(15_000) { repository.getCampsiteFromListByOwnerId(ownerId).isNotEmpty() }
        assertEquals("", repository.getCampsiteFromListByOwnerId(ownerId).single().imagePath)
    }

    // ---------- view (02.01.04) ----------

    @Test
    fun ownerCampsiteView_showsDetailsAndEditButton() {
        val seeded = seed("Detail Camp")
        var editClicked = false
        composeRule.setContent {
            OwnerCampsiteView(campsiteId = seeded.campsiteId, ownerId = ownerId, onEditClick = { editClicked = true })
        }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        composeRule.onNodeWithText("Detail Camp").assertIsDisplayed()
        composeRule.onNodeWithText("Seeded description").assertIsDisplayed()
        composeRule.onNodeWithText("Maximum Guests: 4").assertIsDisplayed()
        composeRule.onNodeWithText("Price per Night: $45.50").assertIsDisplayed()
        composeRule.onNodeWithText("Jasper, AB", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Amenities: Fire pit, Toilets").assertIsDisplayed()
        composeRule.onNodeWithText("Tags: lake").assertIsDisplayed()

        composeRule.onNodeWithText("Edit Campsite").performClick()
        assertTrue(editClicked)
    }

    @Test
    fun ownerCampsiteView_otherOwnersCampsite_isNotShown() {
        val seeded = seed("Not Yours", owner = "test-other-${UUID.randomUUID()}")
        composeRule.setContent {
            OwnerCampsiteView(campsiteId = seeded.campsiteId, ownerId = ownerId, onEditClick = {})
        }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }
        composeRule.onNodeWithText("This campsite could not be found.").assertIsDisplayed()
    }

    // ---------- update (02.01.03) ----------

    @Test
    fun edit_startsFilledInWithCurrentDetails() {
        val seeded = seed("Prefilled Camp")
        composeRule.setContent {
            EditCampsiteView(campsiteId = seeded.campsiteId, ownerId = ownerId, onSaveSuccess = {})
        }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        composeRule.onNodeWithText("Prefilled Camp").assertIsDisplayed()
        composeRule.onNodeWithText("Jasper, AB").assertIsDisplayed()
        composeRule.onNodeWithText("Seeded description").assertIsDisplayed()
    }

    @Test
    fun edit_validChange_isSavedToDatabase() {
        val seeded = seed("Before Edit")
        var saved = false
        composeRule.setContent {
            EditCampsiteView(campsiteId = seeded.campsiteId, ownerId = ownerId, onSaveSuccess = { saved = true })
        }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        replace("campsiteNameField", "After Edit")
        replace("campsiteMaxGuestsField", "9")
        composeRule.onNodeWithTag("saveCampsiteButton").performScrollTo().performClick()

        composeRule.waitUntil(15_000) { saved }
        composeRule.waitUntil(15_000) {
            repository.getCampsiteFromListById(seeded.campsiteId)?.campsiteName == "After Edit"
        }
        val updated = repository.getCampsiteFromListById(seeded.campsiteId)
        assertNotNull(updated)
        assertEquals(9, updated!!.maxGuests)
        // untouched fields are kept
        assertEquals(ownerId, updated.ownerId)
        assertEquals("Seeded description", updated.description)
        assertEquals(listOf("Fire pit", "Toilets"), updated.amenities)
    }

    @Test
    fun edit_invalidChange_showsErrorAndDoesNotSave() {
        val seeded = seed("Keep Me")
        var saved = false
        composeRule.setContent {
            EditCampsiteView(campsiteId = seeded.campsiteId, ownerId = ownerId, onSaveSuccess = { saved = true })
        }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }

        replace("campsiteMaxGuestsField", "0")
        composeRule.onNodeWithTag("saveCampsiteButton").performScrollTo().performClick()

        composeRule.onNodeWithText("Maximum guests must be a whole number above 0.").assertIsDisplayed()
        assertTrue(!saved)
        assertEquals(4, repository.getCampsiteFromListById(seeded.campsiteId)!!.maxGuests)
    }

    @Test
    fun edit_cancelReportsCancel() {
        val seeded = seed("Cancel Edit")
        var cancelled = false
        composeRule.setContent {
            EditCampsiteView(
                campsiteId = seeded.campsiteId, ownerId = ownerId,
                onSaveSuccess = {}, onCancel = { cancelled = true }
            )
        }
        composeRule.waitUntil(15_000) { repository.isCampsitesLoaded.value }
        composeRule.onNodeWithText("Cancel").performScrollTo().performClick()
        assertTrue(cancelled)
    }
}
