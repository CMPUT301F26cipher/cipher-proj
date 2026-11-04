package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.CampsiteInfoModel

@Composable
fun CampsiteFormView(
    campsite: CampsiteInfoModel? = null,
    onSave: (
        String, String, List<String>,
        Int, Double, Double, String?, List<String>
    ) -> Unit
) {
    var campsiteName by remember(campsite) {
        mutableStateOf(campsite?.campsiteName ?: "")
    }

    var description by remember(campsite) {
        mutableStateOf(campsite?.description ?: "")
    }

    var amenities by remember(campsite) {
        mutableStateOf(
            campsite?.amenities?.joinToString(", ") ?: ""
        )
    }

    var occupancy by remember(campsite) {
        mutableStateOf(
            campsite?.maxOccupancy?.toString() ?: ""
        )
    }

    var latitude by remember(campsite) {
        mutableStateOf(
            campsite?.latitude?.toString() ?: ""
        )
    }

    var longitude by remember(campsite) {
        mutableStateOf(
            campsite?.longitude?.toString() ?: ""
        )
    }

    var imageUri by remember(campsite) {
        mutableStateOf(campsite?.image ?: "")
    }

    var tags by remember(campsite) {
        mutableStateOf(
            campsite?.tags?.joinToString(", ") ?: ""
        )
    }

    val valid =
        campsiteName.isNotBlank() &&
                (occupancy.toIntOrNull() ?: 0) > 0 &&
                latitude.toDoubleOrNull()?.let {
                    it in -90.0..90.0
                } == true &&
                longitude.toDoubleOrNull()?.let {
                    it in -180.0..180.0
                } == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = if (campsite == null)
                "Register Campsite"
            else
                "Edit Campsite",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = campsiteName,
            onValueChange = { campsiteName = it },
            label = { Text("Campsite Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amenities,
            onValueChange = { amenities = it },
            label = { Text("Amenities (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = occupancy,
            onValueChange = { occupancy = it },
            label = { Text("Maximum Occupancy") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imageUri,
            onValueChange = { imageUri = it },
            label = { Text("Image URI (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSave(
                    campsiteName.trim(),
                    description.trim(),
                    amenities.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() },
                    occupancy.toInt(),
                    latitude.toDouble(),
                    longitude.toDouble(),
                    imageUri.trim().ifBlank { null },
                    tags.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                )
            },
            enabled = valid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (campsite == null)
                    "Register Campsite"
                else
                    "Save Changes"
            )
        }
    }
}