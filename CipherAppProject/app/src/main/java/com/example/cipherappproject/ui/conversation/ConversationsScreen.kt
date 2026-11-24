package com.example.cipherappproject.ui.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.Conversation
import com.example.cipherappproject.ui.conversation.preview.MessagingPreviewData
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ConversationsScreen(
    currentUserId: String,
    userConversations: List<Conversation>,
    onConversationClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

    ) {
        Text("All Conversations")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = userConversations,
                key = { conversation -> conversation.conversationId }
            ) { conversation ->
                ConversationRow(
                    conversation = conversation,
                    onClick = {
                        onConversationClick(conversation.conversationId)
                    }
                )
            }
        }
    }
}

@Composable
fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color.Black),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top

    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(conversation.campsiteId)
            Text(conversation.lastMessage)
        }

        val date = conversation.createdAt.toDate()
        val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        Text(
            text = formattedDate,

        )


    }
}


/*
@Composable
fun DisplayImage(pathImage: Int, imageDesc: String){ /*ChoiceImage(R.drawable.no, "no")*/
    Image(
        painter = painterResource(pathImage),
        contentDescription = imageDesc
    )
}
ImageStorageRepository
    ├── uploadImage()
    ├── getImageUrl()
    └── deleteImage()

 */
