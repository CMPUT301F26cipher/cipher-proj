package com.example.cipherappproject.ui.conversation.preview

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.cipherappproject.ui.conversation.ConversationScreen
import com.example.cipherappproject.ui.conversation.ConversationsScreen
import com.example.cipherappproject.ui.theme.CipherAppProjectTheme

class MessagingPreviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CipherAppProjectTheme {
                ConversationsScreen(
                    currentUserId = MessagingPreviewData.currentUserId,
                    userConversations = MessagingPreviewData.conversations,
                    onConversationClick = { conversationId  ->
                        Log.d("MessageTest", "Conversation Id: $conversationId")
                    },
                )
            }
        }
    }
}