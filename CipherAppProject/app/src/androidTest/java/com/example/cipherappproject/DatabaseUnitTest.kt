package com.example.cipherappproject

import android.util.Log
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.example.cipherappproject.database.repositories.ProfileRepository
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class DatabaseUnitTest {

    /*Some parts of this file were adapted from OpenAI and ChatGPT suggestions or responses, or also internet particularly regarding testing and simplifying object creation for quicker test setup. However, the overall ideas, design decisions, and implementation are my own unless otherwise specified.Thank you.*/

    // https://developer.android.com/reference/android/util/Log?_gl=1*1sppqts*_up*MQ..*_ga*MTI0MDY1MDM0Ny4xNzk0MDA4OTMy*_ga_6HH9YJMN9M*czE3OTQwMDg5MzIkbzEkZzAkdDE3OTQwMDg5MzIkajYwJGwwJGg1MTA0NzgzNjY.#d(java.lang.String,%20java.lang.String)
    // ----------------------------------- Profile -----------------------------------
    // PASSED
    /*
    @Test
    fun addProfileTest(){
        val repository = ProfileRepository()

        // Dummy profile data
        val name = "Test Profile 4"
        val email = "testprofile4@example.com"
        val role = listOf("owner")
        val imageProfilePath = "test/profile4.jpg"
        val deviceId = "TEST_DEVICE_004"
        val status = "active"

        // Add profile to Firestore
        val addResult = awaitCallback<Result<String>> { callback ->
            repository.addProfile(
                name = name,
                email = email,
                role = role,
                imageProfilePath = imageProfilePath,
                deviceId = deviceId,
                status = status,
                result = callback
            )
        }

        // Check whether the operation succeeded
        assertTrue("Profile should be added successfully", addResult.isSuccess)

        // get campsite ID or throw the Firebase exception
        val profileId = addResult.getOrThrow()

        assertTrue("Profile ID should not be empty", profileId.isNotBlank())

        Log.d("ProfileTest", "Profile added successfully: $profileId")
    }
     */

    // PASSED
    /*
    @Test
    fun getProfileByIdExistsTest() {
        val repository = ProfileRepository()

        // Dummy profile data
        val profileId = "k2UN9ELUfggZ1GLCfYFH"


        // Dummy profile data
        val name = "Test Profile"
        val email = "testprofile@example.com"
        val role = listOf("admin", "user", "owner")
        val imageProfilePath = "test/profile.jpg"
        val deviceId = "TEST_DEVICE_001"
        val status = "active"

        val expectedProfile = Profile(
            profileId = profileId,
            name = name,
            email = email,
            role = role,
            imageProfilePath = imageProfilePath,
            deviceId = deviceId,
            status = status
        )

        // Retrieve profile
        val getResult = awaitCallback<Result<Profile>> { callback ->
            repository.getProfileById(profileId, callback)
        }

        assertTrue("Profile retrieval should succeed", getResult.isSuccess)

        val actualProfile = getResult.getOrThrow()

        // Compare both objects
        assertEquals(expectedProfile, actualProfile)

        Log.d("ProfileTest", "Profile retrieved, name: ${actualProfile.name}")
        Log.d("ProfileTest", "Profile retrieved successfully")
    }
     */

    // PASSED
    /*
    @Test
    fun getProfileByIdDoesNotExistTest() {
        val repository = ProfileRepository()

        // Use an ID that does not exist in Firestore
        val profileId = "NON_EXISTENT_PROFILE_123"

        // Retrieve profile
        val getResult = awaitCallback<Result<Profile>> { callback ->
            repository.getProfileById(profileId, callback)
        }

        // Check that retrieval failed
        assertTrue("Profile retrieval should fail", getResult.isFailure)

        // Check the exception
        val exception = getResult.exceptionOrNull()

        assertTrue(
            "Expected NoSuchElementException",
            exception is NoSuchElementException
        )

        Log.d("ProfileTest", "Profile does not exist, failure handled successfully")
    }
     */

    // PASSED
    /*
    @Test
    fun getProfileByIdDoesNotExistFoldTest() {
        val repository = ProfileRepository()

        val profileId = "NON_EXISTENT_PROFILE_456"

        val getResult = awaitCallback<Result<Profile>> { callback ->
            repository.getProfileById(profileId, callback)
        }

        getResult.fold(
            onSuccess = { profile ->
                //fail --> makes the test case fail in purpose
                fail("Expected failure, but retrieved: $profile")
            },
            onFailure = { exception ->
                assertTrue(exception is NoSuchElementException)

                Log.d("ProfileTest", "Failure handled using fold()")
            }
        )
    }
     */

    // PASSED
    /*
    @Test
    fun updateProfileExistsTest() {
        val repository = ProfileRepository()

        // Dummy profile data
        val profileId = "k2UN9ELUfggZ1GLCfYFH"

        val name = "UPDATED Test Profile1"
        val email = "testprofile1@example.com"
        val role = listOf("admin", "user", "owner")
        val imageProfilePath = "test/updated_profile1.jpg"
        val deviceId = "TEST_DEVICE_001"
        val status = "active"

        val expectedProfile = Profile(
            profileId = profileId,
            name = name,
            email = email,
            role = role,
            imageProfilePath = imageProfilePath,
            deviceId = deviceId,
            status = status
        )

        // Update profile
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateProfile(expectedProfile, callback)
        }

        assertTrue("Profile update should succeed", updateResult.isSuccess)

        // Retrieve updated profile
        val getResult = awaitCallback<Result<Profile>> { callback ->
            repository.getProfileById(profileId, callback)
        }

        assertTrue("Updated profile should be retrieved", getResult.isSuccess)

        val actualProfile = getResult.getOrThrow()

        // Compare both objects
        assertEquals(expectedProfile, actualProfile)

        Log.d("ProfileTest", "Profile updated, name: ${actualProfile.name}")
        Log.d("ProfileTest", "Profile updated successfully")
    }
     */

    // PASSED
    /*
    @Test
    fun updateProfileDoesNotExistTest() {
        val repository = ProfileRepository()

        val profile = Profile(
            profileId = "NON_EXISTENT_PROFILE_123",
            name = "Test Profile",
            email = "testprofile@example.com",
            role = listOf("user"),
            imageProfilePath = "test/profile.jpg",
            deviceId = "TEST_DEVICE_001",
            status = "active"
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateProfile(profile, callback)
        }

        assertTrue("Profile update should fail", updateResult.isFailure)

        val exception = updateResult.exceptionOrNull()

        assertTrue("Expected NoSuchElementException",exception is NoSuchElementException)

        Log.d("ProfileTest", "Non-existent profile update handled successfully")
    }
     */



    // ----------------------------------- Campsite -----------------------------------
    // PASSED
    /*
    @Test
    fun addCampsiteTest() {

        val repository = CampsiteRepository()

        // Create dummy campsite data
        val ownerId = "xL16fQPxP3ZCFonFdFGN"
        val campsiteName = "CS Campsite"
        val mapLocation = listOf("588.44", "-78.3", "Test name3")
        val description = "Dummy campsite for repository testing"
        val amenities = mutableListOf("Parking", "Firepit")
        val maxGuests = 10
        val pricePerNight = 47.0
        val imagePath = "test3/campsite.jpg"
        val tags = mutableListOf("Nature", "Lake")
        val qrCodeImagePath = "test3/qrcode.png"
        val status = "active"

        // Add campsite to Firestore and wait for the result
        val result = awaitCallback<Result<String>> { callback ->
            repository.addCampsite(
                ownerId = ownerId,
                campsiteName = campsiteName,
                mapLocation = mapLocation,
                description = description,
                amenities = amenities,
                maxGuests = maxGuests,
                pricePerNight = pricePerNight,
                imagePath = imagePath,
                tags = tags,
                qrCodeImagePath = qrCodeImagePath,
                status = status,
                result = callback
            )
        }

        // Extract campsite ID or throw the Firebase exception
        val id = result.getOrThrow()

        Log.d("CampsiteTest", "Created campsite: $id")
    }

     */

    // PASSED
    /*
    @Test
    fun getCampsiteByIdExistsTest() {

        val repository = CampsiteRepository()

        // Create dummy campsite data
        val campsiteId = "nuekWNGlvD5HgiLWV8vq"
        val ownerId = "V6msdUQoc0eJ0nGrglf5"
        val campsiteName = "Edmonton Campsite"
        val mapLocation = listOf("67.564", "-22.3", "Test name")
        val description = "Dummy campsite for repository testing"
        val amenities = mutableListOf("Parking", "Firepit")
        val maxGuests = 10
        val pricePerNight = 47.0
        val imagePath = "test/campsite.jpg"
        val tags = mutableListOf("Nature", "Lake")
        val qrCodeImagePath = "test/qrcode.png"
        val status = ""

        val expectedCampsite = Campsite(
            campsiteId = campsiteId,
            ownerId = ownerId,
            campsiteName = campsiteName,
            mapLocation = mapLocation,
            description = description,
            amenities = amenities,
            maxGuests = maxGuests,
            pricePerNight = pricePerNight,
            imagePath = imagePath,
            tags = tags,
            qrCodeImagePath = qrCodeImagePath,
            status = status
        )

        // Retrieve campsite from Firestore
        val getResult = awaitCallback<Result<Campsite>> { callback ->
            repository.getCampsiteById(campsiteId, callback)
        }

        // Verify retrieval was successful
        assertTrue("Campsite retrieval should succeed", getResult.isSuccess)

        val actualCampsite = getResult.getOrThrow()

        // Verify retrieved campsite matches expected campsite
        assertEquals(expectedCampsite, actualCampsite)

        Log.d("CampsiteTest", "Campsite retrieved: ${actualCampsite.campsiteName}")
    }

     */

    // PASSED
    /*
    @Test
    fun getCampsiteByIdDoesNotExistTest() {

        val repository = CampsiteRepository()

        // Create dummy campsite ID
        val campsiteId = "NON_EXISTENT_CAMPSITE_123"

        // Retrieve campsite from Firestore
        val getResult = awaitCallback<Result<Campsite>> { callback ->
            repository.getCampsiteById(campsiteId, callback)
        }

        // Verify retrieval failed
        assertTrue("Campsite retrieval should fail", getResult.isFailure)

        val exception = getResult.exceptionOrNull()

        // Verify the expected exception
        assertTrue(
            "Expected NoSuchElementException",
            exception is NoSuchElementException
        )

        Log.d("CampsiteTest", "Nonexistent campsite handled successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteExistsTest() {

        val repository = CampsiteRepository()

        // Create dummy campsite data
        val campsiteId = "nuekWNGlvD5HgiLWV8vq"
        val ownerId = "V6msdUQoc0eJ0nGrglf5"
        val campsiteName = "UPDATED Edmonton Campsite"
        val mapLocation = listOf("67.564", "-22.3", "Test name")
        val description = "Updated dummy campsite for repository testing"
        val amenities = mutableListOf("Parking", "Firepit", "Wi-Fi")
        val maxGuests = 15
        val pricePerNight = 55.0
        val imagePath = "test/updated_campsite.jpg"
        val tags = mutableListOf("Nature", "Lake", "Updated")
        val qrCodeImagePath = "test/updated_qrcode.png"
        val status = "active"

        val expectedCampsite = Campsite(
            campsiteId = campsiteId,
            ownerId = ownerId,
            campsiteName = campsiteName,
            mapLocation = mapLocation,
            description = description,
            amenities = amenities,
            maxGuests = maxGuests,
            pricePerNight = pricePerNight,
            imagePath = imagePath,
            tags = tags,
            qrCodeImagePath = qrCodeImagePath,
            status = status
        )

        // Update campsite in Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateCampsite(expectedCampsite, callback)
        }

        // Verify update was successful
        assertTrue("Campsite update should succeed", updateResult.isSuccess)

        // Retrieve updated campsite from Firestore
        val getResult = awaitCallback<Result<Campsite>> { callback ->
            repository.getCampsiteById(campsiteId, callback)
        }

        // Verify updated campsite was retrieved successfully
        assertTrue("Updated campsite should be retrieved", getResult.isSuccess)

        val actualCampsite = getResult.getOrThrow()

        // Verify updated campsite matches expected campsite
        assertEquals(expectedCampsite, actualCampsite)

        Log.d("CampsiteTest", "Campsite updated: ${actualCampsite.campsiteName}")
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteDoesNotExistTest() {

        val repository = CampsiteRepository()

        // Create dummy campsite data
        val campsiteId = "NON_EXISTENT_CAMPSITE_456"
        val ownerId = "dvIxoQNArr16yanGiCLY"
        val campsiteName = "Nonexistent Campsite"
        val mapLocation = listOf("67.564", "-22.3", "Test name")
        val description = "Dummy campsite for testing"
        val amenities = mutableListOf("Parking", "Firepit")
        val maxGuests = 10
        val pricePerNight = 47.0
        val imagePath = "test/campsite.jpg"
        val tags = mutableListOf("Nature", "Lake")
        val qrCodeImagePath = "test/qrcode.png"
        val status = "active"

        val campsite = Campsite(
            campsiteId = campsiteId,
            ownerId = ownerId,
            campsiteName = campsiteName,
            mapLocation = mapLocation,
            description = description,
            amenities = amenities,
            maxGuests = maxGuests,
            pricePerNight = pricePerNight,
            imagePath = imagePath,
            tags = tags,
            qrCodeImagePath = qrCodeImagePath,
            status = status
        )

        // Attempt to update nonexistent campsite
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateCampsite(campsite, callback)
        }

        // Verify update failed
        assertTrue("Campsite update should fail", updateResult.isFailure)

        val exception = updateResult.exceptionOrNull()

        // Verify the expected exception
        assertTrue(
            "Expected NoSuchElementException",
            exception is NoSuchElementException
        )

        Log.d("CampsiteTest", "Nonexistent campsite update handled successfully")
    }

     */




    // ----------------------------------- CampsiteAvailability -----------------------------------
    // ----------------------------------- Lottery -----------------------------------
    // ----------------------------------- LotteryEntry -----------------------------------
    // ----------------------------------- Reservation -----------------------------------


    // ----------------------------------- callback for testing -----------------------------------
    /*Taken and learn from OpenAI, ChatGPT on 06-11-2026, with the subject of: "How to make sure the test case wait for
    firebase CRUD operations and if takes too much time exit the test?" */
    // Helper function to wait for Firebase callbacks
    private fun <T> awaitCallback(
        register: ((T) -> Unit) -> Unit
    ): T {

        // start with count 1 and the test wait until it reaches 0
        val latch = CountDownLatch(1)

        // get the object or id of result --> T is a generic type
        val result = AtomicReference<T>()


        // register takes the callback
        register { value ->
            result.set(value) // store whatever  firebase returns
            latch.countDown() // decreases from 1 to 0
        }

        // wait until either: 1. countDown = 0 or 2. pass 15 seconds
        check(latch.await(40, TimeUnit.SECONDS)) {
            "Firebase callback timed out"
        }

        // get and return the value of result
        return result.get()
    }
}