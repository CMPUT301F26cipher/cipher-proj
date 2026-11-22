package com.example.cipherappproject.ui.campsite
/*
--------------------------------------------------------------------------------------
Program Name: CampsiteNavigation.kt
Date Written: 2026-11-20
Author: Madison Levy
Purpose: All owner campsite routes in one place
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-20  Madison    1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */
import com.example.cipherappproject.ui.lottery.LotteryRoutes
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Owner campsite routes.
 *
 * Routes:
 *  - owner/campsites/register
 *  - owner/campsites/detail/{campsiteId}
 *  - owner/campsites/edit/{campsiteId}
 *
 */
object CampsiteRoutes {
    const val REGISTER = "owner/campsites/register"
    const val DETAIL = "owner/campsites/detail/{campsiteId}"
    const val EDIT = "owner/campsites/edit/{campsiteId}"

    fun detail(campsiteId: String) = "owner/campsites/detail/$campsiteId"
    fun edit(campsiteId: String) = "owner/campsites/edit/$campsiteId"
}
fun NavGraphBuilder.ownerCampsiteScreens(
    navController: NavController,
    currentUserId: String
) {
    composable(CampsiteRoutes.REGISTER) {
        RegisterCampsiteView(
            ownerId = currentUserId,
            // Back to owner's campsites, where the new campsite now appears
            onSaveSuccess = { navController.popBackStack() },
            onCancel = { navController.popBackStack() }
        )
    }
    composable(CampsiteRoutes.DETAIL) { backStackEntry ->
        val campsiteId = backStackEntry.arguments?.getString("campsiteId") ?: ""
        OwnerCampsiteView(
            campsiteId = campsiteId,
            ownerId = currentUserId,
            onEditClick = { navController.navigate(CampsiteRoutes.edit(campsiteId)) },
            onLotteryClick = { navController.navigate(LotteryRoutes.waitlist(it)) }
        )
    }
    composable(CampsiteRoutes.EDIT) { backStackEntry ->
        val campsiteId = backStackEntry.arguments?.getString("campsiteId") ?: ""
        EditCampsiteView(
            campsiteId = campsiteId,
            ownerId = currentUserId,
            // Back to the details page, which now shows the changes
            onSaveSuccess = { navController.popBackStack() },
            onCancel = { navController.popBackStack() }
        )
    }
}
