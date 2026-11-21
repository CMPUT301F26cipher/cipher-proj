package com.example.cipherappproject.ui.campsite
/*
--------------------------------------------------------------------------------------
Program Name: EditCampsiteView
Date Written: 2026-11-05
Author: Hye-Sung
Purpose: To update details about a campsite as an owner
US: 02.01.03
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Modifying to fit DB variables
2026-11-20  Madison    1.02     Loads owner's campsite by id, shared form with location, shared repository, price fix
--------------------------------------------------------------------------------------
Future Enhancements
NA
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
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.lottery.CenteredMessage
import com.example.cipherappproject.ui.lottery.LoadingIndicator

/**
 * Lets the owner change the details of one of their campsites.
 *
 * @param campsiteId id of the campsite to edit
 * @param ownerId owner's profile id
 * @param onSaveSuccess called after the changes are saved
 * @param onCancel called when the owner presses Cancel
 */
@Composable
fun EditCampsiteView(
    campsiteId: String,
    ownerId: String,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit = {},
) {
    val campsiteRepository = RepositoryDatabase.campsiteRepository

    if (!campsiteRepository.isCampsitesLoaded.value) {
        LoadingIndicator()
        return
    }

    val campsite = campsiteRepository.getCampsiteFromListByOwnerId(ownerId)
        .find { it.campsiteId == campsiteId }
    if (campsite == null) {
        CenteredMessage("This campsite could not be found.")
        return
    }

    // Starts filled in with the campsite's current details
    val formState = rememberCampsiteFormState(campsite)

    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("editCampsiteView"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Edit Campsite",
            style = MaterialTheme.typography.headlineMedium
        )

        CampsiteFormFields(state = formState, enabled = !isSaving)

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("editCampsiteError")
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

                        val updatedCampsite = campsite.copy(
                            campsiteName = input.campsiteName,
                            mapLocation = input.mapLocation,
                            description = input.description,
                            amenities = input.amenities.toMutableList(),
                            maxGuests = input.maxGuests,
                            pricePerNight = input.pricePerNight,
                            imagePath = input.imagePath,
                            tags = input.tags.toMutableList()
                        )

                        campsiteRepository.updateCampsite(updatedCampsite) { saveResult ->
                            isSaving = false
                            if (saveResult.isSuccess) {
                                onSaveSuccess()
                            } else {
                                errorMessage = "Failed to save campsite changes."
                            }
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .testTag("saveCampsiteButton")
            ) {
                Text(if (isSaving) "Saving..." else "Confirm")
            }
        }
    }
}