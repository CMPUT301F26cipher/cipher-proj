package com.example.cipherappproject.ui.conversation

import androidx.compose.runtime.Composable
import com.example.cipherappproject.database.models.Conversation

@Composable
fun ConversationScreen(
    currentUserId: String,
    conversations: List<Conversation>,
    onConversationClick: (String) -> Unit,
    onNewMessageClick: () -> Unit
){

}