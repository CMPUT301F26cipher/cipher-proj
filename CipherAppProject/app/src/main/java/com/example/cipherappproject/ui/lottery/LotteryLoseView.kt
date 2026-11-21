package com.example.cipherappproject.ui.lottery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Shown to an entrant who was not drawn.
 */
@Composable
fun LotteryLoseView(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Not this time", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "You were not selected in this lottery. If a winner declines, " +
                    "you might still be offered a spot. You can also join other lotteries.",
            textAlign = TextAlign.Center
        )
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
