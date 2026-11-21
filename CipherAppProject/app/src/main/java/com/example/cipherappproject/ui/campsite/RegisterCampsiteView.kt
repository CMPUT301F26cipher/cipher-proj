package com.example.cipherappproject.ui.campsite
/*
--------------------------------------------------------------------------------------
Program Name: RegisterCampsiteView.kt
Date Written: 2026-11-20
Author: Madison Levy
Purpose: Screen where an owner registers a new campsite
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-20  Madison    1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
QR code, rules and policies have not been fully added. addCampsite requires them, so
they are saved empty until those features are built.
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.enums.CampsiteStatus
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Screen. Lets the owner fill in a new campsite's details and register it.
 *
 * @param ownerId owner's profile id
 * @param onSaveSuccess called after the campsite is saved
 * @param onCancel called when the owner presses Cancel
 */
@Composable
fun RegisterCampsiteView(
    ownerId: String,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit = {},
) {
    val campsiteRepository = RepositoryDatabase.campsiteRepository
    val formState = rememberCampsiteFormState()

    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("registerCampsiteView"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Register Campsite",
            style = MaterialTheme.typography.headlineMedium
        )

        CampsiteFormFields(state = formState, enabled = !isSaving)

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("registerCampsiteError")
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
                    val input = try {
                        formState.toInput()
                    } catch (e: IllegalArgumentException) {
                        null.also { errorMessage = e.message ?: "Please check your input." }
                    }

                    if (input != null) {
                        errorMessage = ""
                        isSaving = true

                        campsiteRepository.addCampsite(
                            ownerId = ownerId,
                            campsiteName = input.campsiteName,
                            mapLocation = input.mapLocation,
                            description = input.description,
                            amenities = input.amenities.toMutableList(),
                            maxGuests = input.maxGuests,
                            pricePerNight = input.pricePerNight,
                            imagePath = input.imagePath,
                            tags = input.tags.toMutableList(),
                            // required by addCampsite, but not yet built. saved empty (the Campsite defaults) until built
                            qrCodeImagePath = "",
                            status = CampsiteStatus.ACTIVE.name,
                            rules = mutableListOf(),
                            policies = mutableListOf()
                        ) { result ->
                            isSaving = false
                            if (result.isSuccess) {
                                onSaveSuccess()
                            } else {
                                errorMessage = "Failed to register campsite. Please try again."
                            }
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .testTag("registerCampsiteButton")
            ) {
                Text(if (isSaving) "Saving..." else "Register")
            }
        }
    }
}
