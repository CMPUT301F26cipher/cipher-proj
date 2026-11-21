package com.example.cipherappproject.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/*
--------------------------------------------------------------------------------------
Program Name: RegisterCampsiteView.kt
Date Written: November 5, 2026
Author: Shira Li
Purpose: Form where an owner enters a campsite's details and confirms the registration
US: 02.01.01
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Shira      1.01     Modifying to fit DB variables
2026-11-20  Hye Sung   1.02     Working form: validation, Confirm Registration saves the campsite
--------------------------------------------------------------------------------------
Future Enhancements
Image upload and QR code generation
2026-11-20  Hye Sung   1.03     Booking period (US 02.01.02) saved as the campsite's availability
 */

/**
 * Register-a-campsite form. Confirm Registration saves the campsite for [ownerId] and
 * calls [onRegistered]; problems are shown in the form.
 */
@Composable
fun RegisterCampsiteScreen(
    ownerId: String,
    onRegistered: () -> Unit = {},
    controller: CampsiteRegistrationController = remember { CampsiteRegistrationController() }
) {
    var input by remember { mutableStateOf(CampsiteFormInput()) }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("registerCampsiteView"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Register a Campsite", style = MaterialTheme.typography.headlineMedium)

        FormField("Campsite name", input.name, "campsiteName") { input = input.copy(name = it) }
        FormField("Location (place name)", input.locationName, "campsiteLocation") {
            input = input.copy(locationName = it)
        }
        FormField("Latitude", input.latitude, "campsiteLatitude") { input = input.copy(latitude = it) }
        FormField("Longitude", input.longitude, "campsiteLongitude") { input = input.copy(longitude = it) }
        FormField("Description", input.description, "campsiteDescription") {
            input = input.copy(description = it)
        }
        FormField("Maximum guests", input.maxGuests, "campsiteMaxGuests") {
            input = input.copy(maxGuests = it)
        }
        FormField("Price per night", input.pricePerNight, "campsitePrice") {
            input = input.copy(pricePerNight = it)
        }
        FormField("Amenities (comma separated, optional)", input.amenities, "campsiteAmenities") {
            input = input.copy(amenities = it)
        }
        FormField("Tags (comma separated, optional)", input.tags, "campsiteTags") {
            input = input.copy(tags = it)
        }
        FormField("Rules (comma separated, optional)", input.rules, "campsiteRules") {
            input = input.copy(rules = it)
        }

        Text("Booking period: the dates entrants can book (both days included)")
        FormField("Booking period start (yyyy-MM-dd)", input.bookingStart, "bookingStart") {
            input = input.copy(bookingStart = it)
        }
        FormField("Booking period end (yyyy-MM-dd)", input.bookingEnd, "bookingEnd") {
            input = input.copy(bookingEnd = it)
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("registerError"))
        }

        Button(
            enabled = !saving,
            modifier = Modifier.testTag("confirmRegistration"),
            onClick = {
                saving = true
                error = null
                controller.register(ownerId, input) { problem ->
                    saving = false
                    if (problem == null) onRegistered() else error = problem
                }
            }
        ) {
            Text("Confirm Registration")
        }
    }
}

@Composable
private fun FormField(label: String, value: String, tag: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
    )
}
