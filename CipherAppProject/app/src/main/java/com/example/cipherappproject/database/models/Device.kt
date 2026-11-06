/**
 * Provides functionality for retrieving device-related information.
 *
 * Uses Firebase Installations to retrieve the unique Firebase Installation ID associated with the current app installation.
 *
 * Outstanding issues:
 * - Returns null if the Firebase Installation ID cannot be retrieved.
 * - No retry mechanism is currently implemented if the request fails.
 */
package com.example.cipherappproject.database.models

import android.util.Log
import com.google.firebase.installations.FirebaseInstallations

class Device {

    /**
     * Retrieves the Firebase Installation ID asynchronously.
     *
     * Once the request completes, the result is passed to the callback.
     *
     * @param result Callback that receives the installation ID as a String,
     * or null if the ID could not be retrieved.
     */
    fun getDeviceId(result: (String?) -> Unit) {

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                result(task.result)
            } else {
                Log.e("Installations", "Unable to get Installation ID")
                result(null)
            }
        }
    }
}

/*
 * AI Documentation Assistance
 *
 * Prompt:
 * "Code Documentation: For each source code file, you should have a brief
 * introductory comment describing its purpose or role within the application
 * or a design pattern, as well as any currently outstanding issues. Provide
 * KDoc interface documentation for your model classes and their public
 * methods (at least).
 *
 * Add this documentation to my code. Tell me what to put or where, and if
 * it is very clear what it does you can add the comment."
 *
 * AI Response:
 * /**
 * Provides functionality for retrieving device-related information.
 *
 * Uses Firebase Installations to retrieve the unique Firebase Installation ID associated with the current app installation.
 *
 * Outstanding issues:
 * - Returns null if the Firebase Installation ID cannot be retrieved.
 * - No retry mechanism is currently implemented if the request fails.
 */
package com.example.cipherappproject.database.models

import android.util.Log
import com.google.firebase.installations.FirebaseInstallations

class Device {

    /**
     * Retrieves the Firebase Installation ID asynchronously.
     *
     * Once the request completes, the result is passed to the callback.
     *
     * @param result Callback that receives the installation ID as a String,
     * or null if the ID could not be retrieved.
     */
    fun getDeviceId(result: (String?) -> Unit) {

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                result(task.result)
            } else {
                Log.e("Installations", "Unable to get Installation ID")
                result(null)
            }
        }
    }
}
 *
 * AI Tool: ChatGPT (OpenAI) - Free versiob
 */
