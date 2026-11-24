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
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Shows the profile settings screen. Also has a switch that turns notifications on or off
 * The choice is saved onto the profile's notificationsEnabled field
 *
 * @param, currentUserId the signed-in user's profile id
 */
@Composable
fun ProfileSettingsView(currentUserId: String) {
    val profileRepository = RepositoryDatabase.profileRepository
    val context = LocalContext.current
    val profile = profileRepository.getProfileFromListById(currentUserId)
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("profileSettingsView")
    ) {
        Text("Profile Settings", style = MaterialTheme.typography.headlineMedium)

        if (profile == null) {
            Text("Loading profile...")
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))

            Switch(
                checked = profile.notificationsEnabled,
                onCheckedChange = {enabled ->
                    profileRepository.updateProfile(profile.copy(notificationsEnabled = enabled)) { result ->
                        result.onFailure {
                            Toast.makeText(context, "Could not save setting", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.testTag("notificationsSwitch")
            )
        }
    }
}