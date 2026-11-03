package com.example.cipherappproject

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomBarDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
) {
    SEARCH(
        "search",
        "Search",
        icon = Icons.Default.Search,
        "Search for a campsite"
    ),
    PROFILE(
        "profile",
        "Profile",
        icon = Icons.Default.Person,
        "View profile"
    )
}