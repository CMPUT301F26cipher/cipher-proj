package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Owner screen listing their lotteries.
 *
 * NOTE: the "View result as entrant" button is only for testing the entrant
 * screens until notifications (US 01.03.01) link to them. Remove it after that.
 */
@Composable
fun OwnerLotteriesView(
    navController: NavController,
    ownerId: String
) {
    // Shared repository instances; the lists below are Compose State, so this recomposes on change
    val lotteryRepository = RepositoryDatabase.lotteryRepository
    val campsiteRepository = RepositoryDatabase.campsiteRepository
    val entryRepository = RepositoryDatabase.lotteryEntryRepository

    if (!lotteryRepository.isLotteriesLoaded.value) {
        LoadingIndicator()
        return
    }
    val myLotteries = lotteryRepository.getLotteriesFromListByOwnerId(ownerId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "My Lotteries", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = { navController.navigate(LotteryRoutes.CREATE) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("createLotteryButton")
        ) {
            Text("Create Lottery")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (myLotteries.isEmpty()) {
                item { Text("You haven't created any lotteries yet.") }
            }
            items(myLotteries, key = { it.lotteryId }) { lottery ->
                val campsiteNames = lottery.campsiteIds.map { id ->
                    campsiteRepository.getCampsiteFromListById(id)?.campsiteName
                        ?: "Unknown campsite"
                }
                LotteryRow(
                    lottery = lottery,
                    campsiteNames = campsiteNames,
                    onEdit = { navController.navigate(LotteryRoutes.edit(lottery.lotteryId)) },
                    onViewAsEntrant = { navController.navigate(LotteryRoutes.result(lottery.lotteryId)) },
                    onMakeWinner = {
                        val existing = entryRepository
                            .getLotteryEntryFromListByLotteryIdAndUserId(lottery.lotteryId, ownerId)
                        if (existing == null) {
                            entryRepository.addLotteryEntry(
                                lottery.lotteryId, ownerId, LotteryEntryStatus.WINNER.name, 1
                            ) {}
                        } else {
                            entryRepository.updateLotteryEntry(
                                existing.copy(status = LotteryEntryStatus.WINNER.name)
                            ) {}
                        }
                    }
                )
            }
        }
    }
}

/** One lottery in the owner's list. */
@Composable
private fun LotteryRow(
    lottery: Lottery,
    campsiteNames: List<String>,
    onEdit: () -> Unit,
    onViewAsEntrant: () -> Unit,
    onMakeWinner: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = campsiteNames.joinToString(", "),
                style = MaterialTheme.typography.titleMedium
            )
            Text("Registration: ${lottery.startDate} to ${lottery.endDate}")
            Text("Winners: ${lottery.numWinners}    Status: ${lottery.status}")

            Row {
                if (lottery.status == LotteryStatus.OPEN.name) {
                    TextButton(onClick = onEdit) { Text("Edit") }
                }
                // TEMPORARY: for testing the entrant screens, see the file comment
                TextButton(onClick = onViewAsEntrant) { Text("View result as entrant") }
            }
            TextButton(onClick = onMakeWinner) { Text("Make me a winner (demo)") }
        }
    }
}