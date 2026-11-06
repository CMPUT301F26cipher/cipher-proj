package com.example.cipherappproject.ui.campsite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

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
}
