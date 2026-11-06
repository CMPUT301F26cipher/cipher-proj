package com.example.cipherappproject.ui.campsite

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
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.WaitlistController
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Lottery
import java.time.LocalDate

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
            text = campsite.getCampsiteName(),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(campsite.getCampsiteDescription())

        Text(
            "Maximum Guests: ${campsite.getCampsiteMaxGuests()}"
        )

        Text(
            "Price per Night: $${campsite.getCampsitePricePerNight()}"
        )

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
            label = { Text("Start Date (yyyy-MM-dd)") }
        )

        OutlinedTextField(
            value = endDate,
            onValueChange = {
                endDate = it
                availableLottery = null
                message = ""
            },
            label = { Text("End Date (yyyy-MM-dd)") }
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
            }
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
                    ) { success ->
                        joining = false

                        message = if (success) {
                            "Successfully joined the waiting list!"
                        } else {
                            "Unable to join the waiting list."
                        }
                    }
                }
            ) {
                Text("Join Waitlist")
            }
        }

        if (message.isNotEmpty()) {
            Text(message)
        }
    }
}
