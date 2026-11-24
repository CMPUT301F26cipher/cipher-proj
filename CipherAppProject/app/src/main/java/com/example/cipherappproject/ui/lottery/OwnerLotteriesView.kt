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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.cipherappproject.LotteryConstants
import com.example.cipherappproject.LotteryController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cipherappproject.SendNotificationDialog
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.provider.RepositoryDatabase

/**
 * Owner screen listing their lotteries.
 */
@Composable
fun OwnerLotteriesView(
    navController: NavController,
    ownerId: String
) {
    // Shared repository instances; the lists below are Compose State, so this recomposes on change
    val lotteryRepository = RepositoryDatabase.lotteryRepository
    val campsiteRepository = RepositoryDatabase.campsiteRepository
    val controller = remember { LotteryController() }

    // null = show every status
    var statusFilter by rememberSaveable { mutableStateOf<String?>(null) }

    // The lottery whose otify waitlist dialog is open, or null when its closed.
    var notifyLottery by remember { mutableStateOf<Lottery?>(null)}

    if (!lotteryRepository.isLotteriesLoaded.value) {
        LoadingIndicator()
        return
    }
    val allMyLotteries = lotteryRepository.getLotteriesFromListByAnyOwnerId(ownerId)
    val myLotteries = controller.filterByStatus(allMyLotteries, statusFilter)

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

        StatusFilterChips(
            selected = statusFilter,
            onSelected = { statusFilter = it },
            statusText = controller::statusText
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (allMyLotteries.isEmpty()) {
                item { Text("You haven't created any lotteries yet.") }
            } else if (myLotteries.isEmpty()) {
                item { Text("No lotteries with this status.") }
            }
            items(myLotteries, key = { it.lotteryId }) { lottery ->
                val campsiteNames = lottery.campsiteIds.map { id ->
                    campsiteRepository.getCampsiteFromListById(id)?.campsiteName
                        ?: "Unknown campsite"
                }
                LotteryRow(
                    lottery = lottery,
                    statusText = controller.statusText(lottery.status),
                    canEdit = controller.canEdit(lottery),
                    campsiteNames = campsiteNames,
                    onEdit = { navController.navigate(LotteryRoutes.edit(lottery.lotteryId)) },
                    onNotify = {notifyLottery = lottery}
                )
            }
        }
        notifyLottery?.let {lottery ->
            SendNotificationDialog(lottery, senderId = ownerId, onDismiss = { notifyLottery = null})
        }
    }
}

/** One lottery in the owner's list. */
@Composable
private fun LotteryRow(
    lottery: Lottery,
    statusText: String,
    canEdit: Boolean,
    campsiteNames: List<String>,
    onEdit: () -> Unit,
    onNotify: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = campsiteNames.joinToString(", "),
                style = MaterialTheme.typography.titleMedium
            )
            Text("Registration: ${lottery.startDate} to ${lottery.endDate}")
            Text("Winners: ${lottery.numWinners}    Status: $statusText")
            Text("Waitlist capacity: ${capacityText(lottery.maxParticipants)}")

            if (canEdit) {
                TextButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("editLottery_${lottery.lotteryId}")
                ) { Text("Edit") }
            }
            TextButton(
                onClick = onNotify,
                modifier = Modifier.testTag("notifyLottery_${lottery.lotteryId}")
            ) { Text("Notify Waitlist")}
        }
    }
}
/** "Unlimited" or the waitlist limit. */
private fun capacityText(maxParticipants: Int?): String {
    return if (maxParticipants == null || maxParticipants == LotteryConstants.UNLIMITED_PARTICIPANTS) {
        "Unlimited"
    } else {
        maxParticipants.toString()
    }
}

/** One chip per lottery status, plus "All". */
@Composable
private fun StatusFilterChips(
    selected: String?,
    onSelected: (String?) -> Unit,
    statusText: (String) -> String
) {
    // Lifecycle order rather than enum order
    val options = listOf(
        null,
        LotteryStatus.OPEN.name,
        LotteryStatus.PROCESSING.name,
        LotteryStatus.COMPLETED.name,
        LotteryStatus.CANCELLED.name
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { status ->
            FilterChip(
                selected = status == selected,
                onClick = { onSelected(status) },
                label = { Text(if (status == null) "All" else statusText(status)) },
                modifier = Modifier.testTag("lotteryFilter_${status ?: "ALL"}")
            )
        }
    }
}
