package com.example.cipherappproject

import com.example.cipherappproject.database.repositories.ProfileRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.junit.Test

class DatabaseTest {
    @Test
    fun testAddProfileFirebase() {

        val rep = ProfileRepository()
        val repId = rep.addProfile(
            name = "hello",
            email = "hello@",
            role = listOf("user"),
            imageProfilePath = "path",
            deviceId = ""

        )
        //println(repId)
        // need to connect firebase here but in the web it shows so for now


    }
}