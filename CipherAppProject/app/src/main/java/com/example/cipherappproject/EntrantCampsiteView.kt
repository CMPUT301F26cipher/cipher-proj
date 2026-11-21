package com.example.cipherappproject.ui.campsite

/*
--------------------------------------------------------------------------------------
Program Name: EntrantCampsiteView.kt
Date Written: 2026-11-18
Author: Madison
Purpose: Screen. Loads a campsite by id and shows its details and lottery section to an entrant.
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
 *
 * @param campsiteId id of the campsite to show
 * @param entrantId the current entrant, used to join the lottery
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

        CampsiteLotterySection(
            campsiteId = campsiteId,
            entrantId = entrantId,
            controller = waitlistController
        )
    }
}
