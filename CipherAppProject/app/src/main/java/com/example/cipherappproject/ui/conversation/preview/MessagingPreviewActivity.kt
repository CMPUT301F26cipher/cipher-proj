package com.example.cipherappproject.ui.conversation.preview
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.cipherappproject.database.repositories.ImageRepository
import com.example.cipherappproject.ui.conversation.ConversationScreen
import com.example.cipherappproject.ui.conversation.ConversationsScreen
import com.example.cipherappproject.ui.theme.CipherAppProjectTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.models.UploadedImage
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.R
import androidx.core.net.toUri
import coil3.compose.AsyncImage
class MessagingPreviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CipherAppProjectTheme {
                //ImageUploadTestScreen()

                /*

                var selectedConversationId by remember { mutableStateOf("")}

                // https://hugomartins.io/essays/2021/02/scope-functions-as-if-else-statements/
                if (selectedConversationId == ""){
                    ConversationsScreen(
                        currentUserId = MessagingPreviewData.currentUserId,
                        userConversations = MessagingPreviewData.conversations,
                        onConversationClick = { conversationId  ->
                            selectedConversationId = conversationId
                            Log.d("MessageTest", "Conversation Id: $conversationId")
                        },
                    )
                } else {
                    ConversationScreen(
                        currentUserId = MessagingPreviewData.currentUserId,
                        conversationId = selectedConversationId,
                    )



                }

                 */

            }
        }
    }
}
@Composable
fun DisplayImage(pathImage: Int, imageDesc: String){ /*ChoiceImage(R.drawable.no, "no")*/
    Image(
        painter = painterResource(pathImage),
        contentDescription = imageDesc
    )
}

/*Just to test from a local image! use picker but the logic is similar! thank you! */
@Composable
fun ImageUploadTestScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val context = LocalContext.current

        /*initialize the image repostory */
        LaunchedEffect(Unit) {
            RepositoryDatabase.imageRepository.initialize(context)
        }

        var uploadedImage by remember { mutableStateOf<UploadedImage?>(null) }

        var uploadMessage by remember { mutableStateOf("No image uploaded yet") }

        val imageUri =
            "android.resource://${context.packageName}/${R.drawable.default_profile_icon}".toUri()

        Button(
            onClick = {
                RepositoryDatabase.imageRepository.uploadImage(imageUri) { result ->

                    result.onSuccess { uploaded ->
                        uploadedImage = uploaded
                        uploadMessage = "Image uploaded successfully!"
                        Log.d("imageTest", uploadMessage)
                        Log.d("imageTest", "Public ID: ${uploaded.publicId}")
                        Log.d("imageTest", "Image URL: ${uploaded.imageUrl}")
                    }

                    result.onFailure { error ->
                        uploadMessage = "Upload failed: ${error.message}"
                        Log.e("imageTest", uploadMessage)

                    }
                }
            }
        ) {
            Text("Upload Image")
        }

        uploadedImage?.let { image ->
            AsyncImage(
                model = image.imageUrl,
                contentDescription = "Uploaded image",
                modifier = Modifier.size(200.dp),
                onError = { state ->
                    Log.e("imageTest", "Image loading failed", state.result.throwable)
                },
                onSuccess = {
                    Log.d("imageTest", "Image displayed successfully!")
                }
            )
        }
        Text(uploadMessage)
    }
}