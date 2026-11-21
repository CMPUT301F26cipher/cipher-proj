package com.example.cipherappproject

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
fun QRScanView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("qrScanView"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Scan QR Code", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Scan a campsite's QR code to open its page.")
    }
}
