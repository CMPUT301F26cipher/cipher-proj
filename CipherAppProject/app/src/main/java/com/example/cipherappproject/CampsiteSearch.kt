package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController

/**
 * Home screen for entrants, allows searching from user input, and displays a list of campsites
 * that match the given filters. Campsites in list are clickable, allowing navigation to the
 * details for that campsite.
 */

@Composable
fun CampsiteSearch(
    navController: NavController,
){
    val filteredCampsiteList = remember { mutableStateListOf<Campsite>() }
    var searchFilter by remember { mutableStateOf(SearchFilter()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchFiltersView(
            searchFilter,
            onSearch = {
                searchFilter = it
                filteredCampsiteList.clear()
                //TODO: give filters object to some class that returns list of campsites
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("searchResults"),
        ) {
            for (currentCampsite in filteredCampsiteList){
                item { CampsiteSummary(navController, currentCampsite) }
            }
        }
    }
}