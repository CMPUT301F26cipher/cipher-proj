package com.example.cipherappproject.ui.reservations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button

/**
 * Display
 */
@Composable
fun RegisterCampsiteScreen() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val nameState = rememberTextFieldState()
        val locationState = rememberTextFieldState()
        val lotteryWinnerNumState = rememberTextFieldState()

        LabeledTextField("Name", nameState,"Campsite Name")
        LabeledTextField("Add Location",locationState, "Location Name")

        Text("Add Image")

        //TODO: add img box
        LabeledTextField("Lottery (Optional)", lotteryWinnerNumState, "Set # of lottery winners")

        Text("Booking Date")
        //TODO: add booking dates

        //TODO: create button
        Button(onClick = () -> Unit) { }
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Add Location")
        TextField(
            state = rememberTextFieldState(),
            placeholder = { Text("Location Name") }
        )
    }
}

@Preview
@Composable
fun RegisterCampsitePreview() {
    RegisterCampsiteScreen()
}
