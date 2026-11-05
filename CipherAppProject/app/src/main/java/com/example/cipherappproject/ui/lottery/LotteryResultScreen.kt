package com.example.cipherappproject.ui.lottery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.cipherappproject.LotteryBookingController
import com.example.cipherappproject.LotteryConstants
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry

/**
 * Entrant screen for one lottery.
 *
 * NOTE: entrantId is PLACEHOLDER_USER_ID (LotteryNavigation.kt) until US 01.09.01 is done.
 */
@Composable
fun LotteryResultScreen(
    navController: NavController,
    lotteryId: String,
    entrantId: String
) {
    val controller = remember { LotteryBookingController() }
    var lottery by remember { mutableStateOf<Lottery?>(null) }
    var entry by remember { mutableStateOf<LotteryEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(lotteryId, entrantId) {
        controller.loadLottery(lotteryId) { loadedLottery ->
            lottery = loadedLottery
            controller.loadEntry(lotteryId, entrantId) { loadedEntry ->
                entry = loadedEntry
                isLoading = false
            }
        }
    }

    val currentLottery = lottery
    val currentEntry = entry

    when {
        isLoading -> LoadingIndicator()
        currentLottery == null -> CenteredMessage("Could not load this lottery.")
        currentEntry == null -> CenteredMessage("You haven't joined this lottery.")

        currentEntry.getStatus() == LotteryConstants.ENTRY_WINNER -> LotteryWinScreen(
            controller = controller,
            lottery = currentLottery,
            entry = currentEntry,
            onDeclined = { entry = currentEntry.copy(status = LotteryConstants.ENTRY_DECLINED) },
            onReserve = { campsiteId, checkIn, checkOut ->
                // TODO(reservation group): create the Reservation for these dates,
                //  then set this entry's status to LotteryConstants.ENTRY_ACCEPTED
            }
        )

        currentEntry.getStatus() == LotteryConstants.ENTRY_NOT_SELECTED ->
            LotteryLoseView(onBack = { navController.popBackStack() })

        currentEntry.getStatus() == LotteryConstants.ENTRY_DECLINED ->
            CenteredMessage("You declined this offer. Your spot has been passed on.")

        currentEntry.getStatus() == LotteryConstants.ENTRY_ACCEPTED ->
            CenteredMessage("You've already reserved through this lottery.")

        else -> CenteredMessage(
            "The draw hasn't happened yet. Check back after ${currentLottery.endDate}."
        )
    }
}
