package com.example.cipherappproject.ui.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp

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
        LabeledTextField("Lottery (Optional)", lotteryWinnerNumState, "Set # of lottery winners")

        Text("Booking Date")
        //TODO: add booking dates

        //TODO: create button
        //Button(onClick = () -> Unit) { }
    }


}

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

@Preview
@Composable
fun RegisterCampsitePreview() {
    RegisterCampsiteScreen()
}
