package com.example.cipherappproject

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
/*
 * Built by Bailey
 * Nov 2 2026
 * Updates:
 *
 */
@RunWith(AndroidJUnit4::class)
class NavbarTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>() // access MainActivity

    @Test
    fun navigate_to_profile_and_back_to_search() {
        // navigate to profile
        composeTestRule
            .onNodeWithTag("profile")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("profileMenu")
            .assertExists()

        // navigate to search screen
        composeTestRule
            .onNodeWithTag("search")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("searchResults")
            .assertExists()
    }

    @Test
    fun navigate_to_reservations_and_back_to_search() {
        // navigate to profile
        composeTestRule
            .onNodeWithTag("entrant/reservations")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("reservationsMenu")
            .assertExists()

        // navigate to search screen
        composeTestRule
            .onNodeWithTag("search")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("searchResults")
            .assertExists()
    }

    @Test
    fun navigate_to_messages_and_back_to_search() {
        // navigate to profile
        composeTestRule
            .onNodeWithTag("entrant/messages")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("messageView")
            .assertExists()

        // navigate to search screen
        composeTestRule
            .onNodeWithTag("search")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("searchResults")
            .assertExists()
    }


    @Test
    fun navigate_through_each_in_sequence() {
        // navigate to profile
        composeTestRule
            .onNodeWithTag("entrant/reservations")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("reservationsMenu")
            .assertExists()

        // navigate to profile
        composeTestRule
            .onNodeWithTag("entrant/messages")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("messageView")
            .assertExists()

        // navigate to profile
        composeTestRule
            .onNodeWithTag("profile")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("profileMenu")
            .assertExists()
        // navigate to search screen
        composeTestRule
            .onNodeWithTag("search")
            .performClick()

        // verify navigation
        composeTestRule
            .onNodeWithTag("searchResults")
            .assertExists()
    }
}