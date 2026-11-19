package com.example.cipherappproject.ui.lottery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.cipherappproject.LotteryController
import com.example.cipherappproject.LotteryFormInput
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Owner screen for updating an existing lottery listing (US 02.04.06).
 *
 * NOTE: ownerId is PLACEHOLDER_USER_ID (LotteryNavigation.kt) until US 01.09.01 is done.
 */
@Composable
fun EditLotteryView(
    navController: NavController,
    ownerId: String,
    lotteryId: String
) {
    val controller = remember { LotteryController() }
    val ownerCampsites = RepositoryDatabase.campsiteRepository.getCampsiteFromListByOwnerId(ownerId)

    var original by remember { mutableStateOf<Lottery?>(null) }
    var input by remember { mutableStateOf(LotteryFormInput()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadFailed by remember { mutableStateOf(false) }

    LaunchedEffect(lotteryId) {
        controller.loadLottery(lotteryId) { loadResult ->
            loadResult
                .onSuccess { lottery ->
                    original = lottery
                    input = controller.toFormInput(lottery)
                }
                .onFailure { loadFailed = true }
        }
    }

    val lottery = original
    when {
        loadFailed -> CenteredMessage("Could not load this lottery.")
        lottery == null -> LoadingIndicator()
        lottery.ownerId != ownerId -> CenteredMessage("You can only edit your own lotteries.")
        !controller.canEdit(lottery) ->
            CenteredMessage("This lottery has already been drawn and can no longer be edited.")
        else -> LotteryForm(
            title = "Edit Lottery",
            campsites = ownerCampsites,
            input = input,
            onInputChange = { input = it },
            errorMessage = errorMessage,
            saveLabel = "Save Changes",
            onSave = {
                errorMessage = controller.updateLottery(lottery, input) { updateResult ->
                    updateResult
                        .onSuccess { navController.popBackStack() }
                        .onFailure {
                            errorMessage = it.message ?: "Could not save changes. Please try again."
                        }
                }
            }
        )
    }
}