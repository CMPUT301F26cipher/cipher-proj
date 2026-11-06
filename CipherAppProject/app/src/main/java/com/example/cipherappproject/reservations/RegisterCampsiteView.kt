package com.example.cipherappproject.reservations

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/*
--------------------------------------------------------------------------------------
Program Name: Reservation.kt
Date Written: November 5, 2026
Author: Shira Li
Purpose: Reservation display screen for both past and active reservations, and for
both Entrants and Owners
US: 02.01.01
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

/**
 * Display
 */
@Composable
fun RegisterCampsiteScreen() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        val nameState = rememberTextFieldState()
        val locationState = rememberTextFieldState()
        val lotteryWinnerNumState = rememberTextFieldState()
        var selectedDateRange by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }

        LabeledTextField("Name", nameState,"Campsite Name")
        LabeledTextField("Add Location",locationState, "Location Name")

        Text("Add Image")
        //TODO: add img box
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//
//        ) {
//            Button(
//                onClick = {}
//            ) {
//        }

        //}
        LabeledTextField("Create Lottery (Optional)", lotteryWinnerNumState, "")

        Text("Booking Date")
        //TODO: add booking dates
//        if (selectedDateRange.first != null && selectedDateRange.second != null) {
//            val startDate = LocalDate()
//            val end
//        }
//        if (showRangeModal) {
//            DateRangePickerModal(
//                onDateRangeSelected = {
//                    selectedDateRange = it
//                    showRangeModal = false
//                },
//                onDismiss = {}
//            )
//        }

        //TODO: create button
        //createCampesiteButton { onCreateButton: () -> Unit = {} }
    }


}
/**
 * displays box to enter date for booking start period and booking end as a range
 * following https://developer.android.com/develop/ui/compose/components/datepickers
 */
//@Composable
//fun DateRangePickerModal(
//    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
//    onDismiss: () -> Unit
//
//) {
//    val dateRangePickerState = rememberDateRangePickerState()
//
//    DatePickerDialog(
//        onDismissRequest = onDismiss,
//        confirmButton = {
//            TextButton(
//                onClick = {
//                    onDateRangeSelected(
//                        Pair(
//                            dateRangePickerState.selectedStartDateMillis,
//                            dateRangePickerState.selectedEndDateMillis
//                        )
//                    )
//                    onDismiss()
//                }
//            ) {
//                Text("OK")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    ) {
//        DateRangePicker(
//            state = dateRangePickerState,
//            title = {Text("Select date range")},
//            showModeToggle = false,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(250.dp)
//        )
//    }
//}

/**
 * Text input row
 *
 * @param label
 * @param state
 * @param placeholder grey placeholder text that goes away upon clicking text box
 */
@Composable
fun LabeledTextField(label: String, state: TextFieldState, placeholder: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        TextField(
            state = state,
            placeholder = { Text(placeholder) }
        )
    }
}

/**
 * Create Campsite button using https://developer.android.com/develop/ui/compose/components/button
 */
//@Composable
//fun createCampesiteButton( onClick: () -> Unit) {
//    Button(onClick = { onClick() }) {
//        Text("Create Campsite")
//    }
//}
@Preview
@Composable
fun DateRangePreview() {

}

@Preview
@Composable
fun RegisterCampsitePreview() {
    RegisterCampsiteScreen()
}
