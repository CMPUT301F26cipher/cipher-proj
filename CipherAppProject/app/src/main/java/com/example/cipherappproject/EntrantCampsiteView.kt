package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository

/**
 * PLACEHOLDER, REPLACE THIS FILE
 */
@Composable
fun EntrantCampsiteView(campsiteId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("entrantCampsiteView"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Campsite", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Campsite details will show here. (id: $campsiteId)")
    }

    var tempCampsite = Campsite()

    CampsiteRepository().getCampsiteById(campsiteId) { result ->

        try {
            val campsite = result.getOrThrow()

        } catch (e: Exception){
            throw e
        }
    }


//    try {
//        CampsiteRepository().getCampsiteById(campsiteId) { tempCampsite = it }
//    } catch(e: NullPointerException) {
//        throw e
//    }
//    val campsite = tempCampsite

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
            "Maximum Occupancy: ${campsite.maxGuests}"
        )

        Text(
            "Location: ${campsite.mapLocation[0]}, ${campsite.mapLocation[1]}"
        )

        Text(
            "Tags: ${campsite.tags.joinToString(", ")}"
        )

//        if (campsite.image != null) {
//            Text("Image: ${campsite.image}")
//        }
    }
}
