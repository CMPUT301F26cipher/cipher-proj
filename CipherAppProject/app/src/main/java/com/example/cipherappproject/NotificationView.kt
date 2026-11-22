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
import com.example.cipherappproject.database.enums.NotificationEventType
import com.example.cipherappproject.database.models.Lottery
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.Timestamp
import java.text.DateFormat

/**
 * Format a firestore timestamp for display
 */
private fun formatTime(time: Timestamp): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(time.toDate())

/**
 * Entrant's notification inbox, which is opened from the bell icon
 * Unread notifications are bold, and tapping one marks it red.
 *
 * @param cuurentUserId the signed-in owner's profile id
 */
@Composable
fun NotificationView(currentUserId: String, onViewReservations: () -> Unit = {}) {
    val controller = remember { NotificationController() }
    val notifications = controller.getReceived(currentUserId)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("notificationView"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text(text = "Notifications", style = MaterialTheme.typography.headlineMedium) }
        if (notifications.isEmpty()) {
            item { Text("No notifications yet.") }
        }
        items(notifications, key = { it.notificationId }) { n ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        controller.markAsRead(n)
                        // a reservation notification opens the entrant's reservations
                        if (n.eventType == NotificationEventType.RESERVATION.name) onViewReservations()
                    }
                    .testTag("notification_${n.notificationId}")
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        n.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (n.isRead) FontWeight.Normal else FontWeight.Bold
                    )
                    Text(n.message, style = MaterialTheme.typography.bodyMedium)
                    Text(formatTime(n.createdAt), style = MaterialTheme.typography.labelSmall)
                    if (n.eventType == NotificationEventType.RESERVATION.name) {
                        TextButton(
                            onClick = {
                                controller.markAsRead(n)
                                onViewReservations()
                            },
                            modifier = Modifier.testTag("viewReservations_${n.notificationId}")
                        ) { Text("View reservations") }
                    }
                }
            }
        }
    }
}
    /**
     * Owner's list of messages they have sent, with how many entrants got each one
     *
     * @param ownerId, the signed-in owner's profile id
     * */
    @Composable
    fun SentNotificationView(ownerId: String) {
        val controller = remember { NotificationController() }
        val sent = controller.getSentNotifications(ownerId)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("sentNotificationView"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Sent Notifications",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            if (sent.isEmpty()) {
                item { Text("You haven't sent any notifications.") }
            }
            items(sent) { group ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            group.title, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(group.message)
                        Text(
                            "Sent to ${group.recipientCount} entrant(s) · ${formatTime(group.sentAt)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

            }
        }
    }
        /**
         * Pop up where an owner writes a title and message to send to a lottery's waitlist
         *
         * @param lottery, the lottery whose entrants receive the messag
         * @param senderId, the owner sending it
         * @param onDismiss, close the dialog
         */
        @Composable
        fun SendNotificationDialog(lottery: Lottery, senderId: String, onDismiss: () -> Unit) {
            val controller = remember { NotificationController() }
            val context = LocalContext.current
            var title by remember { mutableStateOf("") }
            var message by remember { mutableStateOf("") }
            var error by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Notify waitlist") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = title, onValueChange = { title = it },
                            label = { Text("Title") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("notifyTitle")
                        )
                        OutlinedTextField(
                            value = message, onValueChange = { message = it },
                            label = { Text("Message") }, minLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("notifyMessage")
                        )
                        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            error = controller.validateMessage(title, message)
                            if (error == null) {
                                val count =
                                    controller.notifyWaitlist(lottery, title, message, senderId)
                                Toast.makeText(
                                    context,
                                    "Sent to $count entrant(s)",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("notifySend")
                    ) { Text("Send") }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
            )
        }



