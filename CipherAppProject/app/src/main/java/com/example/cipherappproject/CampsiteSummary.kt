package com.example.cipherappproject

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cipherappproject.database.models.Campsite

/**
 * Displays summary of a given campsite. Is clickable, which allows navigation to the details on
 * the campsite
 *
 * @param navController For navigability
 * @param campsite gives details of the campsite to be summarized
 *
 */
/*
 * Built by Bailey
 * Nov 2 2026
 * Updates:
 *
 */
@Composable
fun CampsiteSummary(
    navController: NavController,
    campsite: Campsite,
){
    val campsite = remember { campsite }
    val campsiteID = campsite.campsiteId
    val campsiteName = campsite.campsiteName
    val campsiteImagePath = campsite.imagePath //TODO: implement getting picture
    val campsitePrice = campsite.pricePerNight
    val campsitePriceStr = "$$campsitePrice" //TODO: must reformat once campsite price type is finalized

    Box(
        modifier =
            Modifier
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                )
                .clickable(enabled = true){
                    navController.navigate(campsiteID)
                }
                .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column() {
            // TODO: add image here
            Text(
                text = campsiteName
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = campsitePriceStr
                )
            }
        }
    }
}