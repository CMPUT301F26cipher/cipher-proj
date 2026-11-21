package com.example.cipherappproject.database.models

import android.util.Log
import com.google.firebase.installations.FirebaseInstallations

class Device {

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