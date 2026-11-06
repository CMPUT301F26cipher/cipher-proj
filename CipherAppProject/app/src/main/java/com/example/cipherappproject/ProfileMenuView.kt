package com.example.cipherappproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** One row in the profile menu. */
data class ProfileMenuItem(
    val label: String,
    val route: String
)

/** Menu rows for each role. */
fun profileMenuItemsFor(role: UserRole): List<ProfileMenuItem> {
    return when (role) {
        UserRole.ENTRANT -> listOf(
            ProfileMenuItem("Profile Settings", AppRoutes.PROFILE_SETTINGS),
            ProfileMenuItem("Campsites Available", AppRoutes.SEARCH),
            ProfileMenuItem("My Reservations", AppRoutes.ENTRANT_RESERVATIONS),
            ProfileMenuItem("My Lotteries", AppRoutes.ENTRANT_LOTTERIES),
            ProfileMenuItem("Notifications", AppRoutes.ENTRANT_NOTIFICATIONS),
            ProfileMenuItem("Scan QR Code", AppRoutes.SCAN_QR),
            ProfileMenuItem("Messages and Updates", AppRoutes.MESSAGES),
            ProfileMenuItem("Policy and Guidelines", AppRoutes.POLICY)
        )
        UserRole.OWNER -> listOf(
            ProfileMenuItem("Profile Settings", AppRoutes.PROFILE_SETTINGS),
            ProfileMenuItem("My Campsites", AppRoutes.OWNER_CAMPSITES),
            ProfileMenuItem("My Lotteries", AppRoutes.OWNER_LOTTERIES),
            ProfileMenuItem("Reservations", AppRoutes.OWNER_RESERVATIONS),
            ProfileMenuItem("Sent Notifications", AppRoutes.OWNER_NOTIFICATIONS),
            ProfileMenuItem("Policy and Guidelines", AppRoutes.POLICY)
        )
    }
}

/**
 * Profile tab
 *
 * @param role decides which rows are shown
 * @param onOpen called with the route of the tapped row
 */
@Composable
fun ProfileMenuView(
    role: UserRole,
    onOpen: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("profileMenu")
    ) {
        // Header: picture, name, role
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO(profile group): show the user's profile picture
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Profile picture",
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // TODO(profile group): show the user's name once login exists
                Text(text = "User Name", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (role == UserRole.ENTRANT) "Entrant" else "Campsite Owner",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        HorizontalDivider()

        for (item in profileMenuItemsFor(role)) {
            ListItem(
                headlineContent = { Text(item.label) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier
                    .clickable { onOpen(item.route) }
                    .testTag("profileMenu:${item.route}")
            )
            HorizontalDivider()
        }

        TextButton(
            onClick = { /* TODO(profile group): delete profile */ },
            modifier = Modifier
                .padding(8.dp)
                .testTag("deleteProfileButton")
        ) {
            Text("Delete Profile", color = MaterialTheme.colorScheme.error)
        }
    }
}
