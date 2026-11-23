package com.example.cipherappproject.reservations

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedButton
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
import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.ratings.RatingRules
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
2026-11-21  Nethmini   1.02     Cancel only on active stays, rejected shown in cancelled tab
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
 * @param onRateClick (entrants) opens the rate/review screen for a past reservation
 */
@Composable
fun ReservationScreen(userID: String, isOwner: Boolean, onRateClick: (Reservation) -> Unit = {}) {
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

    var showCalendar by remember { mutableStateOf(false) }
    if (isOwner && showCalendar) {
        OwnerBookingCalendar(ownerId = userID, onBack = { showCalendar = false })
        return
    }

    //col so button will sit above the list
    Column( modifier = Modifier.testTag("reservationsMenu")) {
        Spacer(modifier = Modifier.height(8.dp))
        ReservationStatusSelector(
            controller.currentStatus,
            onStatusSelected = { newStatus -> controller.currentStatus = newStatus}
        )

        // owners can see how full their campsites are, day by day
        if (isOwner && controller.currentStatus == Status.ACTIVE) {
            OutlinedButton(
                onClick = { showCalendar = true },
                modifier = Modifier.padding(horizontal = 8.dp).testTag("viewBookedCalendar")
            ) { Text("View booked dates") }
        }

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

        DisplayListContent(reservations, controller.currentStatus.name.lowercase(), isOwner, controller, userID, onRateClick)
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
    controller: ReservationController = remember { ReservationController() },
    userID: String = "",
    onRateClick: (Reservation) -> Unit = {}
) {
    val ratingRepository = RepositoryDatabase.ratingRepository
    var toCancel by remember { mutableStateOf<Reservation?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<Reservation?>(null) }
    var toReject by remember { mutableStateOf<Reservation?>(null) }
    val context = LocalContext.current


    val reservations = when (status) {
        "active" -> if (isOwner) ReservationList.newestFirst(reservationsList.getActiveReservations())
                    else ReservationList.soonestFirst(reservationsList.getActiveReservations())
        "past" -> reservationsList.getPastReservations()
        "cancelled" -> reservationsList.getCancelledReservations()
        else -> emptyList()
    }

    // cancelled reservations are shown a page at a time; start over on page 1 when the tab changes
    var page by remember(status) { mutableStateOf(0) }
    val isCancelledTab = status == "cancelled"
    val pageCount = ReservationList.pageCount(reservations.size)
    val safePage = page.coerceIn(0, pageCount - 1)
    val shown = if (isCancelledTab) ReservationList.pageOf(reservations, safePage) else reservations

    // owner opened a reservation: show its details with the reject button
    selected?.let { target ->
        ReservationDetail(
            reservation = target,
            helpers = remember(target) { ReservationListHelpers(target, true) },
            onReject = { toReject = target },
            onBack = { selected = null }
        )
        toReject?.let { rejecting ->
            ConfirmDialog(
                title = "Reject reservation?",
                text = "Reject the reservation at ${campsiteRepoName(rejecting)}? The entrant will be notified and any refund follows the campsite's policy.",
                confirmTag = "confirmReject",
                onConfirm = {
                    toReject = null
                    selected = null
                    controller.reject(rejecting) { result ->
                        val text = result.fold(
                            onSuccess = { refund ->
                                if (refund > 0.0) "Reservation rejected. Refund: $" + String.format(java.util.Locale.US, "%.2f", refund)
                                else "Reservation rejected. No refund applies."
                            },
                            onFailure = { "Could not reject: ${it.message}" }
                        )
                        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                    }
                },
                onDismiss = { toReject = null }
            )
        }
        return
    }

    toCancel?.let { target ->
        // owners have no cancel action yet (nothing happens for them)
        if (!isOwner) {
            ConfirmDialog(
                title = "Cancel reservation?",
                text = "Do you want to cancel your reservation at ${campsiteRepoName(target)}? Any refund depends on the campsite's policy.",
                confirmTag = "confirmCancel",
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
        if (shown.isNotEmpty()) {
            // goes through list of reservations that
            items(shown) { reservation ->
                val helpers = remember(reservation) { ReservationListHelpers(reservation, isOwner) }

                ReservationRow(
                    helpers.getCampsiteImg(),
                    helpers.formattedCampsiteName(),
                    helpers.formattedUserName(),
                    if (isCancelledTab) helpers.formattedBookingPeriod() +
                            (if (reservation.status == ReservationStatus.REJECTED.name) " (rejected " else " (cancelled ") +
                            DateUtils.display(
                                java.time.Instant.ofEpochSecond(ReservationList.cancellationTime(reservation).seconds)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                            ) + ")"
                    else helpers.formattedBookingPeriod(),
                    // only upcoming stays can be cancelled; owners open the reservation to reject it
                    showButton = !isOwner && status == "active",
                    onClick = if (isOwner && status == "active") ({ selected = reservation }) else null,
                    // entrants remove an active reservation (asks for confirmation first)
                    onCancelClick = { toCancel = reservation },
                    buttonLabel = "Cancel",
                    // entrants can rate past stays (US 01.13.01, 01.13.02)
                    rateLabel = if (!isOwner && status == "past" && RatingRules.canRate(reservation, userID)) {
                        val existing = RatingRules.existingRating(
                            ratingRepository.getRatingsFromListByReservationId(reservation.reservationId),
                            reservation.reservationId, userID
                        )
                        when {
                            RatingRules.isLocked(existing) -> null
                            existing != null -> "Edit Rating"
                            else -> "Rate"
                        }
                    } else null,
                    onRateClick = { onRateClick(reservation) }
                )
                HorizontalDivider()
            }
        } else {
            item { Text("No reservations") }
        }
        if (isCancelledTab && pageCount > 1) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    TextButton(
                        onClick = { page = safePage - 1 },
                        enabled = safePage > 0,
                        modifier = Modifier.testTag("prevPage")
                    ) { Text("Previous") }
                    Text("Page ${safePage + 1} of $pageCount", modifier = Modifier.testTag("pageLabel"))
                    TextButton(
                        onClick = { page = safePage + 1 },
                        enabled = safePage < pageCount - 1,
                        modifier = Modifier.testTag("nextPage")
                    ) { Text("Next") }
                }
            }
        }
    }
}

