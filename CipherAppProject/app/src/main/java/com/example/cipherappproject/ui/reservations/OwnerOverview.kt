package com.example.cipherappproject.ui.reservations

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository

/*
--------------------------------------------------------------------------------------
Program Name:
Date Written:
Author:
Purpose:
US: 02.01.04
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Removed dummy list, added DB integration
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

@Composable
fun OwnerOverview(
    ownerId: String,
    onRegisterClick: () -> Unit = {},
    onCampsiteClick: (Campsite) -> Unit = {}
) {

    val repository = remember { CampsiteRepository() }

    val campsites = repository.campsites.filter {
        it.ownerId == ownerId
    }

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
                        Text(text = campsite.campsiteName)
                    }
                }
            }
        }

        // Display the list of campsites
        }

 }


