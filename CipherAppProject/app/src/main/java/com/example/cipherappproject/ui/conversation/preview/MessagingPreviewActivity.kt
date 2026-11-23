package com.example.cipherappproject.ui.conversation.preview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.cipherappproject.ui.theme.CipherAppProjectTheme

class MessagingPreviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CipherAppProjectTheme {
                MessagingPreviewScreen()
            }
        }
    }
}