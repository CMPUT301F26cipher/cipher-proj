package com.example.cipherappproject.ratings

/*
--------------------------------------------------------------------------------------
Program Name: RatingNavigation.kt
Date Written: November 21, 2026
Author: Nethmini Mackenzy
Purpose: Routes for the ratings & reviews screens
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-21  Nethmini   1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Rating routes. Rate lives under entrant/reservations so the Reservations tab stays highlighted.
 */
object RatingRoutes {
    const val RATE = "entrant/reservations/rate/{reservationId}"
    fun rate(reservationId: String) = "entrant/reservations/rate/$reservationId"
}

fun NavGraphBuilder.ratingScreens(
    navController: NavController,
    currentUserId: String
) {
    composable(RatingRoutes.RATE) { backStackEntry ->
        val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
        RateCampsiteView(
            reservationId = reservationId,
            userId = currentUserId,
            onDone = { navController.popBackStack() }
        )
    }
}
