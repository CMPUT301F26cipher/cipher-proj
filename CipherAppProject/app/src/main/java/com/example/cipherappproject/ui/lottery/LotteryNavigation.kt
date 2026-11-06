package com.example.cipherappproject.ui.lottery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

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
    const val CREATE = "owner/lotteries/create"
    const val EDIT = "owner/lotteries/{lotteryId}/edit"
    const val RESULT = "lottery/{lotteryId}/result"

    fun edit(lotteryId: String) = "owner/lotteries/$lotteryId/edit"
    fun result(lotteryId: String) = "lottery/$lotteryId/result"
}

/** Adds the lottery sub-screens to a NavHost. */
fun NavGraphBuilder.lotteryScreens(
    navController: NavController,
    currentUserId: String = PLACEHOLDER_USER_ID
) {
    composable(LotteryRoutes.CREATE) {
        CreateLotteryView(navController, currentUserId)
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
