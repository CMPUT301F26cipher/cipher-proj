package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cipherappproject.LotteryConstants
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.example.cipherappproject.database.repositories.LotteryEntryRepository
import com.example.cipherappproject.database.repositories.LotteryRepository

/**
 * Entrant screen listing the lotteries they've joined.
 *
 * NOTE: entrantId is PLACEHOLDER_USER_ID (LotteryNavigation.kt) until US 01.09.01 is done.
 */
@Composable
fun EntrantLotteriesView(
    navController: NavController,
    entrantId: String
) {
    val entryRepository = remember { LotteryEntryRepository() }
    val lotteryRepository = remember { LotteryRepository() }
    val campsiteRepository = remember { CampsiteRepository() }
    val myEntries = entryRepository.lotteries.filter { it.userId == entrantId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("entrantLotteriesView"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "My Lotteries", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (myEntries.isEmpty()) {
                item { Text("You haven't joined any lotteries yet.") }
            }
            items(myEntries) { entry ->
                val lottery = lotteryRepository.lotteries.find { it.lotteryId == entry.lotteryId }
                val campsiteNames = lottery?.campsiteIds?.map { id ->
                    campsiteRepository.campsites.find { it.campsiteId == id }?.campsiteName
                        ?: "Unknown campsite"
                } ?: emptyList()

                EntryRow(
                    entry = entry,
                    campsiteNames = campsiteNames,
                    dates = if (lottery != null) "${lottery.startDate} to ${lottery.endDate}" else "",
                    onClick = { navController.navigate(LotteryRoutes.result(entry.lotteryId)) }
                )
            }
        }
    }
}

/** One lottery in the entrant's list. */
@Composable
private fun EntryRow(
    entry: LotteryEntry,
    campsiteNames: List<String>,
    dates: String,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (campsiteNames.isEmpty()) "Lottery" else campsiteNames.joinToString(", "),
                style = MaterialTheme.typography.titleMedium
            )
            if (dates.isNotEmpty()) {
                Text("Registration: $dates")
            }
            Text("Status: ${entryStatusText(entry.status)}")
        }
    }
}

/** Readable text for a lottery entry status. */
private fun entryStatusText(status: String): String {
    return when (status) {
        LotteryConstants.ENTRY_REGISTERED -> "Waiting for the draw"
        LotteryConstants.ENTRY_WINNER -> "You won! Tap to reserve"
        LotteryConstants.ENTRY_NOT_SELECTED -> "Not selected"
        LotteryConstants.ENTRY_ACCEPTED -> "Reserved"
        LotteryConstants.ENTRY_DECLINED -> "Declined"
        else -> status
    }
}
