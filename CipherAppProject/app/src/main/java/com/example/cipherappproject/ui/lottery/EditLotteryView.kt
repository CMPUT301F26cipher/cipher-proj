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
 * ownerId is the signed-in user's profile id (looked up by device in CampsiteApp).
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
    var isSaving by remember { mutableStateOf(false) }

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
            isSaving = isSaving,
            onSave = {
                if (isSaving) return@LotteryForm
                isSaving = true
                errorMessage = null
                val validationError = controller.updateLottery(lottery, input) { updateResult ->
                    updateResult
                        .onSuccess { navController.popBackStack() }
                        .onFailure {
                            isSaving = false
                            errorMessage = it.message ?: "Could not save changes. Please try again."
                        }
                }
                // A validation error means nothing was sent, so allow another try
                if (validationError != null) {
                    errorMessage = validationError
                    isSaving = false
                }
            }
        )
    }
}