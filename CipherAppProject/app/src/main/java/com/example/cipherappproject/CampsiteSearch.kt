package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository

/**
 * Home screen for entrants, allows searching from user input, and displays a list of campsites
 * that match the given filters. Campsites in list are clickable, allowing navigation to the
 * details for that campsite.
 */
/*
 * Built by Bailey Nov 2 2026
 * Updates:
 * Bailey Nov 4 2026
 */
@Composable
fun CampsiteSearch(
    onNavigateToCampsite: (Campsite) -> Unit,
){
    val fullCampsiteList = CampsiteRepository().campsites
    var searchFilter by remember { mutableStateOf(SearchFilter()) }
    val filteredCampsiteList = remember { CampsiteListFromFilters(fullCampsiteList,searchFilter) }
    var searchStatus by remember { mutableStateOf(SearchException()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchFiltersView(
            searchFilter,
            onSearch = {
                searchFilter = it
                searchStatus = SearchException()
                try {
                    filteredCampsiteList.filterCampsites()
                } catch (exception: SearchException){
                    searchStatus = exception
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("searchResults"),
        ) {
            for (currentCampsite in filteredCampsiteList.getList()){
                item { CampsiteSummary(onClick = {onNavigateToCampsite(currentCampsite)}, currentCampsite) }
            }
        }
    }
}