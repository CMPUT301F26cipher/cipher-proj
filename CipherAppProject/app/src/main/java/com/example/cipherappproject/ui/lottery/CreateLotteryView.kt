package com.example.cipherappproject.ui.lottery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.cipherappproject.LotteryController
import com.example.cipherappproject.LotteryFormInput
import com.example.cipherappproject.database.repositories.CampsiteRepository


/**
 * Owner screen for creating a lottery over one or more of their campsites
 *
 * NOTE: ownerId is PLACEHOLDER_USER_ID (LotteryNavigation.kt) until US 01.09.01 is done.
 */
@Composable
fun CreateLotteryView(
    navController: NavController,
    ownerId: String,
    initialCampsiteId: String? = null 
) {
    val controller = remember { LotteryController() }
    val campsiteRepository = remember { CampsiteRepository() }
    // Live list, so this recomposes once Firestore has loaded the campsites
    val ownerCampsites = campsiteRepository.campsites.filter { it.ownerId == ownerId }
    var isSaving by remember { mutableStateOf(false) }
    var input by remember(ownerId, initialCampsiteId)
        { mutableStateOf(LotteryFormInput(
                campsiteIds = initialCampsiteId
                    ?.takeIf { it.isNotBlank()}
                    ?.let { setOf(it)}
                    ?: emptySet()
            )
        ) 
    }
    

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LotteryForm(
        
        title = "Create Lottery",
        campsites = ownerCampsites,
        input = input,
        onInputChange = { 
            if (!isSaving) {
                input = it 
                errorMessage = null
            }
        },
        errorMessage = errorMessage,
        isSaving = isSaving,
        saveLabel = if (isSaving) "Saving..." else "Publish Lottery",
        onSave = {
            if (!isSaving) {
                isSaving = true
                errorMessage = null

                controller.createAndSaveLottery(
                    ownerId = ownerId,
                    input = input,
                    onSuccess = {
                        isSaving = false
                        navController.popBackStack()
                    },
            onFailure = { message ->
                isSaving = false
                errorMessage = message
            }
        )
        }
        }
    )
}
