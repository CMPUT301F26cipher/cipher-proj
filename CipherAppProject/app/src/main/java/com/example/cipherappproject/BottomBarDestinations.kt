package com.example.cipherappproject

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
        AppRoutes.SEARCH,
        "Search",
        icon = Icons.Default.Search,
        "Search for a campsite"
    ),
    PROFILE(
        AppRoutes.PROFILE,
        "Profile",
        icon = Icons.Default.Person,
        "View profile"
    ),

    // Entrant
    ENTRANT_RESERVATIONS(
        AppRoutes.ENTRANT_RESERVATIONS,
        "Reservations",
        icon = Icons.Default.DateRange,
        "View my reservations"
    ),
    MESSAGES(
        AppRoutes.MESSAGES,
        "Messages",
        icon = Icons.Default.Email,
        "View messages"
    ),

    // Owner
    OWNER_CAMPSITES(
        AppRoutes.OWNER_CAMPSITES,
        "Campsites",
        icon = Icons.Default.Home,
        "View my campsites"
    ),
    OWNER_LOTTERIES(
        AppRoutes.OWNER_LOTTERIES,
        "Lotteries",
        icon = Icons.Default.Star,
        "View my lotteries"
    ),
    OWNER_RESERVATIONS(
        AppRoutes.OWNER_RESERVATIONS,
        "Reservations",
        icon = Icons.Default.DateRange,
        "View reservations for my campsites"
    );

    companion object {
        /** Entrant nav-bar: search | my reservations | messages | profile */
        val entrantTabs = listOf(SEARCH, ENTRANT_RESERVATIONS, MESSAGES, PROFILE)

        /** Owner nav-bar: my campsites | my lotteries | reservations | profile */
        val ownerTabs = listOf(OWNER_CAMPSITES, OWNER_LOTTERIES, OWNER_RESERVATIONS, PROFILE)

        fun tabsFor(role: UserRole): List<BottomBarDestinations> {
            return when (role) {
                UserRole.ENTRANT -> entrantTabs
                UserRole.OWNER -> ownerTabs
            }
        }
    }
}