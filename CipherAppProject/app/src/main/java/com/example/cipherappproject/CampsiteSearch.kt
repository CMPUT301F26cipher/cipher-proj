package com.example.cipherappproject
/*
--------------------------------------------------------------------------------------
Program Name: CampsiteSearch.kt
Date Written: Nov 2 2026
Author: Bailey Parrish
Purpose: Home screen for entrants, allows searching from user input, and displays a list of campsites
that match the given filters. Campsites in list are clickable, allowing navigation to the details for that campsite.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-02  Bailey      1.01     Built
2026-11-17  Madison     1.02     made it run, added loading/empty messages, keeps search on back
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Entrant home screen: search filters and the list of campsites that match them.
 *
 * @param onNavigateToCampsite called with the campsite the entrant taps on
 */
@Composable
fun CampsiteSearch(
    onNavigateToCampsite: (Campsite) -> Unit,
){
    val campsiteRepository = RepositoryDatabase.campsiteRepository
    val availabilityRepository = RepositoryDatabase.campsiteAvailabilityRepository
    val allCampsites = campsiteRepository.campsites.value
    var searchFilter by rememberSaveable { mutableStateOf(SearchFilter()) }

    val isLoading = !campsiteRepository.isCampsitesLoaded.value ||
        (searchFilter.checkIn != null && !availabilityRepository.isCampsiteAvailabilitiesLoaded.value)

    val filteredCampsites = CampsiteListFromFilters(
        allCampsites,
        searchFilter,
        availabilityRepository::getAvailableBookingDates
    ).getList()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchFiltersView(
            searchFilter,
            onSearch = { searchFilter = it }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("searchResults"),
        ) {
            when {
                isLoading -> item {
                    Text("Loading campsites…", modifier = Modifier.padding(16.dp))
                }
                filteredCampsites.isEmpty() -> item {
                    Text("No campsites match your search.", modifier = Modifier.padding(16.dp))
                }
                else -> items(filteredCampsites) { campsite ->
                    CampsiteSummary(
                        onClick = { onNavigateToCampsite(campsite) },
                        campsite = campsite
                    )
                }
            }
        }
    }
}