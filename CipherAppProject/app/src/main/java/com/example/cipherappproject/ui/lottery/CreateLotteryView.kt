package com.example.cipherappproject.ui.lottery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.cipherappproject.LotteryController
import com.example.cipherappproject.LotteryFormInput
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Owner screen for creating a lottery over one or more of their campsites
 *
 * ownerId is the signed-in user's profile id (looked up by device in CampsiteApp).
 */
@Composable
fun CreateLotteryView(
    navController: NavController,
    ownerId: String
) {
    val controller = remember { LotteryController() }
    // Shared repository; its list is Compose State, so this recomposes once Firestore has loaded
    val ownerCampsites = RepositoryDatabase.campsiteRepository.getCampsiteFromListByOwnerId(ownerId)

    var input by remember { mutableStateOf(LotteryFormInput()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Blocks repeat taps while a save is in flight, so one tap = one lottery
    var isSaving by remember { mutableStateOf(false) }

    LotteryForm(
        title = "Create Lottery",
        campsites = ownerCampsites,
        input = input,
        onInputChange = { input = it },
        errorMessage = errorMessage,
        saveLabel = "Publish Lottery",
        isSaving = isSaving,
        onSave = {
            if (isSaving) return@LotteryForm
            isSaving = true
            errorMessage = null
            // The callback can run before this returns (e.g. ownership check), so only
            // overwrite errorMessage when there is a validation error
            val validationError = controller.createFromForm(ownerId, input) { saveResult ->
                saveResult
                    .onSuccess { navController.popBackStack() }
                    .onFailure {
                        isSaving = false
                        errorMessage = it.message ?: "Could not save lottery. Please try again."
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