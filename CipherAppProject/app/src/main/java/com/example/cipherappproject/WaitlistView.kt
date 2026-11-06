package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.LotteryEntry

/*
--------------------------------------------------------------------------------------
Program Name:
Date Written:
Author:
Purpose:
US: 02.09.01
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Initial Implementation
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

@Composable
fun WaitlistView(
    lotteryId: String,
    onBackClick: () -> Unit = {}
) {
    val controller = remember { WaitlistController() }

    var waitlist by remember(lotteryId) {
        mutableStateOf<List<LotteryEntry>>(emptyList())
    }

    var isLoading by remember(lotteryId) {
        mutableStateOf(true)
    }

    var hasError by remember(lotteryId) {
        mutableStateOf(false)
    }

    // Retrieve entrants when the lottery changes
    LaunchedEffect(lotteryId) {

        isLoading = true
        hasError = false

        controller.getWaitlist(lotteryId) { entries ->

            if (entries != null) {
                waitlist = entries
            } else {
                hasError = true
            }

            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(onClick = onBackClick) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lottery Waitlist",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Entrants registered before the lottery draw",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            hasError -> {
                Text("Failed to retrieve the waitlist.")
            }

            waitlist.isEmpty() -> {
                Text("No entrants have joined this lottery yet.")
            }

            else -> {

                Text(
                    text = "Total Entrants: ${waitlist.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = waitlist,
                        key = { it.lotteryEntryId }
                    ) { entry ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = "Entrant: ${entry.userId}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Status: ${entry.status}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