private fun campsiteRepoName(reservation: Reservation): String =
    RepositoryDatabase.campsiteRepository.getCampsiteFromListById(reservation.campsiteId)?.campsiteName
        ?: "this campsite"

/**
 * Asks the user to confirm an action (cancelling or rejecting a reservation).
 */
@Composable
fun ConfirmDialog(title: String, text: String, confirmTag: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm, modifier = Modifier.testTag(confirmTag)) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.testTag("dismissDialog")) { Text("Back") } }
    )
}

/**
 * Details of one reservation for the owner, with the reject button.
 */
@Composable
fun ReservationDetail(
    reservation: Reservation,
    helpers: ReservationListHelpers,
    onReject: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("reservationDetail"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Reservation details", style = MaterialTheme.typography.headlineSmall)
        Text("Campsite: ${helpers.formattedCampsiteName()}")
        Text("Entrant: ${helpers.formattedUserName()}")
        Text("Dates: ${helpers.formattedBookingPeriod()}")
        Text("Price per night: $" + String.format(java.util.Locale.US, "%.2f", reservation.pricePerNight))
        Text("Total: $" + String.format(java.util.Locale.US, "%.2f", reservation.totalPrice))
        Text("Reservation ID: ${reservation.reservationId}")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onBack, modifier = Modifier.testTag("detailBack")) { Text("Back") }
            Button(onClick = onReject, modifier = Modifier.testTag("rejectReservation")) { Text("Reject reservation") }
        }
    }
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
 * @param rateLabel text of the rate button ("Rate"/"Edit Rating"), or null to hide it
 * @param onRateClick called when the rate button is pressed
 */
@Composable
fun ReservationRow(
    imagePath: String,
    campsiteName: String,
    userName: String,
    bookingPeriod: String,
    onCancelClick: () -> Unit = {},
    buttonLabel: String = "Cancel",
    showButton: Boolean = true,
    onClick: (() -> Unit)? = null,
    rateLabel: String? = null,
    onRateClick: () -> Unit = {}) {

    Row(
        modifier = Modifier.fillMaxWidth().let { if (onClick != null) it.clickable { onClick() } else it },
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

        if (rateLabel != null) {
            Button(onClick = onRateClick, modifier = Modifier.testTag("rateButton")) {
                Text(rateLabel)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (showButton) Button(onClick = { onCancelClick() }, modifier = Modifier.testTag("removeReservation")) {
            Text(buttonLabel)
        }
    }
}

@Preview
@Composable
fun ReservationPreview() {
    ReservationScreen(userID = "", isOwner = true)
}
