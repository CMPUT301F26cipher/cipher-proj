package com.example.cipherappproject.ui.lottery

import com.example.cipherappproject.waitlist.WaitlistView
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
 * [currentUserId] is the signed-in user's profile id (looked up by device in CampsiteApp).
 */
object LotteryRoutes {
    const val CREATE = "owner/lotteries/create"
    const val EDIT = "owner/lotteries/{lotteryId}/edit"
    const val RESULT = "lottery/{lotteryId}/result"
    const val WAITLIST = "owner/lotteries/{lotteryId}/waitlist"

    fun edit(lotteryId: String) = "owner/lotteries/$lotteryId/edit"
    fun result(lotteryId: String) = "lottery/$lotteryId/result"
    fun waitlist(lotteryId: String) = "owner/lotteries/$lotteryId/waitlist"
}

/** Adds the lottery sub-screens to a NavHost. */
fun NavGraphBuilder.lotteryScreens(
    navController: NavController,
    currentUserId: String
) {
    composable(LotteryRoutes.CREATE) {
        CreateLotteryView(navController, currentUserId)
    }
    composable(LotteryRoutes.EDIT) { backStackEntry ->
        val lotteryId = backStackEntry.arguments?.getString("lotteryId") ?: ""
        EditLotteryView(navController, currentUserId, lotteryId)
    }
    composable(LotteryRoutes.WAITLIST) { backStackEntry ->
        val lotteryId = backStackEntry.arguments?.getString("lotteryId") ?: ""
        WaitlistView(lotteryId = lotteryId, onBackClick = { navController.popBackStack() })
    }
    composable(LotteryRoutes.RESULT) { backStackEntry ->
        val lotteryId = backStackEntry.arguments?.getString("lotteryId") ?: ""
        LotteryResultScreen(navController, lotteryId, currentUserId)
    }
}
