package com.example.cipherappproject.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.reservations.ReservationList.Status
import com.example.cipherappproject.database.models.Reservation
import kotlin.collections.emptyList

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

// acts like a watchdog, only becomes active when time of closest end booking date is notif
// TODO: Write TEST cases
// TODO: fix status
// TODO: fix date conversion for checking booking
// TODO: test with dummy values

/**
 * Displays the reservation screen where users can click between 3 different reservations statuses
 * (active, past, cancelled) and see the list of their most recent reservations
 *
 * @param userID string representing the user, used to load reservations
 */
@Composable
fun ReservationScreen(userID: String) {
    val reservations = remember { ReservationList(userID) }
    //rememberSaveable ensure if phone is rotated does not reset status
    var currentStatus by rememberSaveable { mutableStateOf(Status.ACTIVE) }

    //col so button will sit above the list
    Column( modifier = Modifier.testTag("reservationsMenu")) {
        Spacer(modifier = Modifier.height(8.dp))
        ReservationStatusSelector(
            currentStatus,
            onStatusSelected = { newStatus -> currentStatus = newStatus}
        )

        DisplayListContent(reservations, currentStatus)
    }
}

/**
 * Displays the row of status buttons, with the current acitve tab in a different colour
 *
 * @param selectedStatus status of current active tab
 * @param onStatusSelected
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
                colors = if (status == selectedStatus)

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
fun DisplayListContent(reservationsList: ReservationList, status: String) {
    val reservations = when (status) {
        "active" -> reservationsList.getActiveReservations()
        "past" -> reservationsList.getPastReservations()
        else -> emptyList()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (reservations.isNotEmpty()) {
            // goes through list of reservations that
            items(reservations) { reservation ->
                ReservationRow(
                     reservations.getCampsiteImg(reservation),
                    reservations.formattedCampsiteName(reservation),
                    reservations.formattedUserName(reservation),
                    reservations.formattedBookingPeriod(reservation)
                    )
                HorizontalDivider()
            }
        } else {
            item { Text("No reservations") }
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
 */
@Composable
fun ReservationRow(
    imagePath: String,
    campsiteName: String,
    userName: String,
    bookingPeriod: String,
    onCancelClick: () -> Unit = {}) {

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

        Button( onClick = { onCancelClick() }) {
            Text("Cancel")
        }
    }
}

@Preview
@Composable
fun ReservationPreview() {
    ReservationScreen()
}
