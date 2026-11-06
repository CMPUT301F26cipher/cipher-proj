package com.example.cipherappproject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
2026-11-05  Hye Sung   1.01     Modifying to fit DB variables
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

@Composable
fun WaitlistView(
    waitlist: WaitlistModel,
    joined: Boolean,
    clickJoin: () -> Unit,
    clickLeave: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = waitlist.campsiteName)

        Text(text = "Participants: ${waitlist.participants.size}")

        Text(text = "Lottery Status: ${waitlist.lotteryStatus}")

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = if (!joined) {clickJoin} else clickLeave
        ) {
            Text(text = if (!joined) "Join Waitlist" else "Leave Waitlist")
        }
    }
}

