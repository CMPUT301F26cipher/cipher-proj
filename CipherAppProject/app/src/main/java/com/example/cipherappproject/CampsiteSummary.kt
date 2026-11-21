package com.example.cipherappproject
/*
--------------------------------------------------------------------------------------
Program Name: CampsiteSummary.kt
Date Written: Nov 2 2026
Author: Bailey Parrish
Purpose: Campsite summary is clickable and shows up in the search results, showing a campsite's name, price,
location and max guests. Clicking it opens that campsite's details.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-02  Bailey      1.01     Built
2026-11-18  Madison     1.02     made it run
--------------------------------------------------------------------------------------
Future Enhancements
Show campsite image (campsite.imagePath)
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Campsite
import java.util.Locale

/**
 * Displays a summary of a campsite. Clicking it opens the campsite's details.
 *
 * @param onClick called when the card is tapped
 * @param campsite the campsite on the card
 */
@Composable
fun CampsiteSummary(
    onClick: () -> Unit,
    campsite: Campsite,
){
    val locationName = campsite.mapLocation.getOrNull(2).orEmpty()
    val priceText = String.format(Locale.CANADA, "$%.2f / night", campsite.pricePerNight)

    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable { onClick() }
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .testTag("campsiteSummary")
    ) {
        Column {
            // TODO: add image here (campsite.imagePath)
            Text(
                text = campsite.campsiteName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = priceText)
                Text(text = locationName)
                Text(text = "Max Guests: ${campsite.maxGuests}")
            }
        }
    }
}