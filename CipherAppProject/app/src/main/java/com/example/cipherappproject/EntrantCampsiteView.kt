package com.example.cipherappproject.ui.campsite

/*
--------------------------------------------------------------------------------------
Program Name: EntrantCampsiteView.kt
Date Written: Unknown
Author: Unknown
Purpose: Details page for a campsite, opened by tapping a campsite in the entrant
search results.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-18  Madison     1.02    Replaced placeholder
--------------------------------------------------------------------------------------
Future Enhancements
NA
*/
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.util.Locale

/**
 * Shows details of a campsite.
 *
 * @param campsiteId id of the campsite to show
 */

@Composable
fun EntrantCampsiteView(campsiteId: String) {
    val repository = RepositoryDatabase.campsiteRepository
    val campsite = repository.campsites.value.find { it.campsiteId == campsiteId }

    if (campsite == null) {
        Text(
            text = if (repository.isCampsitesLoaded.value) "Campsite not found." else "Loading…",
            modifier = Modifier
                .padding(16.dp)
                .testTag("entrantCampsiteView")
        )
    } else {
        val latitude = campsite.mapLocation.getOrNull(0) ?: "?"
        val longitude = campsite.mapLocation.getOrNull(1) ?: "?"
        val locationName = campsite.mapLocation.getOrNull(2).orEmpty()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("entrantCampsiteView"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = campsite.campsiteName,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(String.format(Locale.CANADA, "$%.2f / night", campsite.pricePerNight))
            Text("Location: $locationName ($latitude, $longitude)")
            Text("Maximum Occupancy: ${campsite.maxGuests}")
            Text("Description: ${campsite.description}")
            Text("Amenities: ${campsite.amenities.joinToString(", ")}")
            Text("Tags: ${campsite.tags.joinToString(", ")}")
            if (campsite.rules.isNotEmpty()) {
                Text("Rules: ${campsite.rules.joinToString(", ")}")
            }
            if (campsite.policies.isNotEmpty()) {
                Text("Policies: ${campsite.policies.joinToString(", ")}")
            }
        }
    }
}