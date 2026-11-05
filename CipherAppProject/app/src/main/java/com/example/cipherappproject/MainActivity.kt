package com.example.cipherappproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cipherappproject.ui.theme.CipherAppProjectTheme

/*
 * Built by Bailey
 * Nov 2 2026
 * Updates:
 *
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CipherAppProjectTheme {
                CampsiteApp()
            }
        }
    }
}
