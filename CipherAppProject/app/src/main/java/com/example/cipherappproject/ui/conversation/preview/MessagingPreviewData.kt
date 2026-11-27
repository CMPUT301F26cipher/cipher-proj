package com.example.cipherappproject.ui.conversation.preview

import com.example.cipherappproject.database.models.Conversation
import com.example.cipherappproject.database.models.Message
import com.example.cipherappproject.database.models.Profile


object MessagingPreviewData {

    const val currentUserId = "entrant123"

    val conversations: List<Conversation> = listOf(

        // Conversation 1
        Conversation(
            conversationId = "conv01",
            campsiteId = "camp01",
            reservationId = "res01",
            ownerId = "owner01",
            userId = currentUserId,
            lastMessage = "Thank you! We look forward to your arrival.",
            status = "OPEN"
        ),

        // Conversation 2
        Conversation(
            conversationId = "conv02",
            campsiteId = "camp02",
            reservationId = "res02",
            ownerId = "owner02",
            userId = currentUserId,
            lastMessage = "Could you please confirm the check-in time?",
            status = "OPEN"
        ),

        // Conversation 3
        Conversation(
            conversationId = "conv03",
            campsiteId = "camp03",
            reservationId = "res03",
            ownerId = "owner03",
            userId = currentUserId,
            lastMessage = "Thank you for letting me know about the cancellation.",
            status = "CLOSED"
        )
    )



    val entrant01 = Profile(
        profileId = currentUserId,
        name = "Name entrant01",
    )

    val owner01 = Profile(
        profileId = "owner01",
        name = "Name owner01",
    )

    val conversation01: List<Message> = listOf(
        Message(
            messageId = "mess01",
            conversationId = "conv01",
            fromUserId = currentUserId,
            message = "Hello",
            status = "SENT"
        ),
        Message(
            messageId = "mess02",
            conversationId = "conv01",
            fromUserId = "owner01",
            message = "Hello, How are you?",
            status = "SENT"
        ),
        Message(
            messageId = "mess03",
            conversationId = "conv01",
            fromUserId = "owner01",
            message = "How can I help you today?",
            status = "SENT"
        )
    )

    val emptyConversations: List<Conversation> = emptyList()
}
