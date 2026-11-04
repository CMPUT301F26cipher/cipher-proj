package com.example.cipherappproject.database.repositories

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProfileRepository {

    private val db = Firebase.firestore
    private val profilesRef = db.collection("profiles")
    private val _profiles = mutableStateListOf<Profile>()

    val profiles: List<Profile>
        get() = _profiles

    fun addProfile(
        name: String,
        email: String,
        role: List<String>,
        imageProfilePath: String,
        deviceId: String

    ) {
        // create a reference and id to the new profile
        val profileRef = profilesRef.document()

        // create profile object
        val profile = Profile(
            profileId = profileRef.id,
            name = name,
            email = email,
            role = role,
            imageProfilePath = imageProfilePath,
            deviceId = deviceId
        )

        // save the object in the firebase
        profileRef.set(profile)
    }

    fun updateProfile(profile: Profile){
        profilesRef.document(profile.profileId).set(profile)
    }


    fun deleteProfile(profileId: String){
        profilesRef.document(profileId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }


    init {
        profilesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _profiles.clear()

            snapshot?.documents?.forEach { document ->
                val profile = document.toObject(Profile::class.java) // pass from firestore to kotlin object

                if (profile != null) {
                    _profiles.add(profile)
                }
            }
        }
    }


}