package com.example.cipherappproject

/*
 * Nov 5 2026
 * Route names and user roles used by CampsiteApp's navigation.
 * Lottery routes are kept separately in ui/lottery/LotteryNavigation.kt (LotteryRoutes).
 */

/**
 * Which nav-bar and profile menu the app shows.
 *
 * TEMPORARY: switched with the button in the top bar until login/sign-up (Madison)
 * can pick the role from the current user's Profile.role.
 */
enum class UserRole {
    ENTRANT,
    OWNER
}

/**
 * Routes for the main screens (the ones the nav-bar, bell and profile menu open).
 *
 * Screens reached from inside a main screen keep their routes in their own feature's file,
 * e.g. create/edit lottery and lottery results are in LotteryRoutes (ui/lottery/LotteryNavigation.kt).
 */
object AppRoutes {
    // Entrant
    const val SEARCH = "search"
    const val ENTRANT_RESERVATIONS = "entrant/reservations"
    const val ENTRANT_LOTTERIES = "entrant/lotteries"
    const val MESSAGES = "entrant/messages"
    const val ENTRANT_NOTIFICATIONS = "entrant/notifications"
    const val SCAN_QR = "entrant/scan"

    // Campsite page opened from search (CampsiteSearch takes a callback, so it's wired here)
    const val CAMPSITE = "campsite/{campsiteId}"
    fun campsite(campsiteId: String) = "campsite/$campsiteId"

    // Owner
    const val OWNER_CAMPSITES = "owner/campsites"
    const val OWNER_LOTTERIES = "owner/lotteries"
    const val OWNER_RESERVATIONS = "owner/reservations"
    const val OWNER_NOTIFICATIONS = "owner/notifications"

    // Shared
    const val PROFILE = "profile"
    const val PROFILE_SETTINGS = "profile/settings"
    const val POLICY = "profile/policy"

    /** The bell in the top right: received notifications for entrants, sent ones for owners. */
    fun notificationsFor(role: UserRole): String {
        return when (role) {
            UserRole.ENTRANT -> ENTRANT_NOTIFICATIONS
            UserRole.OWNER -> OWNER_NOTIFICATIONS
        }
    }
}
