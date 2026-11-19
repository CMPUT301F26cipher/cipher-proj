package com.example.cipherappproject.reservations

//@Composable

//editable textbox for booking period and reservation period
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeParseException

/*
--------------------------------------------------------------------------------------
Program Name: RegisterCampsitePeriodView
Date Written: 2026-11-05
Author: Hye-Sung
Purpose: For the owner to select a specific period in which the campsite can be registered for
US: 02.04.03
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Initial implementation
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

data class LotteryPeriod(
    val registrationStart: LocalDate,
    val registrationEnd: LocalDate,
    val drawDate: LocalDate,
    val activeStart: LocalDate,
    val activeEnd: LocalDate
)

@Composable
fun RegisterCampsitePeriodView(
    onConfirm: (LotteryPeriod) -> Unit,
    onCancel: () -> Unit = {}
) {
    var registrationStart by remember {
        mutableStateOf("")
    }

    var registrationEnd by remember {
        mutableStateOf("")
    }

    var drawDate by remember {
        mutableStateOf("")
    }

    var activeStart by remember {
        mutableStateOf("")
    }

    var activeEnd by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Set Lottery Period",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Choose when entrants can register, when the lottery draw takes place, and the camping dates covered."
        )

        HorizontalDivider()

        Text(
            text = "Registration Period",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Entrants can join the lottery during this period."
        )

        OutlinedTextField(
            value = registrationStart,
            onValueChange = {
                registrationStart = it
                errorMessage = ""
            },
            label = {
                Text("Registration Start (yyyy-MM-dd)")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = registrationEnd,
            onValueChange = {
                registrationEnd = it
                errorMessage = ""
            },
            label = {
                Text("Registration End (yyyy-MM-dd)")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        HorizontalDivider()

        Text(
            text = "Lottery Draw",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Select the date when winners will be drawn."
        )

        OutlinedTextField(
            value = drawDate,
            onValueChange = {
                drawDate = it
                errorMessage = ""
            },
            label = {
                Text("Draw Date (yyyy-MM-dd)")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        HorizontalDivider()

        Text(
            text = "Lottery Active Period",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Select the camping dates covered by this lottery."
        )

        OutlinedTextField(
            value = activeStart,
            onValueChange = {
                activeStart = it
                errorMessage = ""
            },
            label = {
                Text("Active Start (yyyy-MM-dd)")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = activeEnd,
            onValueChange = {
                activeEnd = it
                errorMessage = ""
            },
            label = {
                Text("Active End (yyyy-MM-dd)")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {

                    val period = try {
                        LotteryPeriod(
                            registrationStart =
                                LocalDate.parse(registrationStart),
                            registrationEnd =
                                LocalDate.parse(registrationEnd),
                            drawDate =
                                LocalDate.parse(drawDate),
                            activeStart =
                                LocalDate.parse(activeStart),
                            activeEnd =
                                LocalDate.parse(activeEnd)
                        )
                    } catch (e: DateTimeParseException) {
                        null
                    }

                    when {
                        period == null -> {
                            errorMessage =
                                "Enter all dates in yyyy-MM-dd format."
                        }

                        period.registrationStart
                            .isBefore(LocalDate.now()) -> {
                            errorMessage =
                                "Registration cannot start in the past."
                        }

                        period.registrationEnd.isBefore(
                            period.registrationStart
                        ) -> {
                            errorMessage =
                                "Registration end must be on or after registration start."
                        }

                        !period.drawDate.isAfter(
                            period.registrationEnd
                        ) -> {
                            errorMessage =
                                "The draw must happen after registration closes."
                        }

                        !period.activeStart.isAfter(
                            period.drawDate
                        ) -> {
                            errorMessage =
                                "The camping period must start after the lottery draw."
                        }

                        period.activeEnd.isBefore(
                            period.activeStart
                        ) -> {
                            errorMessage =
                                "Active end must be on or after active start."
                        }

                        else -> {
                            errorMessage = ""
                            onConfirm(period)
                        }
                    }
                }
            ) {
                Text("Confirm")
            }
        }
    }
}