package com.example.cipherappproject

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.ReservationModel.Status

//
// Google, Gemini Flash, "syntax for button in kotlin android studio", 2026-10-27


/**
 * Display list of active reservation according to current active user type (entrants, owners,
 * admin)
 *
 * @param userType
 * @param
 * @return
 */
@Composable
fun DisplayListContent(reservations: List<Entrant>, onCancelClick: () -> Unit = {}) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (reservations != null) {
            item {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onCancelClick() }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        } else {
            item {
                Text("No active reservations")
            }
        }
    }
}

/**
 * Displays the active reservation screen
 *
 * @param
 */
@Composable
fun ReservationScreen() {
    //rememberSaveable ensure if phone is rotated does not reset status
    var currentStatus by rememberSaveable { mutableStateOf(Status.ACTIVE) }

    //col so button will sit above the list
    Column() {
        Spacer(modifier = Modifier.height(8.dp))
        ReservationStatusSelector(currentStatus, onStatusSelected = { newStatus -> currentStatus = newStatus})

        DisplayListContent(emptyList())
    }


}

/**
 * Displays the row of status buttons,
 *
 * @param selectedStatus
 *
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
            Button( onClick = { onStatusSelected(status) }) {
                Text(status.toString())
            }
        }
    }
}

@Preview
@Composable
fun ReservationPreview() {
    ReservationScreen()
}
