package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository

/*
--------------------------------------------------------------------------------------
Program Name:
Date Written:
Author:
Purpose:
US: 02.01.03
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Modifying to fit DB variables
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

@Composable
fun EditCampsiteView(
    campsite: Campsite,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit = {},
    repository: CampsiteRepository = remember {
        CampsiteRepository()
    }
) {

    // Prepopulate fields with existing campsite data
    var campsiteName by remember(campsite.campsiteId) {
        mutableStateOf(campsite.campsiteName)
    }

    var description by remember(campsite.campsiteId) {
        mutableStateOf(campsite.description)
    }

    var amenities by remember(campsite.campsiteId) {
        mutableStateOf(campsite.amenities.joinToString(", "))
    }

    var maxGuests by remember(campsite.campsiteId) {
        mutableStateOf(campsite.maxGuests.toString())
    }

    var pricePerNight by remember(campsite.campsiteId) {
        mutableStateOf(campsite.pricePerNight.toString())
    }

    var imagePath by remember(campsite.campsiteId) {
        mutableStateOf(campsite.imagePath)
    }

    var tags by remember(campsite.campsiteId) {
        mutableStateOf(campsite.tags.joinToString(", "))
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Edit Campsite",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = campsiteName,
            onValueChange = { campsiteName = it },
            label = { Text("Campsite Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = amenities,
            onValueChange = { amenities = it },
            label = { Text("Amenities") },
            supportingText = {
                Text("Separate amenities with commas")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = maxGuests,
            onValueChange = { maxGuests = it },
            label = { Text("Maximum Guests") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pricePerNight,
            onValueChange = { pricePerNight = it },
            label = { Text("Price Per Night") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imagePath,
            onValueChange = { imagePath = it },
            label = { Text("Image Path") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags") },
            supportingText = {
                Text("Separate tags with commas")
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onCancel,
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {

                    val guests = maxGuests.toIntOrNull()
                    val price = pricePerNight.toFloatOrNull()?.toDouble()

                    when {
                        campsiteName.isBlank() -> {
                            errorMessage =
                                "Campsite name cannot be empty."
                        }

                        guests == null || guests <= 0 -> {
                            errorMessage =
                                "Maximum guests must be greater than 0."
                        }

                        price == null ||
                                !price.isFinite() ||
                                price < 0f -> {
                            errorMessage =
                                "Enter a valid price."
                        }

                        else -> {

                            errorMessage = ""
                            isSaving = true

                            val updatedCampsite = campsite.copy(
                                campsiteName = campsiteName.trim(),
                                description = description.trim(),

                                amenities = amenities
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() } as MutableList<String>,

                                maxGuests = guests,
                                pricePerNight = price,
                                imagePath = imagePath.trim(),

                                tags = tags
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() } as MutableList<String>
                            )

                            repository.updateCampsite(
                                updatedCampsite
                            ) { success ->

                                isSaving = false

                                if (success) {
                                    onSaveSuccess()
                                } else {
                                    errorMessage =
                                        "Failed to save campsite changes."
                                }
                            }
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (isSaving) "Saving..." else "Confirm"
                )
            }
        }
    }
}