package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.WaitlistController
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.lottery.CenteredMessage
import com.example.cipherappproject.ui.lottery.LoadingIndicator

/**
 * Campsite details screen for entrants (opened from search), with the lottery
 * join section (US 01.01.06 / 01.02.01).
 *
 * The campsite comes from the shared CampsiteRepository's local list (Compose State), so
 * it appears once the campsites have loaded.
 */
@Composable
fun EntrantCampsiteView(campsiteId: String, entrantId: String) {
    val campsiteRepository = RepositoryDatabase.campsiteRepository

    if (!campsiteRepository.isCampsitesLoaded.value) {
        LoadingIndicator()
        return
    }

    val campsite = campsiteRepository.getCampsiteFromListById(campsiteId)
    if (campsite == null) {
        CenteredMessage("This campsite could not be found.")
        return
    }

    val waitlistController = remember { WaitlistController() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("entrantCampsiteView"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CampsiteDetailView(campsite = campsite, isOwner = false, onEditClick = {})

        if (campsite.amenities.isNotEmpty()) {
            Text("Amenities: ${campsite.amenities.joinToString(", ")}")
        }
        if (campsite.mapLocation.size >= 2) {
            Text("Location: ${campsite.mapLocation[0]}, ${campsite.mapLocation[1]}")
        }
        if (campsite.tags.isNotEmpty()) {
            Text("Tags: ${campsite.tags.joinToString(", ")}")
        }

        CampsiteLotterySection(
            campsiteId = campsiteId,
            entrantId = entrantId,
            controller = waitlistController
        )
    }
}
