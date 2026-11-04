package com.example.cipherappproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment

@Composable
fun OwnerOverview() {
    val campsites = listOf(
        "Campsite 1",
        "Campsite 2",
        "Campsite 3",
        "Campsite 4",
        "Campsite 5"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Text(text = "App name",
            modifier =  Modifier.padding(vertical = 24.dp)
        )
        Text(text = "Register Campsite",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(text = "Press below to register a campsite",
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Button(onClick = {}) {
            Text("Register")
        }

        Text(text = "________________________________________________")
        Text(text = "My Campsites")

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(campsites) { campsite ->

                Card(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = campsite)
                    }
                }
            }
        }

        // Display the list of campsites
        }

 }

// Have Text that says "My campsites"

// Work on WaitlistView, WaitlistModel

