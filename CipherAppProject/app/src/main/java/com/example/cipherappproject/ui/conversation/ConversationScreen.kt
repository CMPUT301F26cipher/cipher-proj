package com.example.cipherappproject.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Conversation
import com.example.cipherappproject.ui.conversation.preview.MessagingPreviewData

@Composable
fun ConversationScreen(
    currentUserId: String,
    conversationId: String
    /* onBackClick: () -> Unit*/

){

    val messages = MessagingPreviewData.conversation01.filter {
        it.conversationId == conversationId
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),

        horizontalArrangement = Arrangement.spacedBy(8.dp)

    ){
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.LightGray, CircleShape)
                .align(Alignment.CenterVertically),
            contentAlignment = Alignment.Center

        ) {
            Text("C")
        }


    }



}