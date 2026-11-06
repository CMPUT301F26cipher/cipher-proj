package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Campsite

@Composable
fun CampsiteDetailView(
    campsite: Campsite,
    isOwner: Boolean,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            text = campsite.getCampsiteName(),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(campsite.getCampsiteDescription())

        Text(
            "Maximum Guests: ${campsite.getCampsiteMaxGuests()}"
        )

        Text(
            "Price per Night: $${campsite.getCampsitePricePerNight()}"
        )

        if (isOwner) {
            Button(onClick = onEditClick) {
                Text("Edit Campsite")
            }
        }
    }
}

