package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
/*
 * Built by Bailey
 * Nov 2 2026
 * Updates:
 *
 */
@Composable
fun ProfileView(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize().testTag("profileColumn"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Profile info/options go here")
    }
}