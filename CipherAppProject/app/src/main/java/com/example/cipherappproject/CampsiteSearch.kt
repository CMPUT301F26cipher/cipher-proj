package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteSearch(
    onNavigateToCampsite: (Campsite) -> Unit,
){
    val campsiteRepo = CampsiteRepository()
    var searchFilter by remember { mutableStateOf( SearchFilter() ) }
    var searchName by remember { mutableStateOf( "" ) }
    val campsiteNameList = remember { mutableListOf<String>() }
    val filteredCampsiteList by remember { mutableStateOf(campsiteRepo.campsites) }

    for (campsite in campsiteRepo.campsites){
        campsiteNameList.add(campsite.getCampsiteName())
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomizableSearchBar(
            query = searchName,
            onQueryChange = { searchName = it },
            onSearch = {},
            searchResults = campsiteNameList,
            onResultClick = {}
        )
        SearchBar(
            expanded = false,
            onExpandedChange = { false },
            inputField = {
                InputField(
                    query = searchName,
                    onQueryChange = { searchFilter.setName(searchName) },
                    onSearch = {

                    },
                    expanded = false,
                    onExpandedChange = { false },
                    placeholder = { Text("Campsite Name") },
                    leadingIcon = { Icons.Default.Search },
                    trailingIcon = { null }
                )
            }
        ) { }
//        SearchFiltersView(
//            searchFilter,
//            onSearch = {
//                searchFilter = it
//                searchStatus = SearchException()
//                try {
//                    filteredCampsiteList.filterCampsites()
//                    print(searchFilter.getName())
//                } catch (exception: SearchException){
//                    searchStatus = exception
//                }
//            }
//        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("searchResults"),
        ) {
            (filteredCampsiteList).forEach { currentCampsite ->
                item { CampsiteSummary(onClick = {onNavigateToCampsite(currentCampsite)}, currentCampsite) }
            }
        }
    }
}