package com.example.cipherappproject.ui.lottery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.cipherappproject.LotteryBookingController
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Entrant screen for one lottery.
 *
 * Reads the lottery and the entrant's entry from the shared repositories' local lists
 * (Compose State), so the screen updates by itself when the entry's status changes
 * (e.g. after declining or reserving).
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

    // Wait for the first load of both lists before deciding something is missing
    if (!RepositoryDatabase.lotteryRepository.isLotteriesLoaded.value ||
        !RepositoryDatabase.lotteryEntryRepository.isLotteriesEntriesLoaded.value
    ) {
        LoadingIndicator()
        return
    }

    val currentLottery = controller.getLottery(lotteryId)
    val currentEntry = controller.getEntry(lotteryId, entrantId)

    when {
        currentLottery == null -> CenteredMessage("Could not load this lottery.")
        currentEntry == null -> CenteredMessage("You haven't joined this lottery.")

        currentEntry.status == LotteryEntryStatus.WINNER.name -> LotteryWinScreen(
            controller = controller,
            lottery = currentLottery,
            entry = currentEntry
        )

        currentEntry.status == LotteryEntryStatus.NOT_SELECTED.name ->
            LotteryLoseView(onBack = { navController.popBackStack() })

        currentEntry.status == LotteryEntryStatus.DECLINED.name ->
            CenteredMessage("You declined this offer. Your spot has been passed on.")

        currentEntry.status == LotteryEntryStatus.ACCEPTED.name ->
            CenteredMessage("You've already reserved through this lottery.")

        currentEntry.status == LotteryEntryStatus.WITHDRAWN.name ->
            CenteredMessage("You left the waiting list for this lottery.")

        else -> CenteredMessage(
            "The draw hasn't happened yet. Check back after ${currentLottery.endDate}."
        )
    }
}
