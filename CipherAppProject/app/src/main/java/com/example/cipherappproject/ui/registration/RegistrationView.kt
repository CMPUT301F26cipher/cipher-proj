package com.example.cipherappproject.ui.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

enum class RegistrationStep {
    USER_SELECTION,
    ENTRANT_PROFILE,
    OWNER_PROFILE
}

@Composable
fun Registration(
    onEntrantRegistration: (EntrantDetails) -> Unit,
    onOwnerRegistration: (OwnerDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember {
        mutableStateOf(RegistrationStep.USER_SELECTION)
    }

    when (currentScreen) {

        RegistrationStep.USER_SELECTION -> {
            userSelectionView(
                onEntrantClick = {
                    currentScreen = RegistrationStep.ENTRANT_PROFILE
                },
                onCampsiteOwnerClick = {
                    currentScreen = RegistrationStep.OWNER_PROFILE
                },
                modifier = modifier
            )
        }

        RegistrationStep.ENTRANT_PROFILE -> {
            entrantProfileView(
                onConfirmRegistration = { entrantDetails ->
                    onEntrantRegistration(entrantDetails)
                },
                modifier = modifier
            )
        }

        RegistrationStep.OWNER_PROFILE -> {
            ownerProfileView(
                onConfirmRegistration = { ownerDetails ->
                    onOwnerRegistration(ownerDetails)
                },
                modifier = modifier
            )
        }
    }
}