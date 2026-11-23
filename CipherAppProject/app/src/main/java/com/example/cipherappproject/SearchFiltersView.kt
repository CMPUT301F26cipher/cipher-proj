package com.example.cipherappproject
/*
--------------------------------------------------------------------------------------
Program Name: SearchFiltersView.kt
Date Written: Nov 2 2026
Author: Bailey Parrish
Purpose: Search bar and filter inputs for the entrant search screen
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-02  Bailey      1.01     Built (name only)
2026-11-18  Madison     1.02     Added location, guests, dates and error message
--------------------------------------------------------------------------------------
Future Enhancements
ability to search by tags, price
 */
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Lets the entrant type their filters. When Search is pressed, the input is checked and the new SearchFilter is passed up, or an error message is shown.
 *
 * @param searchFilter the current filter, used to fill in the fields
 * @param onSearch called with the new filter when the entrant presses Search
 */
@Composable
fun SearchFiltersView(
    searchFilter: SearchFilter,
    onSearch: (SearchFilter) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(searchFilter.name) }
    var location by rememberSaveable { mutableStateOf(searchFilter.location) }
    var guests by rememberSaveable { mutableStateOf(searchFilter.guests?.toString() ?: "") }
    var checkIn by rememberSaveable { mutableStateOf(searchFilter.checkIn?.toString() ?: "") }
    var checkOut by rememberSaveable { mutableStateOf(searchFilter.checkOut?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Campsite Name") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
                    .testTag("searchNameBar")
            )
            Button(
                modifier = Modifier.testTag("searchButton"),
                onClick = {
                    try {
                        onSearch(SearchFilter.fromInput(name, location, guests, checkIn, checkOut))
                        errorMessage = null
                    } catch (e: SearchException) {
                        errorMessage = e.message
                    }
                }
            ) {
                Text("Search")
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
            TextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
                    .testTag("searchLocationBar")
            )
            TextField(
                value = guests,
                onValueChange = { guests = it },
                label = { Text("Guests") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(0.6f)
                    .testTag("searchGuestsField")
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
            TextField(
                value = checkIn,
                onValueChange = { checkIn = it },
                label = { Text("Check-in") },
                placeholder = { Text("yyyy-MM-dd") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
                    .testTag("searchCheckInField")
            )
            TextField(
                value = checkOut,
                onValueChange = { checkOut = it },
                label = { Text("Check-out") },
                placeholder = { Text("yyyy-MM-dd") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("searchCheckOutField")
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .testTag("searchError")
            )
        }
    }
}
