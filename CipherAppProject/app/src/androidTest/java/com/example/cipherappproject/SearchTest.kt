package com.example.cipherappproject

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
/*
--------------------------------------------------------------------------------------
Program Name: SearchTest.kt
Date Written: November 5 2026
Author: Bailey Parrish
Purpose: Test Search with various inputs
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Bailey     1.00     added first version
--------------------------------------------------------------------------------------
Future Enhancements
Fix to make it work with any database state
 */
@RunWith(AndroidJUnit4::class)
class SearchTest {
    /*
     * These tests can be broken by database changes
     * If they are *all* broken, check that the database is in the state that makes them work (I don't know how to fix this)
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>() // access MainActivity

    @Before fun initializeSearch(){
        composeTestRule
            .onNodeWithTag("search")
            .performClick()
    }

    @Test
    fun test_with_no_search_terms(){
        composeTestRule
            .onNodeWithTag("searchNameBar")
            .performClick()
            .assertTextEquals("")
    }

    @Test
    fun search_name_with_no_results(){
        composeTestRule
            .onNodeWithTag("searchNameBar")
            .assertExists()

        composeTestRule
            .onNodeWithTag("searchNameBar")
            .performTextInput("_SHOULD@NOT&RETURN_ANYTHING")

        composeTestRule
            .onNodeWithTag("searchButton")
            .performClick()

        composeTestRule
            .onNodeWithText("campsite1")
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithText("tents")
            .assertDoesNotExist()
    }
}