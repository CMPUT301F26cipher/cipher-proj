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
import com.example.cipherappproject.database.models.Lottery

/**
 * PLACEHOLDER, REPLACE THIS FILE
 */
@Composable
fun NotificationView(currentUserId: String) {
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
fun SentNotificationView(ownerId: String) {
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


// owner should also write a message to a lottery's waitlist
@Composable
fun SendNotificationDialog(lottery: Lottery, onDismiss: () -> Unit) {
// TODO: AlertDialog with title + message fields, Send -> controller.notifyWaitlist
}

