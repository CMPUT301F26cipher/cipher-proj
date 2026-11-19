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
import com.example.cipherappproject.database.provider.RepositoryDatabase
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip


/**
 * Screens for an entrant who won a lottery
 *
 * Available dates come from the shared availability + reservation lists (Compose State),
 * so this redraws by itself once they have loaded or changed.
 */
@Composable
fun LotteryWinScreen(
    controller: LotteryBookingController,
    lottery: Lottery,
    entry: LotteryEntry
) {
    val choices = controller.getCampsiteChoices(lottery)

    // Copies of the controller's state, so Compose redraws when they change
    var selectedCampsiteId by remember { mutableStateOf(choices.firstOrNull()) }
    var selectedDates by remember { mutableStateOf(setOf<String>()) }
    var message by remember { mutableStateOf<String?>(null) }

    // Make sure the controller knows about the initially shown campsite
    LaunchedEffect(selectedCampsiteId) {
        selectedCampsiteId?.let { controller.selectCampsite(lottery, it) }
    }

    // null while availability is loading; read here so Compose tracks the repositories' State
    val availableDates = selectedCampsiteId?.let { controller.getAvailableDates(it) }

    // (id, display name) pairs; names fill in once the campsites have loaded
    val campsiteChoices = choices.map { id ->
        val name = RepositoryDatabase.campsiteRepository.getCampsiteFromListById(id)?.campsiteName
        Pair(id, name ?: "Campsite")
    }

    LotteryWinView(
        campsiteChoices = campsiteChoices,
        selectedCampsiteId = selectedCampsiteId,
        onCampsiteSelected = { campsiteId ->
            if (campsiteId != selectedCampsiteId) {
                controller.selectCampsite(lottery, campsiteId)
                selectedDates = emptySet()
                message = null
                selectedCampsiteId = campsiteId
            }
        },
        availableDates = availableDates,
        selectedDates = selectedDates,
        onDateClicked = { date ->
            if (date in controller.getSelectedDates()) {
                controller.deselectDate(date)
            } else {
                controller.selectDate(date)
            }
            selectedDates = controller.getSelectedDates()
        },
        message = message,
        onReserve = {
            controller.reserve(lottery, entry) { reserveResult ->
                // On success the entry becomes ACCEPTED and LotteryResultScreen switches screens
                reserveResult.onFailure {
                    message = it.message ?: "Could not reserve. Check your connection and try again."
                }
            }
        },
        onDecline = {
            controller.declineOffer(entry) { declineResult ->
                // On success the entry becomes DECLINED and LotteryResultScreen switches screens
                declineResult.onFailure {
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
 * @param availableDates "yyyy-MM-dd" dates, earliest first; null while loading
 * @param selectedDates "yyyy-MM-dd" dates the entrant has ticked
 */
@Composable
fun LotteryWinView(
    campsiteChoices: List<Pair<String, String>>,
    selectedCampsiteId: String?,
    onCampsiteSelected: (String) -> Unit,
    availableDates: List<String>?,
    selectedDates: Set<String>,
    onDateClicked: (String) -> Unit,
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
        Text("Pick the dates you want, or decline if none of them work for you.")

        // Only shown when the lottery covers more than one campsite (US 01.04.02)
        if (campsiteChoices.size > 1) {
            LotteryCampsiteTabs(
                        campsiteChoices = campsiteChoices,
                        selectedCampsiteId = selectedCampsiteId,
                        onCampsiteSelected = onCampsiteSelected
            )
        }

        Text(text = "Available dates", style = MaterialTheme.typography.titleMedium)

        // Takes the leftover space so the buttons stay at the bottom
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                availableDates == null -> CircularProgressIndicator()
                availableDates.isEmpty() -> Text("There are no dates left to book for this campsite.")
                else -> LazyColumn(modifier = Modifier.testTag("availableDatesList")) {
                    items(availableDates) { date ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDateClicked(date) }
                                .testTag("date_$date")
                        ) {
                            Checkbox(checked = date in selectedDates, onCheckedChange = null)
                            Text(
                                text = DateUtils.parse(date)?.let { DateUtils.display(it) } ?: date,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (selectedDates.isNotEmpty()) {
            Text("${selectedDates.size} date(s) selected")
        }
        if (message != null) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onReserve,
            enabled = selectedDates.isNotEmpty(),
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

/*
    Displays a horizontally scrollable campsite choices for multiple
    campsite lottery and reports the selected campsite ID
 */

@Composable
fun LotteryCampsiteTabs(
    campsiteChoices: List<Pair<String, String>>,
    selectedCampsiteId: String?,
    onCampsiteSelected: (String) -> Unit
) {
    if (campsiteChoices.size <= 1) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        campsiteChoices.forEach { (id, name) ->
            FilterChip(
                selected = id == selectedCampsiteId,
                onClick = { onCampsiteSelected(id) },
                label = { Text(name) },
                modifier = Modifier.testTag("campsiteChoice_$id")
            )
        }
    }
}
