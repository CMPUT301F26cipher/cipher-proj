package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
Program Name: ProfileRepository.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for ProfileRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-02-24  Sofia      1.01     added skeleton
2026-03-22  Sofia      1.01     added query get by id
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Profile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProfileRepository {

    private val db = Firebase.firestore
    private val profilesRef = db.collection("profiles")
    private val _allProfiles = mutableStateListOf<Profile>()

    val profiles: List<Profile>
        get() = _allProfiles

    /**
     * Creates behinds a Profile object and add it to the firebase database
     * with a random and unique id.
     *
     * @param name
     * @param email
     * @param role
     * @param imageProfilePath
     * @param deviceId
     * @param status
     * @param result lamba function that provide if:
     *      - The object was added successfully then it returns the id of that object
     *      - The object was not added not successfully so it returns null
     * @return nullable string
     */
    fun addProfile(
        name: String,
        email: String,
        role: List<String>,
        imageProfilePath: String,
        deviceId: String,
        status: String,
        result: (String?) -> Unit

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
            deviceId = deviceId,
            status = status
        )

        // save the object in the firebase
        /*This was taken and learn "await" and "Task" from OpenAI,
         ChatGPT with the following prompt:
         "how to make sure a testcase wait for the firebase to add the object and then return id? cuz it fails and idk why" followed by "how this task and await stuff works? can you provide an example please?"
         03-11-2026.*/
        //Tasks.await(profileRef.set(profile))

        profileRef
            .set(profile)
            .addOnSuccessListener {
                result(profileRef.id)
            }
            .addOnFailureListener {
                result(null)
            }


    }

    /**
     * Updates a Profile object
     *
     * @param profile object
     * @param result lamba function that provide if:
     *      - successfully then it returns a boolean as true
     *      - not successfully so it returns a boolean as false
     * @return Boolean
     */
    fun updateProfile(
        profile: Profile,
        result: (Boolean) -> Unit
    ){
        profilesRef.document(profile.getProfileId())
            .set(profile)
            .addOnSuccessListener {
                result(true)
            }
            .addOnFailureListener {
                result(false)
            }
    }

    /*
    fun deleteProfile(profileId: String){
        profilesRef.document(profileId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }*/

    /**
     * get one single Profile object
     *
     * @param profileId
     * @param result lamba function that provide if:
     *      - successfully then it returns a Profile
     *      - not successfully so it returns null
     * @return nullable Profile
     */
    /*taken and learned from https://firebase.google.com/docs/firestore/query-data/queries*/
    fun getProfileById(
        profileId: String,
        result: (Profile?) -> Unit
    ){ // use getProfileById("id"){ profile -> return something so here can be if or something like that
        profilesRef
            .document(profileId)
            .get()
            .addOnSuccessListener { document ->

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val profile = document.toObject(Profile::class.java)

                // to avoid :type use lamba
                result(profile)

            }
            .addOnFailureListener { exception ->
                result(null)
            }
    }



    init {
        profilesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allProfiles.clear()

            snapshot?.documents?.forEach { document ->
                val profile = document.toObject(Profile::class.java) // pass from firestore to kotlin object

                if (profile != null) {
                    _allProfiles.add(profile)
                }
            }
        }
    }


}