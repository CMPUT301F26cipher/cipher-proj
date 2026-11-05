package com.example.cipherappproject.ui.lottery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * All lottery routes in one place
 *
 * Routes:
 *  - owner/lotteries
 *  - owner/lotteries/create
 *  - owner/lotteries/{lotteryId}/edit
 *  - lottery/{lotteryId}/result
 *
 * NOTE: [PLACEHOLDER_USER_ID] stands in for the current user until device
 * identification (US 01.09.01) exists. The same ID acts as owner and entrant for testing.
 */
object LotteryRoutes {
    const val OWNER_LOTTERIES = "owner/lotteries"
    const val CREATE = "owner/lotteries/create"
    const val EDIT = "owner/lotteries/{lotteryId}/edit"
    const val RESULT = "lottery/{lotteryId}/result"
    const val CREATE_WITH_CAMPSITE = "$CREATE?campsiteId={campsiteId}"

    /** Builds a creation route with a saved campsite preselected. */
    fun createForCampsite(campsiteId: String): String {
        return "$CREATE?campsiteId=${Uri.encode(campsiteId)}"
    }

    fun edit(lotteryId: String) = "owner/lotteries/$lotteryId/edit"
    fun result(lotteryId: String) = "lottery/$lotteryId/result"
}

const val PLACEHOLDER_USER_ID = "test-user-1"

/** Adds every lottery screen to a NavHost. */
fun NavGraphBuilder.lotteryScreens(
    navController: NavController,
    currentUserId: String = PLACEHOLDER_USER_ID
) {
    composable(LotteryRoutes.OWNER_LOTTERIES) {
        OwnerLotteriesView(navController, currentUserId)
    }
    composable(
        route = LotteryRoutes.CREATE_WITH_CAMPSITE,
        arguments = listOf(
            navArgument("campsiteId") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
        ) { backStackEntry ->
            val campsiteId = backStackEntry.arguments
                ?.getString("campsiteId")
                ?.takeIf { it.isNotBlank() }
            CreateLotteryView(navController = navController, ownerId = currentUserId, initialCampsiteId = campsiteId)
    }
    composable(LotteryRoutes.EDIT) { backStackEntry ->
        val lotteryId = backStackEntry.arguments?.getString("lotteryId") ?: ""
        EditLotteryView(navController, currentUserId, lotteryId)
    }
    composable(LotteryRoutes.RESULT) { backStackEntry ->
        val lotteryId = backStackEntry.arguments?.getString("lotteryId") ?: ""
        LotteryResultScreen(navController, lotteryId, currentUserId)
    }
}
