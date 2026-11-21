package com.example.cipherappproject.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.CampsiteSummary
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.provider.RepositoryDatabase

/*
--------------------------------------------------------------------------------------
Program Name: OwnerOverview.kt
Date Written: Unknown
Author: Hye-Sung?
Purpose: The owner's Campsites screen. Lists the owner's campsites, opens a campsite's details when tapped, and has a Register button
for adding a new campsite.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Removed dummy list, added DB integration
2026-11-20  Madison    1.02     Added register/campsite clicks, loading and empty messages, and campsite summaries
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Lists the owner's campsites.
 *
 * @param ownerId owner's profile id, only their campsites are listed
 * @param onRegisterClick called when the owner presses Register Campsite
 * @param onCampsiteClick called with the campsite the owner taps on
 */
@Composable
fun OwnerOverview(
    ownerId: String,
    onRegisterClick: () -> Unit = {},
    onCampsiteClick: (Campsite) -> Unit = {}
) {
    val campsiteRepository = RepositoryDatabase.campsiteRepository
    val isLoaded = campsiteRepository.isCampsitesLoaded.value
    val campsites = campsiteRepository.getCampsiteFromListByOwnerId(ownerId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("ownerOverview"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "My Campsites",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("openRegisterCampsiteButton")
        ) {
            Text("Register a Campsite")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ownerCampsiteList")
        ) {
            when {
                !isLoaded -> item {
                    Text("Loading campsites…", modifier = Modifier.padding(16.dp))
                }
                campsites.isEmpty() -> item {
                    Text(
                        "You have no campsites yet. Press Register a Campsite to add one.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> items(campsites) { campsite ->
                    CampsiteSummary(
                        onClick = { onCampsiteClick(campsite) },
                        campsite = campsite
                    )
                }
            }
        }
    }
}
