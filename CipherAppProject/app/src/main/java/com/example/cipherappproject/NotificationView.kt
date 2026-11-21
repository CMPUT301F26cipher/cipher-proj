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
fun NotificationView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("notificationView"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Notifications", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Notifications you've received will show here.")
    }
}

/** Owner's sent notifications. */
@Composable
fun SentNotificationView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("sentNotificationView"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Sent Notifications", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Notifications you've sent to entrants will show here.")
    }
}
