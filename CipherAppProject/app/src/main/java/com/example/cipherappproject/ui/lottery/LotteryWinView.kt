package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.LotteryBookingController
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.repositories.CampsiteRepository

/**
 * Screens for an entrant who won a lottery
 *
 * NOTE: the Reserve button calls [onReserve]; creating the reservation
 * belongs to the reservation components.
 */
@Composable
fun LotteryWinScreen(
    controller: LotteryBookingController,
    lottery: Lottery,
    entry: LotteryEntry,
    onDeclined: () -> Unit,
    onReserve: (campsiteId: String, checkIn: String, checkOut: String) -> Unit
) {
    val campsiteRepository = remember { CampsiteRepository() }
    val choices = controller.getCampsiteChoices(lottery)

    // Copies of the controller's state, so Compose redraws when they change
    var selectedCampsiteId by remember { mutableStateOf(choices.firstOrNull()) }
    var availableNights by remember { mutableStateOf<List<String>?>(null) }
    var selectedNights by remember { mutableStateOf(setOf<String>()) }
    var loadFailed by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    // Reload the nights whenever a different campsite is chosen
    LaunchedEffect(selectedCampsiteId) {
        val campsiteId = selectedCampsiteId ?: return@LaunchedEffect
        controller.selectCampsite(lottery, campsiteId)
        availableNights = null
        selectedNights = controller.getSelectedNights()
        loadFailed = false
        controller.loadAvailableNights(campsiteId) { nights ->
            if (nights == null) loadFailed = true else availableNights = nights.toList()
        }
    }

    // (id, display name) pairs; names fill in once the campsites have loaded
    val campsiteChoices = choices.map { id ->
        val name = campsiteRepository.campsites.find { it.getCampsiteId() == id }?.getCampsiteName()
        Pair(id, name ?: "Campsite")
    }

    LotteryWinView(
        campsiteChoices = campsiteChoices,
        selectedCampsiteId = selectedCampsiteId,
        onCampsiteSelected = { selectedCampsiteId = it },
        availableNights = availableNights,
        loadFailed = loadFailed,
        selectedNights = selectedNights,
        onNightClicked = { night ->
            if (night in controller.getSelectedNights()) {
                controller.deselectNight(night)
            } else {
                controller.selectNight(night)
            }
            selectedNights = controller.getSelectedNights()
        },
        message = message,
        onReserve = {
            val stay = controller.getStay()
            val campsiteId = controller.getSelectedCampsiteId()
            if (stay != null && campsiteId != null) {
                onReserve(campsiteId, stay.first, stay.second)
            }
        },
        onDecline = {
            controller.declineOffer(entry) { success ->
                if (success) {
                    onDeclined()
                } else {
                    message = "Could not decline. Check your connection and try again."
                }
            }
        }
    )
}

/**
 * Draws the winner screen.
 *
 * @param campsiteChoices (campsite ID, campsite name) pairs
 * @param availableNights "yyyy-MM-dd" nights, earliest first; null while loading
 * @param selectedNights "yyyy-MM-dd" nights the entrant has ticked
 */
@Composable
fun LotteryWinView(
    campsiteChoices: List<Pair<String, String>>,
    selectedCampsiteId: String?,
    onCampsiteSelected: (String) -> Unit,
    availableNights: List<String>?,
    loadFailed: Boolean,
    selectedNights: Set<String>,
    onNightClicked: (String) -> Unit,
    message: String?,
    onReserve: () -> Unit,
    onDecline: () -> Unit
) {
    var showDeclineDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "You won the lottery!", style = MaterialTheme.typography.headlineMedium)
        Text("Pick the nights you want, or decline if none of them work for you.")

        // Only shown when the lottery covers more than one campsite (US 01.04.02)
        if (campsiteChoices.size > 1) {
            Text(text = "Choose a campsite", style = MaterialTheme.typography.titleMedium)
            for ((id, name) in campsiteChoices) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCampsiteSelected(id) }
                        .testTag("campsiteChoice_$id")
                ) {
                    RadioButton(selected = id == selectedCampsiteId, onClick = null)
                    Text(text = name, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Text(text = "Available nights", style = MaterialTheme.typography.titleMedium)

        // Takes the leftover space so the buttons stay at the bottom
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                loadFailed -> Text("Could not load the available dates.")
                availableNights == null -> CircularProgressIndicator()
                availableNights.isEmpty() -> Text("There are no nights left to book for this campsite.")
                else -> LazyColumn(modifier = Modifier.testTag("availableNightsList")) {
                    items(availableNights) { night ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNightClicked(night) }
                                .testTag("night_$night")
                        ) {
                            Checkbox(checked = night in selectedNights, onCheckedChange = null)
                            Text(
                                text = DateUtils.parse(night)?.let { DateUtils.display(it) } ?: night,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (selectedNights.isNotEmpty()) {
            Text("${selectedNights.size} night(s) selected")
        }
        if (message != null) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onReserve,
            enabled = selectedNights.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reserveButton")
        ) {
            Text("Reserve")
        }
        OutlinedButton(
            onClick = { showDeclineDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("declineButton")
        ) {
            Text("Decline")
        }
    }

    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Decline this offer?") },
            text = { Text("Your spot will be offered to someone else. This can't be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeclineDialog = false
                        onDecline()
                    },
                    modifier = Modifier.testTag("confirmDeclineButton")
                ) {
                    Text("Decline")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) {
                    Text("Keep offer")
                }
            }
        )
    }
}
