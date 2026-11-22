package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.cipherappproject.WaitlistController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Entrant screen listing the lotteries they've joined.
 *
 * entrantId is the signed-in user's profile id (looked up by device in CampsiteApp).
 */
@Composable
fun EntrantLotteriesView(
    navController: NavController,
    entrantId: String
) {
    // Shared repository instances; their lists are Compose State, so this recomposes on change
    val entryRepository = RepositoryDatabase.lotteryEntryRepository
    val lotteryRepository = RepositoryDatabase.lotteryRepository
    val campsiteRepository = RepositoryDatabase.campsiteRepository

    if (!entryRepository.isLotteriesEntriesLoaded.value || !lotteryRepository.isLotteriesLoaded.value) {
        LoadingIndicator()
        return
    }
    val waitlistController = remember { WaitlistController() }
    var entryToRemove by remember { mutableStateOf<LotteryEntry?>(null) }
    var message by remember { mutableStateOf("") }
    val myEntries = entryRepository.getLotteryEntriesFromListByUserId(entrantId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("entrantLotteriesView"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "My Lotteries", style = MaterialTheme.typography.headlineMedium)

        if (message.isNotEmpty()) Text(message, modifier = Modifier.testTag("leaveMessage"))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (myEntries.isEmpty()) {
                item { Text("You haven't joined any lotteries yet.") }
            }
            items(myEntries) { entry ->
                val lottery = lotteryRepository.getLotteryFromListById(entry.lotteryId)
                val campsiteNames = lottery?.campsiteIds?.map { id ->
                    campsiteRepository.getCampsiteFromListById(id)?.campsiteName
                        ?: "Unknown campsite"
                } ?: emptyList()

                EntryRow(
                    entry = entry,
                    campsiteNames = campsiteNames,
                    dates = if (lottery != null) "${lottery.startDate} to ${lottery.endDate}" else "",
                    onClick = { navController.navigate(LotteryRoutes.result(entry.lotteryId)) },
                    onRemoveClick = { entryToRemove = entry }
                )
            }
        }
    }

    entryToRemove?.let { entry ->
        AlertDialog(
            modifier = Modifier.testTag("leaveDialog"),
            onDismissRequest = { entryToRemove = null },
            title = { Text("Leave waiting list?") },
            text = { Text("You will be removed from this lottery's waiting list.") },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag("confirmLeave"),
                    onClick = {
                        entryToRemove = null
                        waitlistController.leaveWaitlist(entry) { ok ->
                            message = if (ok) "You left the waiting list."
                            else "Couldn't leave the waiting list."
                        }
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { entryToRemove = null }) { Text("Cancel") }
            }
        )
    }
}

/** One lottery in the entrant's list. */
@Composable
private fun EntryRow(
    entry: LotteryEntry,
    campsiteNames: List<String>,
    dates: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
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
            // Leaving is only possible on the initial waiting list, before the draw
            if (entry.status == LotteryEntryStatus.REGISTERED.name) {
                OutlinedButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.testTag("removeEntry_${entry.lotteryEntryId}")
                ) { Text("Remove") }
            }
        }
    }
}

/** Readable text for a lottery entry status. */
private fun entryStatusText(status: String): String {
    return when (status) {
        LotteryEntryStatus.REGISTERED.name -> "Waiting for the draw"
        LotteryEntryStatus.WINNER.name -> "You won! Tap to reserve"
        LotteryEntryStatus.NOT_SELECTED.name -> "Not selected"
        LotteryEntryStatus.ACCEPTED.name -> "Reserved"
        LotteryEntryStatus.DECLINED.name -> "Declined"
        LotteryEntryStatus.WITHDRAWN.name -> "Left the waiting list"
        else -> status
    }
}
