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
import com.example.cipherappproject.WaitlistController
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Lottery
import java.time.LocalDate
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
    }
}

@Composable
fun CampsiteLotterySection(
    campsiteId: String,
    entrantId: String,
    controller: WaitlistController
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var availableLottery by remember {
        mutableStateOf<Lottery?>(null)
    }

    var message by remember { mutableStateOf("") }
    var checking by remember { mutableStateOf(false) }
    var joining by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Join a Campsite Lottery",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = startDate,
            onValueChange = {
                startDate = it
                availableLottery = null
                message = ""
            },
            label = { Text("Start Date (yyyy-MM-dd)") },
            modifier = Modifier.testTag("lotteryStartDate")
        )

        OutlinedTextField(
            value = endDate,
            onValueChange = {
                endDate = it
                availableLottery = null
                message = ""
            },
            label = { Text("End Date (yyyy-MM-dd)") },
            modifier = Modifier.testTag("lotteryEndDate")
        )

        Button(
            enabled = !checking && !joining,
            onClick = {
                val start = runCatching {
                    LocalDate.parse(startDate)
                }.getOrNull()

                val end = runCatching {
                    LocalDate.parse(endDate)
                }.getOrNull()

                if (start == null || end == null ||
                    !end.isAfter(start)
                ) {
                    message = "Please enter valid dates."
                } else {
                    checking = true
                    availableLottery = null
                    message = ""

                    controller.findAvailableLottery(
                        campsiteId,
                        start,
                        end
                    ) { lottery ->
                        availableLottery = lottery
                        checking = false

                        if (lottery == null) {
                            message =
                                "No eligible lottery found."
                        }
                    }
                }
            },
            modifier = Modifier.testTag("checkLotteryButton")
        ) {
            Text("Check Lottery Availability")
        }

        if (availableLottery != null) {
            Text("An eligible lottery is available.")

            Button(
                enabled = !joining,
                onClick = {
                    joining = true

                    controller.joinWaitlist(
                        lotteryId = availableLottery!!.lotteryId,
                        userId = entrantId
                    ) { outcome ->
                        joining = false

                        message = when (outcome) {
                            WaitlistController.JoinResult.JOINED ->
                                "Successfully joined the waiting list! See My Lotteries."
                            WaitlistController.JoinResult.ALREADY_JOINED ->
                                "You have already joined this lottery."
                            WaitlistController.JoinResult.FULL ->
                                "This lottery's waiting list is full."
                            WaitlistController.JoinResult.NOT_READY ->
                                "Still loading. Please try again in a moment."
                            WaitlistController.JoinResult.FAILED ->
                                "Unable to join the waiting list."
                        }
                        if (outcome == WaitlistController.JoinResult.JOINED ||
                            outcome == WaitlistController.JoinResult.ALREADY_JOINED
                        ) {
                            availableLottery = null
                        }
                    }
                },
                modifier = Modifier.testTag("joinWaitlistButton")
            ) {
                Text("Join Waitlist")
            }
        }

        if (message.isNotEmpty()) {
            Text(message, modifier = Modifier.testTag("lotteryMessage"))
        }
    }
}
