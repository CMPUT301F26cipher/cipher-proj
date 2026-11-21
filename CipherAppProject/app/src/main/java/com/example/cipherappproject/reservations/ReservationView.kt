package com.example.cipherappproject.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.example.cipherappproject.database.provider.RepositoryDatabase
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.cipherappproject.database.models.Reservation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.reservations.ReservationList.Status
import kotlinx.coroutines.delay
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

/*
--------------------------------------------------------------------------------------
Program Name: ReservationView.kt
Date Written: November 5, 2026
Author: Shira Li
Purpose: Reservation display screen for both past and active reservations, and for
both Entrants and Owners
US: 02.07.01, 02.07.02 (owners), 01.06.01, 01.06.02 (entrants)
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Shira      1.01     Modifying to fit DB variables
2026-
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

// Google, Gemini Flash, "syntax for button in kotlin android studio", 2026-10-27


// TODO: Write TEST cases
// TODO: test with dummy values

/**
 * Displays the reservation screen where users can click between 3 different reservations statuses
 * (active, past, cancelled) and see the list of their most recent reservations
 *
 * @param userID string representing the user, used to load reservations
 */
@Composable
fun ReservationScreen(userID: String, isOwner: Boolean) {
    val reservations = remember { ReservationList(userID, isOwner) }
    val controller = remember { ReservationController() }

    // START OF AI GEN SECTION
    // Anthropic, Claude Opus 5.5, "if i want to connect a timer from a view to model how would a
    // template look like", 2026-11-06

    // acts like a watchdog, waits until the day of after the closest booking end date then
    // refreshes the lists so that the expired reservation moves from active to past
    val expiry = reservations.getNextExpiry()
    LaunchedEffect(expiry) {
        if (expiry != null) {
            val refreshListAt = expiry.plusDays(1).atStartOfDay(ZoneId.systemDefault())
            delay(maxOf(0L, refreshListAt.toInstant().toEpochMilli() - System.currentTimeMillis()).milliseconds)
            reservations.refreshTime()
        }
    }
    // END oF AI GEN SECTION

    //col so button will sit above the list
    Column( modifier = Modifier.testTag("reservationsMenu")) {
        Spacer(modifier = Modifier.height(8.dp))
        ReservationStatusSelector(
            controller.currentStatus,
            onStatusSelected = { newStatus -> controller.currentStatus = newStatus}
        )

        // entrants can download their active (confirmed) reservations as a CSV
        if (!isOwner && controller.currentStatus == Status.ACTIVE) {
            val campsiteRepo = RepositoryDatabase.campsiteRepository
            ReservationCsv.DownloadCsvButton(
                fileName = "active_reservations.csv",
                buildCsv = {
                    ReservationCsv.build(reservations.getActiveReservations()) {
                        campsiteRepo.getCampsiteFromListById(it)?.campsiteName ?: "Unknown campsite"
                    }
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        DisplayListContent(reservations, controller.currentStatus.name.lowercase(), isOwner, controller)
    }
}

/**
 * Displays the row of status buttons, with the current acitve tab in a different colour
 *
 * @param selectedStatus status of current active tab
 * @param onStatusSelected holds the new status of button clicked
 */
@Composable
fun ReservationStatusSelector(selectedStatus: Status, onStatusSelected: (Status) -> Unit) {
    Row (
        //ensure row is wider than contents so taht it can be centered
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        for (status in Status.entries) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onStatusSelected(status) },
                colors = if (status == selectedStatus) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
                }

                ) {
                Text(status.toString())
            }
        }
    }
}


/**
 * Display list of reservation by according to current active user type (entrants, owners, admin)
 *
 * @param reservationsList reservation list hold the current user's reservation
 * @param status selected button status, which determines which list is shown
 */
@Composable
fun DisplayListContent(
    reservationsList: ReservationList,
    status: String,
    isOwner: Boolean,
    controller: ReservationController = remember { ReservationController() }
) {
    var toCancel by remember { mutableStateOf<Reservation?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    val reservations = when (status) {
        "active" -> reservationsList.getActiveReservations()
        "past" -> reservationsList.getPastReservations()
        else -> emptyList()
    }

    toCancel?.let { target ->
        // owners have no cancel action yet (nothing happens for them)
        if (!isOwner) {
            ConfirmCancelDialog(
                campsiteName = campsiteRepoName(target),
                onConfirm = {
                    toCancel = null
                    controller.cancel(target) { result ->
                        val text = result.fold(
                            onSuccess = { refund ->
                                if (refund > 0.0) "Reservation cancelled. Refund: $" + String.format(java.util.Locale.US, "%.2f", refund)
                                else "Reservation cancelled. No refund applies."
                            },
                            onFailure = { "Could not cancel: ${it.message}" }
                        )
                        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                    }
                },
                onDismiss = { toCancel = null }
            )
        } else toCancel = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (reservations.isNotEmpty()) {
            // goes through list of reservations that
            items(reservations) { reservation ->
                val helpers = remember(reservation) { ReservationListHelpers(reservation, isOwner) }

                ReservationRow(
                    helpers.getCampsiteImg(),
                    helpers.formattedCampsiteName(),
                    helpers.formattedUserName(),
                    helpers.formattedBookingPeriod(),
                    // entrants remove an active reservation (asks for confirmation first)
                    onCancelClick = { toCancel = reservation },
                    buttonLabel = if (!isOwner && status == "active") "Remove" else "Cancel"
                )
                HorizontalDivider()
            }
        } else {
            item { Text("No reservations") }
        }
    }
}

private fun campsiteRepoName(reservation: Reservation): String =
    RepositoryDatabase.campsiteRepository.getCampsiteFromListById(reservation.campsiteId)?.campsiteName
        ?: "this campsite"

/**
 * Asks the entrant to confirm removing a reservation.
 */
@Composable
fun ConfirmCancelDialog(campsiteName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel reservation?") },
        text = { Text("Do you want to cancel your reservation at $campsiteName? Any refund depends on the campsite's policy.") },
        confirmButton = {
            TextButton(onClick = onConfirm, modifier = Modifier.testTag("confirmCancel")) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Keep reservation") } }
    )
}

/**
 * Display SINGLE reservation row, including the campsite name, campsite image, name of user, and
 * booking period
 *
 * @param imagePath path to campsite image
 * @param campsiteName name of campsite
 * @param userName (entrants) name of campsite owner. (owner) name of entrant who made reservation
 * @param bookingPeriod formatted dates i.e Nov 1 - Nov 3
 * @param onCancelClick
 */
@Composable
fun ReservationRow(
    imagePath: String,
    campsiteName: String,
    userName: String,
    bookingPeriod: String,
    onCancelClick: () -> Unit = {},
    buttonLabel: String = "Cancel") {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //TODO: Display image of campsite

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Display {campsite name},{username | booking period}
            Text(campsiteName)
            Row {
                Text(userName)
                Spacer(modifier = Modifier.width(8.dp))
                Text(bookingPeriod)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { onCancelClick() }, modifier = Modifier.testTag("removeReservation")) {
            Text(buttonLabel)
        }
    }
}

@Preview
@Composable
fun ReservationPreview() {
    ReservationScreen(userID = "", isOwner = true)
}
