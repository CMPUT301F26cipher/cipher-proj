package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.CampsiteInfoModel

@Composable
fun CampsiteDetailView(
    campsite: CampsiteInfoModel,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = campsite.campsiteName,
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Description: ${campsite.description}")

        Text(
            "Amenities: ${
                campsite.amenities.joinToString(", ")
            }"
        )

        Text(
            "Maximum Occupancy: ${campsite.maxOccupancy}"
        )

        Text(
            "Location: ${campsite.latitude}, ${campsite.longitude}"
        )

        Text(
            "Tags: ${campsite.tags.joinToString(", ")}"
        )

        if (campsite.image != null) {
            Text("Image: ${campsite.image}")
        }

        Button(onClick = onEditClick) {
            Text("Edit Campsite")
        }
    }
}