package com.example.cipherappproject.ui.campsite
/*
--------------------------------------------------------------------------------------
Program Name: OwnerCampsiteView.kt
Date Written: 2026-11-20
Author: Madison Levy
Purpose: Screen an owner sees after tapping one of their campsites.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-20  Madison    1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
Show campsite image (campsite.imagePath), show location on a map
 */
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import com.example.cipherappproject.database.enums.LotteryStatus
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.lottery.CenteredMessage
import com.example.cipherappproject.ui.lottery.LoadingIndicator

/**
 * Shows the details of one of the owner's campsites
 *
 * @param campsiteId id of the campsite to show
 * @param ownerId owner's profile id
 * @param onEditClick called when the owner presses Edit Campsite
 */
@Composable
fun OwnerCampsiteView(
    campsiteId: String,
    ownerId: String,
    onEditClick: () -> Unit,
    onLotteryClick: (String) -> Unit = {}
) {
    val campsiteRepository = RepositoryDatabase.campsiteRepository

    if (!campsiteRepository.isCampsitesLoaded.value) {
        LoadingIndicator()
        return
    }

    val campsite = campsiteRepository.getCampsiteFromListByOwnerId(ownerId)
        .find { it.campsiteId == campsiteId }
    if (campsite == null) {
        CenteredMessage("This campsite could not be found.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("ownerCampsiteView")
    ) {
        CampsiteDetailView(
            campsite = campsite,
            isOwner = true,
            onEditClick = onEditClick
        )

        // active lotteries for this campsite; tapping one opens its waitlist (US 02.09.01)
        val activeLotteries = RepositoryDatabase.lotteryRepository.lotteries.value.filter {
            campsiteId in it.campsiteIds && it.status == LotteryStatus.OPEN.name
        }
        Text(
            "Active lotteries",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        if (activeLotteries.isEmpty()) {
            Text("No active lotteries for this campsite.", modifier = Modifier.testTag("noActiveLotteries"))
        }
        activeLotteries.forEach { lottery ->
            OutlinedButton(
                onClick = { onLotteryClick(lottery.lotteryId) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("activeLottery_${lottery.lotteryId}")
            ) {
                Text("Lottery ${lottery.startDate} to ${lottery.endDate} - view waitlist")
            }
        }
    }
}
