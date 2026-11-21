package com.example.cipherappproject.ui.campsite
/*
--------------------------------------------------------------------------------------
Program Name: CampsiteForm.kt
Date Written: 2026-11-20
Author: Madison Levy
Purpose: The campsite form shared by the register and edit screens. CampsiteFormState holds the typed text
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-20  Madison    1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
Pick an image from the device instead of typing a path (needs image storage)
Pick the map location on a map instead of typing latitude/longitude
 */
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Campsite

/**
 * Checked campsite values from the form, ready to save.
 *
 * @property mapLocation in the database format: [latitude, longitude, location name]
 */
data class CampsiteInput(
    val campsiteName: String,
    val mapLocation: List<String>,
    val description: String,
    val amenities: List<String>,
    val maxGuests: Int,
    val pricePerNight: Double,
    val imagePath: String,
    val tags: List<String>,
) {
    companion object {
        /**
         * Checks the text the owner typed and builds a CampsiteInput.
         * Amenities and tags are comma separated. Image path is optional.
         *
         * @throws IllegalArgumentException with a message to show the owner if any input is invalid
         */
        fun fromText(
            campsiteName: String,
            locationName: String,
            latitude: String,
            longitude: String,
            description: String,
            amenities: String,
            maxGuests: String,
            pricePerNight: String,
            imagePath: String,
            tags: String,
        ): CampsiteInput {
            if (campsiteName.isBlank()) {
                throw IllegalArgumentException("Campsite name cannot be empty.")
            }
            if (locationName.isBlank()) {
                throw IllegalArgumentException("Location name cannot be empty.")
            }
            val lat = latitude.trim().toDoubleOrNull()?.takeIf { it in -90.0..90.0 }
                ?: throw IllegalArgumentException("Latitude must be a number from -90 to 90.")
            val lng = longitude.trim().toDoubleOrNull()?.takeIf { it in -180.0..180.0 }
                ?: throw IllegalArgumentException("Longitude must be a number from -180 to 180.")
            val guests = maxGuests.trim().toIntOrNull()?.takeIf { it > 0 }
                ?: throw IllegalArgumentException("Maximum guests must be a whole number above 0.")
            val price = pricePerNight.trim().toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
                ?: throw IllegalArgumentException("Price per night must be a number, 0 or more.")

            return CampsiteInput(
                campsiteName = campsiteName.trim(),
                mapLocation = listOf(lat.toString(), lng.toString(), locationName.trim()),
                description = description.trim(),
                amenities = splitCommaList(amenities),
                maxGuests = guests,
                pricePerNight = price,
                imagePath = imagePath.trim(),
                tags = splitCommaList(tags),
            )
        }
        private fun splitCommaList(text: String): List<String> {
            return text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }
}

/**
 * The text in each box of the campsite form. Starts empty, or filled in from campsite
 * when editing.
 */
class CampsiteFormState(campsite: Campsite? = null) {
    var campsiteName by mutableStateOf(campsite?.campsiteName ?: "")
    var locationName by mutableStateOf(campsite?.mapLocation?.getOrNull(2) ?: "")
    var latitude by mutableStateOf(campsite?.mapLocation?.getOrNull(0) ?: "")
    var longitude by mutableStateOf(campsite?.mapLocation?.getOrNull(1) ?: "")
    var description by mutableStateOf(campsite?.description ?: "")
    var amenities by mutableStateOf(campsite?.amenities?.joinToString(", ") ?: "")
    var maxGuests by mutableStateOf(campsite?.maxGuests?.toString() ?: "")
    var pricePerNight by mutableStateOf(campsite?.pricePerNight?.toString() ?: "")
    var imagePath by mutableStateOf(campsite?.imagePath ?: "")
    var tags by mutableStateOf(campsite?.tags?.joinToString(", ") ?: "")

    fun toInput(): CampsiteInput {
        return CampsiteInput.fromText(
            campsiteName, locationName, latitude, longitude, description,
            amenities, maxGuests, pricePerNight, imagePath, tags
        )
    }
}

/** Keeps one CampsiteFormState for the screen. if different campsite is passed, starts over. */
@Composable
fun rememberCampsiteFormState(campsite: Campsite? = null): CampsiteFormState {
    return remember(campsite?.campsiteId) { CampsiteFormState(campsite) }
}

/**
 * The campsite form's input boxes.
 * @param state the text in each box
 * @param enabled false while saving, so the owner can't type
 */
@Composable
fun CampsiteFormFields(
    state: CampsiteFormState,
    enabled: Boolean = true,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormField("Campsite Name", state.campsiteName, { state.campsiteName = it }, enabled, "campsiteNameField")
        FormField("Location Name (e.g. Jasper, AB)", state.locationName, { state.locationName = it }, enabled, "campsiteLocationField")
        FormField("Latitude", state.latitude, { state.latitude = it }, enabled, "campsiteLatitudeField", KeyboardType.Decimal)
        FormField("Longitude", state.longitude, { state.longitude = it }, enabled, "campsiteLongitudeField", KeyboardType.Decimal)
        FormField("Description", state.description, { state.description = it }, enabled, "campsiteDescriptionField", singleLine = false)
        FormField("Amenities (comma separated)", state.amenities, { state.amenities = it }, enabled, "campsiteAmenitiesField")
        FormField("Maximum Guests", state.maxGuests, { state.maxGuests = it }, enabled, "campsiteMaxGuestsField", KeyboardType.Number)
        FormField("Price Per Night", state.pricePerNight, { state.pricePerNight = it }, enabled, "campsitePriceField", KeyboardType.Decimal)
        FormField("Image Path (optional)", state.imagePath, { state.imagePath = it }, enabled, "campsiteImageField")
        FormField("Tags (comma separated)", state.tags, { state.tags = it }, enabled, "campsiteTagsField")
    }
}

/**
 *  labeled text box in the campsite form.
 */
@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    testTag: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
