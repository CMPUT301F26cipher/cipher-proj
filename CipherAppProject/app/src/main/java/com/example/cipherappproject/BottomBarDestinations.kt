package com.example.cipherappproject

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 *
 *
 * Contains information necessary to build nav-bar
 */
/*
 * Adapted from https://github.com/android/snippets/blob/main/compose/snippets/src/main/java/com/example/compose/snippets/components/Navigation.kt
 * enum class Destination(...)
 *
 * Built by Bailey Nov 2 2026
 * Updates:
 *
 */
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