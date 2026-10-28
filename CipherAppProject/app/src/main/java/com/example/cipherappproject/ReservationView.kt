package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import java.util.Date
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.apps.card.v1.OnClick

// Google, Gemini Flash, "syntax for button in kotlin android studio", 2026-10-27

@Composable
fun ActiveReservationView(entrants: List<Entrant>?, onCancelClick: () -> Unit = {}) {
       LazyColumn(
           modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (entrants != null) {
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

    // Display list that we get from Reservation Model

}