package com.example.cipherappproject

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
 * Satisfy user story US 01.10.01
 */

@Composable
fun ProfileSettingsView(currentUserId: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("profileSettingsView")
    ) {
        Text("Profile Settings", style = MaterialTheme.typography.headlineMedium)
        // TODO: Row { Text("Receive notifications"); Switch(...) } -> updateProfile(notificationsEnabled)
    }
}