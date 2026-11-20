package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
    // True while a reserve/decline write is in flight, so repeat taps can't send it twice
    var isWorking by remember { mutableStateOf(false) }

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
        onRangeSelected = { start, end ->
            message = if (start == null) {
                controller.clearSelectedDates()
                null
            } else {
                controller.selectRange(start, end)
            }
            selectedDates = controller.getSelectedDates()
        },
        message = message,
        isWorking = isWorking,
        onReserve = {
            if (isWorking) return@LotteryWinView
            isWorking = true
            message = null
            controller.reserve(lottery, entry) { reserveResult ->
                // On success the entry becomes ACCEPTED and LotteryResultScreen switches screens
                reserveResult.onFailure {
                    isWorking = false
                    message = it.message ?: "Could not reserve. Check your connection and try again."
                }
            }
        },
        onDecline = {
            if (isWorking) return@LotteryWinView
            isWorking = true
            message = null
            controller.declineOffer(entry) { declineResult ->
                // On success the entry becomes DECLINED and LotteryResultScreen switches screens
                declineResult.onFailure {
                    isWorking = false
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
 * @param selectedDates "yyyy-MM-dd" dates of the chosen stay
 * @param onRangeSelected called with the picked first and last day ("yyyy-MM-dd"); the last
 *   day is null while only the first has been tapped, and both are null when cleared
 */
@Composable
fun LotteryWinView(
    campsiteChoices: List<Pair<String, String>>,
    selectedCampsiteId: String?,
    onCampsiteSelected: (String) -> Unit,
    availableDates: List<String>?,
    selectedDates: Set<String>,
    onRangeSelected: (String?, String?) -> Unit,
    message: String?,
    onReserve: () -> Unit,
    onDecline: () -> Unit,
    isWorking: Boolean = false
) {
    var showDeclineDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "You won the lottery!", style = MaterialTheme.typography.headlineMedium)
        Text("Pick the first and last night of your stay, or decline if no dates work for you.")

        // Only shown when the lottery covers more than one campsite (US 01.04.02)
        if (campsiteChoices.size > 1) {
            LotteryCampsiteTabs(
                campsiteChoices = campsiteChoices,
                selectedCampsiteId = selectedCampsiteId,
                onCampsiteSelected = onCampsiteSelected
            )
        }

        // Takes the leftover space so the buttons stay at the bottom
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                availableDates == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                availableDates.isEmpty() -> Text("There are no dates left to book for this campsite.")
                // New calendar per campsite / availability change, so old picks don't carry over
                else -> key(selectedCampsiteId, availableDates) {
                    AvailabilityCalendar(
                        availableDates = availableDates,
                        onRangeSelected = onRangeSelected
                    )
                }
            }
        }

        StaySummary(selectedDates)
        if (message != null) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onReserve,
            enabled = selectedDates.isNotEmpty() && !isWorking,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reserveButton")
        ) {
            Text("Reserve")
        }
        OutlinedButton(
            onClick = { showDeclineDialog = true },
            enabled = !isWorking,
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

/**
 * Calendar of the campsite's bookable dates (US 01.04.01). Dates that can't be booked are
 * greyed out and can't be tapped; the entrant taps a first and a last night.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvailabilityCalendar(
    availableDates: List<String>,
    onRangeSelected: (String?, String?) -> Unit
) {
    val available = remember(availableDates) {
        availableDates.mapNotNull { DateUtils.parse(it) }.toSet()
    }
    val firstDate = available.min()
    val lastDate = available.max()

    val state = rememberDateRangePickerState(
        initialDisplayedMonthMillis = DateUtils.toUtcMillis(firstDate),
        yearRange = firstDate.year..lastDate.year,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                DateUtils.fromUtcMillis(utcTimeMillis) in available

            override fun isSelectableYear(year: Int): Boolean =
                year in firstDate.year..lastDate.year
        }
    )

    // Report each change of the picked range to the controller
    LaunchedEffect(state.selectedStartDateMillis, state.selectedEndDateMillis) {
        val start = state.selectedStartDateMillis?.let { DateUtils.fromUtcMillis(it).toString() }
        val end = state.selectedEndDateMillis?.let { DateUtils.fromUtcMillis(it).toString() }
        onRangeSelected(start, end)
    }

    DateRangePicker(
        state = state,
        title = null,
        headline = null,
        showModeToggle = false,
        modifier = Modifier
            .fillMaxSize()
            .testTag("availableDatesCalendar")
    )
}

/** "Sat, May 1, 2027 to Mon, May 3, 2027 (3 nights)", or nothing if no dates are picked. */
@Composable
private fun StaySummary(selectedDates: Set<String>) {
    if (selectedDates.isEmpty()) return
    val sorted = selectedDates.sorted()
    val first = DateUtils.parse(sorted.first())?.let { DateUtils.display(it) } ?: sorted.first()
    val last = DateUtils.parse(sorted.last())?.let { DateUtils.display(it) } ?: sorted.last()
    val nights = if (sorted.size == 1) "1 night" else "${sorted.size} nights"
    Text(
        text = if (sorted.size == 1) "$first ($nights)" else "$first to $last ($nights)",
        modifier = Modifier.testTag("staySummary")
    )
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
