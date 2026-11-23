package com.example.cipherappproject.ui.campsite
/*
--------------------------------------------------------------------------------------
Program Name: CampsiteDetailView.kt
Date Written: Unknown
Author: Unknown
Purpose: Details page for a campsite, opened by tapping a campsite in the entrant search results. Also lets the entrant join the campsite's lottery.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-02  Unknown     1.01     Lottery
2026-11-18  Madison    1.02     Added campsite details
2026-11-21  Nethmini   1.03     Lottery section lists open lotteries to join/leave
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.LotteryConstants
import com.example.cipherappproject.LotteryController
import com.example.cipherappproject.WaitlistController
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.ratings.CampsiteReviewsSection
import java.util.Locale

@Composable
fun CampsiteDetailView(
    campsite: Campsite,
    isOwner: Boolean,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            text = campsite.campsiteName,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(campsite.description)

        Text(
            "Maximum Guests: ${campsite.maxGuests}"
        )

        Text(
            String.format(Locale.CANADA, "Price per Night: $%.2f", campsite.pricePerNight)
        )
        if (campsite.mapLocation.isNotEmpty()) {
            val latitude = campsite.mapLocation.getOrNull(0) ?: "?"
            val longitude = campsite.mapLocation.getOrNull(1) ?: "?"
            val locationName = campsite.mapLocation.getOrNull(2).orEmpty()
            Text("Location: $locationName ($latitude, $longitude)")
        }

        if (campsite.amenities.isNotEmpty()) {
            Text("Amenities: ${campsite.amenities.joinToString(", ")}")
        }

        if (campsite.tags.isNotEmpty()) {
            Text("Tags: ${campsite.tags.joinToString(", ")}")
        }

        if (campsite.rules.isNotEmpty()) {
            Text("Rules: ${campsite.rules.joinToString(", ")}")
        }

        if (campsite.policies.isNotEmpty()) {
            Text("Policies: ${campsite.policies.joinToString(", ")}")
        }

        if (isOwner) {
            Button(onClick = onEditClick) {
                Text("Edit Campsite")
            }
        }

        CampsiteReviewsSection(campsite.campsiteId)
    }
}

/**
 * Lets an entrant join (US 01.01.06, 01.02.01) or leave (US 01.02.02) the waiting list of
 * each lottery this campsite is listed in whose registration is open today.
 * Reads the repositories' live lists, so it updates as soon as an entry changes.
 */
@Composable
fun CampsiteLotterySection(
    campsiteId: String,
    entrantId: String,
    controller: WaitlistController
) {
    val lotteryController = remember { LotteryController() }
    val openLotteries = controller.getOpenLotteries(campsiteId)

    // result of the last join/leave, per lottery id
    var messages by remember { mutableStateOf(mapOf<String, String>()) }
    var working by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Campsite Lotteries", style = MaterialTheme.typography.titleMedium)

        if (openLotteries.isEmpty()) {
            Text(
                "This campsite has no lottery open for registration right now.",
                modifier = Modifier.testTag("noOpenLottery")
            )
        }

        openLotteries.forEach { lottery ->
            val entry = controller.getEntry(lottery.lotteryId, entrantId)
            val onWaitlist = entry?.status == LotteryEntryStatus.REGISTERED.name
            val joinedBefore = entry != null && entry.status != LotteryEntryStatus.WITHDRAWN.name
            val size = lotteryController.getWaitlistSize(lottery.lotteryId)
            val full = lotteryController.isWaitlistFull(lottery)
            val capacity = lottery.maxParticipants
                ?.takeIf { it != LotteryConstants.UNLIMITED_PARTICIPANTS }

            Card(modifier = Modifier.fillMaxWidth().testTag("openLottery_${lottery.lotteryId}")) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Registration open until ${lottery.endDate}")
                    Text("Winners drawn: ${lottery.numWinners}")
                    if (size != null) {
                        Text(
                            if (capacity != null) "On the waiting list: $size / $capacity"
                            else "On the waiting list: $size"
                        )
                    }

                    when {
                        onWaitlist -> {
                            Text("You're on the waiting list.")
                            OutlinedButton(
                                enabled = working == null,
                                onClick = {
                                    working = lottery.lotteryId
                                    controller.leaveWaitlist(entry!!) { ok ->
                                        working = null
                                        messages = messages + (lottery.lotteryId to
                                                if (ok) "You left the waiting list."
                                                else "Unable to leave the waiting list.")
                                    }
                                },
                                modifier = Modifier.testTag("leaveWaitlistButton")
                            ) { Text("Leave Waitlist") }
                        }

                        joinedBefore -> Text("You've already taken part in this lottery.")

                        full -> Text("This lottery's waiting list is full.")

                        else -> Button(
                            enabled = working == null,
                            onClick = {
                                working = lottery.lotteryId
                                controller.joinWaitlist(lottery.lotteryId, entrantId) { outcome ->
                                    working = null
                                    messages = messages + (lottery.lotteryId to when (outcome) {
                                        WaitlistController.JoinResult.JOINED ->
                                            "Successfully joined the waiting list! See My Lotteries."
                                        WaitlistController.JoinResult.ALREADY_JOINED ->
                                            "You have already joined this lottery."
                                        WaitlistController.JoinResult.FULL ->
                                            "This lottery's waiting list is full."
                                        WaitlistController.JoinResult.CLOSED ->
                                            "Registration for this lottery has closed."
                                        WaitlistController.JoinResult.NOT_READY ->
                                            "Still loading. Please try again in a moment."
                                        WaitlistController.JoinResult.FAILED ->
                                            "Unable to join the waiting list."
                                    })
                                }
                            },
                            modifier = Modifier.testTag("joinWaitlistButton")
                        ) { Text("Join Waitlist") }
                    }

                    messages[lottery.lotteryId]?.let {
                        Text(it, modifier = Modifier.testTag("lotteryMessage"))
                    }
                }
            }
        }
    }
}
