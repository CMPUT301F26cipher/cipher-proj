package com.example.cipherappproject.ui.registration

/*
TODO:
- Use kotlin patterns to validate user input for email and phone number. Try implementing isError from OutlinedTextField?
- Allow user to import profile images, have a default image
- Update UI to match the rest of the app
 */

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Patterns

// Stores entrant registration information
data class EntrantDetails(
    val fullName: String,
    val emailAddress: String,
    val phoneNumber: String
)

@Composable
fun entrantProfileView(
    modifier: Modifier = Modifier,
    onConfirmRegistration: (EntrantDetails) -> Unit = {}
) {

    // Variables to store user input
    var fullName by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        //Cipher top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CIPHER",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //Main screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "ENTRANT REGISTRATION",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Enter Full Name
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Full Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        showError = false
                    },
                    label = {
                        Text("Enter your full name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enter email Address
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email Address",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = {
                        emailAddress = it
                        showError = false
                    },
                    label = {
                        Text("Enter your email address")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enter Phone Number
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Phone Number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        showError = false
                    },
                    label = {
                        Text("Enter your phone number")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Error message
            if (showError) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please enter a valid name, email address, and phone number.",
                    color = Color.Red,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm Registration
            Button(
                onClick = {

                    // Check that the name is not empty
                    val validName = fullName.isNotBlank()

                    // Check that the email is not empty
                    val validEmail = emailAddress.isNotBlank()

                    // Check that the phone number is not empty
                    val validPhone = phoneNumber.isNotBlank()

                    if (validName && validEmail && validPhone) {

                        showError = false

                        // Collect entrant information
                        val entrant = EntrantDetails(
                            fullName = fullName.trim(),
                            emailAddress = emailAddress.trim(),
                            phoneNumber = phoneNumber.trim()
                        )

                        onConfirmRegistration(entrant)

                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = "CONFIRM REGISTRATION",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
