package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.LotteryFormInput
import com.example.cipherappproject.database.models.Campsite

/**
 * The lottery form shared by CreateLotteryView and EditLotteryView.
 *
 * NOTE: dates are typed as yyyy-MM-dd; a Material3 DatePicker could replace this.
 */
@Composable
fun LotteryForm(
    title: String,
    campsites: List<Campsite>,
    input: LotteryFormInput,
    onInputChange: (LotteryFormInput) -> Unit,
    errorMessage: String?,
    saveLabel: String,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)

        // Campsites (one or more)
        Text(text = "Campsites in this lottery", style = MaterialTheme.typography.titleMedium)
        if (campsites.isEmpty()) {
            Text("You have no campsites registered yet.")
        }
        for (campsite in campsites) {
            val isChecked = campsite.campsiteId in input.campsiteIds
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val newIds =
                            if (isChecked) input.campsiteIds - campsite.campsiteId
                            else input.campsiteIds + campsite.campsiteId
                        onInputChange(input.copy(campsiteIds = newIds))
                    }
                    .testTag("campsiteOption_${campsite.campsiteId}")
            ) {
                // The whole row handles the click, so the checkbox itself doesn't need to
                Checkbox(checked = isChecked, onCheckedChange = null)
                Text(text = campsite.campsiteName, modifier = Modifier.padding(start = 8.dp))
            }
        }

        // Registration period
        Text(text = "Registration period", style = MaterialTheme.typography.titleMedium)
        LotteryTextField(
            label = "Opens (yyyy-MM-dd)",
            value = input.registrationStart,
            tag = "registrationStart",
            onValueChange = { onInputChange(input.copy(registrationStart = it)) }
        )
        LotteryTextField(
            label = "Closes / draw day (yyyy-MM-dd)",
            value = input.registrationEnd,
            tag = "registrationEnd",
            onValueChange = { onInputChange(input.copy(registrationEnd = it)) }
        )

        // Winners and capacity
        Text(text = "Draw settings", style = MaterialTheme.typography.titleMedium)
        LotteryTextField(
            label = "Number of winners",
            value = input.numWinners,
            tag = "numWinners",
            isNumber = true,
            onValueChange = { onInputChange(input.copy(numWinners = it)) }
        )
        LotteryTextField(
            label = "Waitlist capacity (empty = unlimited)",
            value = input.capacity,
            tag = "capacity",
            isNumber = true,
            onValueChange = { onInputChange(input.copy(capacity = it)) }
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag("lotteryFormError")
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("saveLotteryButton")
        ) {
            Text(saveLabel)
        }
    }
}

/** One labelled text box in the lottery form. */
@Composable
private fun LotteryTextField(
    label: String,
    value: String,
    tag: String,
    onValueChange: (String) -> Unit,
    isNumber: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
    )
}
