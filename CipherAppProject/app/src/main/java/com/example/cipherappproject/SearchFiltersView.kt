package com.example.cipherappproject

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * User input module, allows user to select desired filter parameters, and then on user click
 * activates a search through the database
 *
 * Inherits a searchFilter object from parent, allows mutation of the searchFilter by user input and returns
 * mutated searchFilter upon user input
 *
 * @param searchFilter inherited set of parameters to be mutated
 * @param onSearch allows upstream return of searchFilter
 */
/*
 * Built by Bailey
 * Nov 2 2026
 * Updates:
 *
 */
@Composable
fun SearchFiltersView(
    searchFilter: SearchFilter,
    onSearch: (SearchFilter)->Unit,
) {
    var searchName by remember { mutableStateOf("") }
    var tempSearchFilter by remember { mutableStateOf(searchFilter)}
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = searchName,
            onValueChange = { searchName = it },
            label = { Text("Campsite Name") },
            modifier = Modifier
                .padding(
                    start = 5.dp,
                    end = 5.dp,
                    top = 5.dp,
                    bottom = 0.dp
                )
                .testTag("searchNameBar")
        )
        Button(
            onClick = {
                tempSearchFilter.setName(searchName)
                onSearch(tempSearchFilter)
            }
        ){
            Text("Search")
        }
    }

    //TODO: finish implementing, with ability to search by tags, price, etc.
}