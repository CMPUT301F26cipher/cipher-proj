package com.example.cipherappproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cipherappproject.database.models.Lottery
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Apply ui tests for the notification screens and owner's send dialog
class NotificationViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val noSuchUser = "test-user-that-does-not-exist"

    @Test
    fun inboxShowsEmptyMessage() {
        composeTestRule.setContent { NotificationView(noSuchUser) }
        composeTestRule.onNodeWithTag("notificationView").assertExists()
        composeTestRule.onNodeWithText("No notifications yet.").assertIsDisplayed()
    }

    @Test
    fun sentListShowsEmptyMessage() {
        composeTestRule.setContent { SentNotificationView(noSuchUser) }
        composeTestRule.onNodeWithTag("sentNotificationView").assertExists()
        composeTestRule.onNodeWithText("You haven't sent any notifications.").assertIsDisplayed()
    }

    @Test
    fun sendDialogRejectsBlankTitleAndStaysOpen() {
        var dismissed = false
        composeTestRule.setContent {
            SendNotificationDialog(Lottery(lotteryId = "test"), noSuchUser) { dismissed = true }
        }
        composeTestRule.onNodeWithTag("notifyMessage").performTextInput("Campsite flooded")
        composeTestRule.onNodeWithTag("notifySend").performClick()
        composeTestRule.onNodeWithText("Title cannot be empty.").assertIsDisplayed()
        assertFalse(dismissed)
    }

    @Test
    fun sendDialogRejectsBlankMessage() {
        composeTestRule.setContent {
            SendNotificationDialog(Lottery(lotteryId = "test"), noSuchUser) {}
        }
        composeTestRule.onNodeWithTag("notifyTitle").performTextInput("Closed")
        composeTestRule.onNodeWithTag("notifySend").performClick()
        composeTestRule.onNodeWithText("Message cannot be empty.").assertIsDisplayed()
    }
    @Test
    fun sendDialogClosesAfterValidSend() {
        var dismissed = false
        composeTestRule.setContent {
            SendNotificationDialog(Lottery(lotteryId = "test-no-entries"), noSuchUser) { dismissed = true }
        }
        composeTestRule.onNodeWithTag("notifyTitle").performTextInput("Closed")
        composeTestRule.onNodeWithTag("notifyMessage").performTextInput("Campsite flooded")
        composeTestRule.onNodeWithTag("notifySend").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun sendDialogCancelCloses() {
        var dismissed = false
        composeTestRule.setContent {
            SendNotificationDialog(Lottery(lotteryId = "test"), noSuchUser) { dismissed = true }
        }
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun settingsShowsLoadingForUnknownProfile() {
        composeTestRule.setContent { ProfileSettingsView(noSuchUser) }
        composeTestRule.onNodeWithTag("profileSettingsView").assertExists()
        composeTestRule.onNodeWithText("Loading profile...").assertIsDisplayed()
    }
}