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
2026-03-22  Sofia      1.02     added query get by id
2026-11-06  Sofia      1.03     delete getters and setters. Set the attributes to public.
2026-11-06  Madison    1.04     added query get by deviceId
2026-11-08  Sofia      1.05     added exceptions and error handling. Commented deviceId for now.
2026-11-08  Sofia      1.07     added queries using the list
2026-11-12  Sofia      1.08     added phone number, state boolean for list local queries.
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Profile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.State



/**
 * Manages profile data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update profile documents
 * directly from Firestore, as well as methods to search and filter
 * from the local profile list.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */
class ProfileRepository {

    private val db = Firebase.firestore
    private val profilesRef = db.collection("profiles")
    private val _allProfiles = mutableStateListOf<Profile>()

    val profiles: List<Profile>
        get() = _allProfiles

    /*Taken and learn from OpenAI, ChatGPT on 2026-11-10
    on "How can I make sure I wait for all the objects be already
    inside the local list? and how it works?"*/
    private val _isProfilesLoaded = mutableStateOf(false)

    val isProfilesLoaded: State<Boolean>
        get() = _isProfilesLoaded


    // ----------------------------------- Queries -----------------------------------

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
     * @param phoneNumber
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */
    fun addProfile(
        name: String,
        email: String,
        role: List<String>,
        imageProfilePath: String,
        deviceId: String,
        status: String,
        phoneNumber: String,
        result: (Result<String>) -> Unit


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
            status = status,
            phoneNumber = phoneNumber
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
                result(Result.success(profileRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }


    }

    /**
     * Updates a Profile object
     *
     * @param profile object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateProfile(
        profile: Profile,
        result: (Result<Unit>) -> Unit
    ){

        //https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
        if (profile.profileId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("profile ID cannot be empty.")
            ))
            return
        }


        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val profileRef = profilesRef.document(profile.profileId)

        // instead of get + set we do one atomic read and write
        db.runTransaction { transaction ->
            val snapshot = transaction.get(profileRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NoSuchElementException("Profile does not exist.")
            }

            transaction.set(profileRef, profile)

            // since we do not care about get an object but just success or not
            Unit

        }
            .addOnSuccessListener {
                Log.d("ProfileTest", "Transaction succeeded")
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileTest", "Transaction failed", exception)
                result(Result.failure(exception))

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
     * @param result lamba function that has Result and if:
     *      - Result.success(Profile) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Profile>
     */
    /*taken and learned from https://firebase.google.com/docs/firestore/query-data/queries*/
    fun getProfileById(
        profileId: String,
        result: (Result<Profile>) -> Unit

    ){

        profilesRef
            .document(profileId)
            .get()
            .addOnSuccessListener { document ->

                // if the document do not exist --> Profile do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Profile does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val profile = document.toObject(Profile::class.java)


                // for testing
                Log.d("ProfileTest", "Raw data: ${document.data}")
                Log.d("ProfileTest", "Mapped object: $profile")


                // if conversion was successful
                if (profile == null) {
                    result(Result.failure(
                        IllegalStateException("Failed to convert document to Profile.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved profile
                result(Result.success(profile))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))

            }
    }



    // ----------------------------------- From list -----------------------------------

    /**
     * Get one single Profile object
     *
     * @param profileId String
     * @return Profile? --> null if not match found
     */
    // https://kotlinlang.org/docs/collection-elements.html#retrieve-with-selector
    fun getProfileFromListById(profileId: String): Profile? {
        return _allProfiles.find { it.profileId == profileId }

    }

    /**
     * Get one single Profile object
     *
     * @param deviceId String
     * @return Profile? --> null if not match found
     */
    fun getProfileFromListByDeviceId(deviceId: String): Profile? {
        return _allProfiles.find { it.deviceId == deviceId }

    }


    /**
     * Get a list of Profile objects
     *
     * @param profileName
     * @return List<Profile> --> can be empty list if not match found
     */

    // https://www.baeldung.com/kotlin/list-filtering
    // https://softwarebits.substack.com/p/case-insensitive-equality-checking
    fun getProfilesFromListByName(profileName: String): List<Profile> {
        return _allProfiles.filter { it.name.contains(profileName, ignoreCase = true) }

    }


    /**
     * Get one single Profile object
     *
     * @param profileEmail String
     * @return Profile? --> null if not match found
     */

    fun getProfileFromListByEmail(profileEmail: String): Profile? {
        return _allProfiles.find { it.email == profileEmail}

    }

    /**
     * Get a Profile object String
     *
     * @param profileRole String
     * @return List<Profile> --> can be empty list if not match found
     */

    // Taken and learn from OpenAI, ChatPGT on 09-11-2026 with the question of "How to check and return an element in list in kotlin? can we use filter on it too?"
    fun getProfilesFromListByRole(profileRole: String): List<Profile> {
        return _allProfiles.filter { profile ->
            // filter add an item that is true
            profile.role.any { role ->
                // any check if the current profile has the role in the role list and then if it does filter add it
                role.equals(profileRole, ignoreCase = true)
            }
        }
    }


    /**
     * Get a list of Profile objects
     *
     * @param profileStatus String
     * @return List<Profile> --> can be empty list if not match found
     */

    fun getProfilesFromListByStatus(profileStatus: String): List<Profile> {
        return _allProfiles.filter { it.status == profileStatus}

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

            _isProfilesLoaded.value = true
        }
    }


}