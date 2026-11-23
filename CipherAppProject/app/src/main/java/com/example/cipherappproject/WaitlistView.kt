package com.example.cipherappproject.waitlist

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.WaitlistController
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
2026-11-06  Hye Sung   1.02     Update after implementing WaitlistController
2026-11-21  Nethmini   1.03     Live list of everyone who joined, readable statuses
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

    // live list: updates as entrants join or leave, and shows draw results once drawn
    val loaded = controller.isEntriesLoaded()
    val waitlist = controller.getJoinedEntrants(lotteryId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("waitlistView")
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
            text = "Entrants who joined this lottery's waiting list",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            !loaded -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            waitlist.isEmpty() -> {
                Text("No entrants have joined this lottery yet.")
            }

            else -> {

                Text(
                    text = "Total Entrants: ${waitlist.size}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("waitlistTotal")
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
                                    text = "Entrant: ${RepositoryDatabase.profileRepository.getProfileFromListById(entry.userId)?.name ?: entry.userId}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Status: ${controller.statusLabel(entry)}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
