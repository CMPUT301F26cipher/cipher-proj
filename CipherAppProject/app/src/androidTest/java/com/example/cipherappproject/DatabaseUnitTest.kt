package com.example.cipherappproject

import android.util.Log
import com.example.cipherappproject.database.enums.ProfileRole
import com.example.cipherappproject.database.enums.ProfileStatus
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.CampsiteAvailability
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.example.cipherappproject.database.repositories.CampsiteAvailabilityRepository
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.database.repositories.LotteryRepository
import com.example.cipherappproject.database.repositories.ReservationRepository
import com.example.cipherappproject.database.repositories.LotteryEntryRepository

import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
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
    //private val profileRepo = ProfileRepository()
    private val profileRepo = RepositoryDatabase.profileRepository

    // PASSED
    /*
    @Test
    fun addProfileTest() {

        // Dummy profile data
        val name = "Test Profile 2"
        val email = "testprofile2@example.com"
        val role = listOf(ProfileRole.USER.name)
        val imageProfilePath = "test/profile1.jpg"
        val deviceId = "TEST_DEVICE_001"
        val status = ProfileStatus.ACTIVE.name
        val phoneNumber = "7801234567"
        val notificationsEnabled = true

        // Add profile to Firestore
        val addResult = awaitCallback<Result<String>> { callback ->
            profileRepo.addProfile(
                name = name,
                email = email,
                role = role,
                imageProfilePath = imageProfilePath,
                deviceId = deviceId,
                status = status,
                phoneNumber = phoneNumber,
                notificationsEnabled = notificationsEnabled,
                result = callback
            )
        }

        // Check whether the operation succeeded
        assertTrue("Profile should be added successfully", addResult.isSuccess)

        // Get profile ID
        val profileId = addResult.getOrThrow()

        assertTrue("Profile ID should not be empty", profileId.isNotBlank())

        Log.d("ProfileTest", "Profile added successfully: $profileId")
    }

     */


    // PASSED
    /*
    @Test
    fun getProfileByIdExistsTest() {
        val profileRepo = RepositoryDatabase.profileRepository

        val profileId = "gaNX38oDMm5b3XbCnsKh"

        // Expected profile data
        val name = "Test Profile 1"
        val email = "testprofile1@example.com"
        val role = listOf("owner")
        val imageProfilePath = "test/profile1.jpg"
        val deviceId = "TEST_DEVICE_001"
        val status = "active"
        val phoneNumber = "7801234567"
        val notificationsEnabled = true

        // Retrieve profile
        val getResult = awaitCallback<Result<Profile>> { callback ->
            profileRepo.getProfileById(profileId, callback)
        }

        assertTrue("Profile retrieval should succeed", getResult.isSuccess)

        val actualProfile = getResult.getOrThrow()

        // Compare both objects
        assertEquals(profileId, actualProfile.profileId)
        assertEquals(name, actualProfile.name)
        assertEquals(email, actualProfile.email)
        assertEquals(role, actualProfile.role)
        assertEquals(imageProfilePath, actualProfile.imageProfilePath)
        assertEquals(deviceId, actualProfile.deviceId)
        assertEquals(status, actualProfile.status)
        assertEquals(phoneNumber, actualProfile.phoneNumber)
        assertEquals(notificationsEnabled, actualProfile.notificationsEnabled)

        Log.d("ProfileTest", "Profile retrieved successfully: ${actualProfile.name}")
    }
     */


    // PASSED
    /*
    @Test
    fun getProfileByIdDoesNotExistTest() {
        val profileRepo = RepositoryDatabase.profileRepository

        val profileId = "NON_EXISTENT_PROFILE_123"

        val getResult = awaitCallback<Result<Profile>> { callback ->
            profileRepo.getProfileById(profileId, callback)
        }

        assertTrue("Profile retrieval should fail", getResult.isFailure)

        val exception = getResult.exceptionOrNull()

        assertTrue(
            "Exception should be NotFoundException",
            exception is NotFoundException
        )

        Log.d("ProfileTest", "Profile not found as expected")
    }
     */


    // PASSED
    /*
    @Test
    fun getProfileByIdFoldFailureTest() {
        val profileRepo = RepositoryDatabase.profileRepository

        val profileId = "NON_EXISTENT_PROFILE_123"

        val getResult = awaitCallback<Result<Profile>> { callback ->
            profileRepo.getProfileById(profileId, callback)
        }

        getResult.fold(
            onSuccess = {
                fail("Expected profile retrieval to fail")
            },
            onFailure = { exception ->
                assertTrue(
                    "Exception should be NotFoundException",
                    exception is NotFoundException
                )

                Log.d("ProfileTest", "Fold: Profile not found as expected")
            }
        )
    }

     */


    // PASSED
    /*
    @Test
    fun updateProfileExistsTest() {
        val profileRepo = RepositoryDatabase.profileRepository

        val profileId = "gaNX38oDMm5b3XbCnsKh"

        // Updated profile data
        val name = "Test Profile 1 Updated"
        val email = "testprofile1updated@example.com"
        val role = listOf(ProfileRole.ADMIN.name, ProfileRole.OWNER.name, ProfileRole.USER.name)
        val imageProfilePath = "test/profile1_updated.jpg"
        val deviceId = "TEST_DEVICE_001"
        val status = ProfileStatus.ACTIVE.name
        val phoneNumber = "7809876543"
        val notificationsEnabled = false

        val updatedProfile = Profile(
            profileId = profileId,
            name = name,
            email = email,
            role = role,
            imageProfilePath = imageProfilePath,
            deviceId = deviceId,
            status = status,
            phoneNumber = phoneNumber,
            notificationsEnabled = notificationsEnabled
        )

        // Update profile
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            profileRepo.updateProfile(updatedProfile, callback)
        }

        assertTrue("Profile update should succeed", updateResult.isSuccess)

        // Retrieve updated profile
        val getResult = awaitCallback<Result<Profile>> { callback ->
            profileRepo.getProfileById(profileId, callback)
        }

        assertTrue("Profile retrieval should succeed", getResult.isSuccess)

        val actualProfile = getResult.getOrThrow()

        // Compare updated profile data
        assertEquals(profileId, actualProfile.profileId)
        assertEquals(name, actualProfile.name)
        assertEquals(email, actualProfile.email)
        assertEquals(role, actualProfile.role)
        assertEquals(imageProfilePath, actualProfile.imageProfilePath)
        assertEquals(deviceId, actualProfile.deviceId)
        assertEquals(status, actualProfile.status)
        assertEquals(phoneNumber, actualProfile.phoneNumber)
        assertEquals(notificationsEnabled, actualProfile.notificationsEnabled)

        Log.d("ProfileTest", "Profile updated successfully: ${actualProfile.name}")
    }

     */


    // PASSED
    /*
    @Test
    fun updateProfileDoesNotExistTest() {
        val profileRepo = RepositoryDatabase.profileRepository

        val profileId = "NON_EXISTENT_PROFILE_123"

        val updatedProfile = Profile(
            profileId = profileId,
            name = "Test Profile",
            email = "testprofile@example.com",
            role = listOf(ProfileRole.OWNER.name),
            imageProfilePath = "test/profile.jpg",
            deviceId = "TEST_DEVICE_001",
            status = ProfileStatus.ACTIVE.name,
            phoneNumber = "7801234567",
            notificationsEnabled = true
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            profileRepo.updateProfile(updatedProfile, callback)
        }

        assertTrue("Profile update should fail", updateResult.isFailure)

        val exception = updateResult.exceptionOrNull()

        assertTrue(
            "Exception should be NotFoundException",
            exception is NotFoundException
        )

        Log.d("ProfileTest", "Profile not found as expected")
    }

     */

    // PASSED
    /*
    @Test
    fun updateProfileDoesNotExistOnFailureTest() {
        val profileRepo = RepositoryDatabase.profileRepository

        val profileId = "NON_EXISTENT_PROFILE_123"

        val updatedProfile = Profile(
            profileId = profileId,
            name = "Test Profile",
            email = "testprofile@example.com",
            role = listOf(ProfileRole.OWNER.name),
            imageProfilePath = "test/profile.jpg",
            deviceId = "TEST_DEVICE_001",
            status = ProfileStatus.ACTIVE.name,
            phoneNumber = "7801234567",
            notificationsEnabled = true
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            profileRepo.updateProfile(updatedProfile, callback)
        }

        assertTrue("Profile update should fail", updateResult.isFailure)

        updateResult.onFailure { exception ->
            when (exception) {
                is ValidationException -> {
                    fail("Unexpected ValidationException: ${exception.message}")
                }

                is NotFoundException -> {
                    Log.d("ProfileTest", "Profile does not exist as expected")
                }

                else -> {
                    fail("Unexpected exception: ${exception.message}")
                }
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun getProfileFromListByIdTest() {
        val profileId = "gaNX38oDMm5b3XbCnsKh"

        // Wait until profiles are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!profileRepo.isProfilesLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Profiles failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve profile from local list
        val profile = profileRepo.getProfileFromListById(profileId)

        assertNotNull("Profile should exist in the local list", profile)

        assertEquals(profileId, profile?.profileId)

        Log.d("ProfileTest", "Profile retrieved from local list: $profile")
    }

     */


    // PASSED
    /*
    @Test
    fun getProfileFromListByIdDoesNotExistTest() {
        val profileId = "NON_EXISTENT_PROFILE_123"

        // Wait until profiles are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!profileRepo.isProfilesLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Profiles failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve profile from local list
        val profile = profileRepo.getProfileFromListById(profileId)

        assertNull("Profile should not exist in the local list", profile)

        Log.d("ProfileTest", "Profile not found in local list as expected")
    }

     */


    @Test
    fun getProfileFromListByEmailTest() {
        val profileEmail = "testprofile1@example.com"

        // Wait until profiles are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!profileRepo.isProfilesLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Profiles failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve profile from local list
        val profile = profileRepo.getProfileFromListByEmail(profileEmail)

        assertNotNull("Profile should exist in the local list", profile)

        assertEquals(profileEmail, profile?.email)

        Log.d("ProfileTest", "Profile retrieved from local list: $profile")
    }


    @Test
    fun getProfileFromListByEmailDoesNotExistTest() {
        val profileEmail = "nonexistentprofile@example.com"

        // Wait until profiles are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!profileRepo.isProfilesLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Profiles failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve profile from local list
        val profile = profileRepo.getProfileFromListByEmail(profileEmail)

        assertNull("Profile should not exist in the local list", profile)

        Log.d("ProfileTest", "Profile not found in local list as expected")
    }

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

    // PASSED
    /*
    @Test
    fun createCampsiteAvailabilityTest() {

        val repository = CampsiteAvailabilityRepository()

        // Create dummy availability data
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val startDate = "2026-12-01"
        val endDate = "2026-12-15"
        val status = "active"

        // Create CampsiteAvailability inside Firestore
        val createResult = awaitCallback<Result<String>> { callback ->
            repository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = startDate,
                endDate = endDate,
                status = status,
                result = callback
            )
        }

        // Extract availability ID or throw the Firebase exception
        val campsiteAvailabilityId = createResult.getOrThrow()

        // Create expected CampsiteAvailability object
        val expectedAvailability = CampsiteAvailability(
            campsiteAvailabilityId = campsiteAvailabilityId,
            campsiteId = campsiteId,
            startDate = startDate,
            endDate = endDate,
            status = status
        )

        // Retrieve the newly created availability from Firestore
        val getResult = awaitCallback<Result<CampsiteAvailability>> { callback ->
            repository.getCampsiteAvailabilityById(
                campsiteAvailabilityId = campsiteAvailabilityId,
                campsiteId = campsiteId,
                result = callback
            )
        }

        // Verify retrieval was successful
        assertTrue(
            "Campsite availability retrieval should succeed",
            getResult.isSuccess
        )

        val actualAvailability = getResult.getOrThrow()

        // Verify retrieved availability matches expected availability
        assertEquals(expectedAvailability, actualAvailability)

        Log.d(
            "CampsiteAvailabilityTest",
            "Created and retrieved availability: $campsiteAvailabilityId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun createCampsiteAvailabilityParentDoesNotExistTest() {

        val repository = CampsiteAvailabilityRepository()

        // Create dummy availability data
        val campsiteId = "nonexistentCampsiteId"
        val startDate = "2026-12-01"
        val endDate = "2026-12-15"
        val status = "active"

        val createResult = awaitCallback<Result<String>> { callback ->
            repository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = startDate,
                endDate = endDate,
                status = status,
                result = callback
            )
        }

        assertTrue(
            "Creating availability with nonexistent parent campsite should fail",
            createResult.isFailure
        )

        assertTrue(
            "Failure should be NoSuchElementException",
            createResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d(
            "CampsiteAvailabilityTest",
            "Creating availability with nonexistent parent campsite failed as expected."
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteAvailabilityExistsTest() {

        val repository = CampsiteAvailabilityRepository()

        // Create dummy availability data
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val campsiteAvailabilityId = "i7K0NzjVTiKbUR9yYOUQ"

        // Create updated CampsiteAvailability object
        val updatedAvailability = CampsiteAvailability(
            campsiteAvailabilityId = campsiteAvailabilityId,
            campsiteId = campsiteId,
            startDate = "2026-12-05",
            endDate = "2026-12-20",
            status = "inactive"
        )

        // Update CampsiteAvailability inside Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateCampsiteAvailability(
                updatedAvailability,
                callback
            )
        }

        assertTrue(
            "Campsite availability update should succeed",
            updateResult.isSuccess
        )

        // Retrieve updated availability from Firestore
        val getResult = awaitCallback<Result<CampsiteAvailability>> { callback ->
            repository.getCampsiteAvailabilityById(
                campsiteAvailabilityId = campsiteAvailabilityId,
                campsiteId = campsiteId,
                result = callback
            )
        }

        val actualAvailability = getResult.getOrThrow()

        assertEquals(updatedAvailability, actualAvailability)

        Log.d(
            "CampsiteAvailabilityTest",
            "Updated availability successfully: $campsiteAvailabilityId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteAvailabilityDoesNotExistTest() {

        val repository = CampsiteAvailabilityRepository()

        // Create dummy availability data
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val campsiteAvailabilityId = "nonexistentAvailabilityId"

        // Create CampsiteAvailability object
        val availability = CampsiteAvailability(
            campsiteAvailabilityId = campsiteAvailabilityId,
            campsiteId = campsiteId,
            startDate = "2026-12-05",
            endDate = "2026-12-20",
            status = "inactive"
        )

        // Update CampsiteAvailability inside Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateCampsiteAvailability(
                availability,
                callback
            )
        }

        assertTrue(
            "Updating nonexistent availability should fail",
            updateResult.isFailure
        )

        assertTrue(
            "Failure should be NoSuchElementException",
            updateResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d(
            "CampsiteAvailabilityTest",
            "Error message: ${updateResult.exceptionOrNull()?.message}"
        )

        Log.d(
            "CampsiteAvailabilityTest",
            "Updating nonexistent availability failed as expected."
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteAvailabilityParentDoesNotExistTest() {

        val repository = CampsiteAvailabilityRepository()

        // Create dummy availability data
        val campsiteId = "nonexistentCampsiteId"
        val campsiteAvailabilityId = "i7K0NzjVTiKbUR9yYOUQ"

        val availability = CampsiteAvailability(
            campsiteAvailabilityId = campsiteAvailabilityId,
            campsiteId = campsiteId,
            startDate = "2026-12-01",
            endDate = "2026-12-15",
            status = "active"
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateCampsiteAvailability(
                availability,
                callback
            )
        }

        assertTrue(
            "Updating availability with nonexistent parent campsite should fail",
            updateResult.isFailure
        )

        assertTrue(
            "Failure should be NoSuchElementException",
            updateResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d(
            "CampsiteAvailabilityTest",
            "Error message: ${updateResult.exceptionOrNull()?.message}"
        )

        Log.d(
            "CampsiteAvailabilityTest",
            "Updating availability with nonexistent parent campsite failed as expected."
        )
    }

     */


    // ----------------------------------- Lottery -----------------------------------

    // PASSED
    /*
    @Test
    fun createLotteryTest() {

        val repository = LotteryRepository()

        // Create dummy lottery data
        val ownerId = "xL16fQPxP3ZCFonFdFGN"
        val campsiteIds = mutableListOf("E3trgc4ByPRblHL8rXkN")
        val numWinners = 2
        val startDate = "2026-12-01"
        val endDate = "2026-12-15"
        val maxParticipants = 20
        val status = "active"
        val createdAt = "2026-11-09"

        // Create Lottery inside Firestore
        val createResult = awaitCallback<Result<String>> { callback ->
            repository.createLottery(
                ownerId = ownerId,
                campsiteIds = campsiteIds,
                numWinners = numWinners,
                startDate = startDate,
                endDate = endDate,
                maxParticipants = maxParticipants,
                status = status,
                createdAt = createdAt,
                result = callback
            )
        }

        assertTrue(
            "Lottery creation should succeed",
            createResult.isSuccess
        )

        val lotteryId = createResult.getOrThrow()

        Log.d(
            "LotteryTest",
            "Created lottery successfully. Lottery ID: $lotteryId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryByIdTest() {

        val repository = LotteryRepository()

        // Create dummy lottery data
        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val ownerId = "xL16fQPxP3ZCFonFdFGN"
        val campsiteIds = mutableListOf("E3trgc4ByPRblHL8rXkN")
        val numWinners = 2
        val startDate = "2026-12-01"
        val endDate = "2026-12-15"
        val maxParticipants = 20
        val status = "active"
        val createdAt = "2026-11-09"

        val expectedLottery = Lottery(
            lotteryId = lotteryId,
            ownerId = ownerId,
            campsiteIds = campsiteIds,
            numWinners = numWinners,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )

        // Retrieve Lottery from Firestore
        val getResult = awaitCallback<Result<Lottery>> { callback ->
            repository.getLotteryById(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue(
            "Lottery retrieval should succeed",
            getResult.isSuccess
        )

        val actualLottery = getResult.getOrThrow()

        assertEquals(expectedLottery, actualLottery)

        Log.d(
            "LotteryTest",
            "Retrieved lottery successfully: $lotteryId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryTest() {

        val repository = LotteryRepository()

        // Create dummy lottery data
        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val ownerId = "xL16fQPxP3ZCFonFdFGN"
        val campsiteIds = mutableListOf("E3trgc4ByPRblHL8rXkN")
        val numWinners = 3
        val startDate = "2026-12-05"
        val endDate = "2026-12-20"
        val maxParticipants = 30
        val status = "active"
        val createdAt = "2026-11-09"

        val updatedLottery = Lottery(
            lotteryId = lotteryId,
            ownerId = ownerId,
            campsiteIds = campsiteIds,
            numWinners = numWinners,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )

        // Update Lottery inside Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateLottery(
                updatedLottery,
                callback
            )
        }

        assertTrue(
            "Lottery update should succeed",
            updateResult.isSuccess
        )

        // Retrieve updated Lottery from Firestore
        val getResult = awaitCallback<Result<Lottery>> { callback ->
            repository.getLotteryById(
                lotteryId = lotteryId,
                result = callback
            )
        }

        val actualLottery = getResult.getOrThrow()

        assertEquals(updatedLottery, actualLottery)

        Log.d(
            "LotteryTest",
            "Updated lottery successfully: $lotteryId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateNonexistentLotteryTest() {

        val repository = LotteryRepository()

        // Create dummy lottery data
        val lotteryId = "nonexistentLotteryId"
        val ownerId = "xL16fQPxP3ZCFonFdFGN"
        val campsiteIds = mutableListOf("E3trgc4ByPRblHL8rXkN")
        val numWinners = 3
        val startDate = "2026-12-05"
        val endDate = "2026-12-20"
        val maxParticipants = 30
        val status = "inactive"
        val createdAt = "2026-11-09"

        val lottery = Lottery(
            lotteryId = lotteryId,
            ownerId = ownerId,
            campsiteIds = campsiteIds,
            numWinners = numWinners,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )

        // Update Lottery inside Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateLottery(
                lottery,
                callback
            )
        }

        assertTrue(
            "Updating nonexistent lottery should fail",
            updateResult.isFailure
        )

        assertTrue(
            "Failure should be NoSuchElementException",
            updateResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d(
            "LotteryTest",
            "Error message: ${updateResult.exceptionOrNull()?.message}"
        )
    }

     */


    // ----------------------------------- LotteryEntry -----------------------------------
    // PASSED
    /*
    @Test
    fun addLotteryEntryTest() {
        val repository = LotteryEntryRepository()

        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val status = "registered"
        val positionLottery = 1

        val addResult = awaitCallback<Result<String>> { callback ->
            repository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = userId,
                status = status,
                positionLottery = positionLottery,
                result = callback
            )
        }

        assertTrue("Adding lottery entry should succeed", addResult.isSuccess)

        val lotteryEntryId = addResult.getOrThrow()

        Log.d("LotteryEntryTest", "Created Lottery Entry ID: $lotteryEntryId")
    }

     */

    // PASSED
    /*
    @Test
    fun addLotteryEntryLotteryDoesNotExistTest() {
        val repository = LotteryEntryRepository()

        val lotteryId = "nonexistentLotteryId"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val status = "registered"
        val positionLottery = 1

        val addResult = awaitCallback<Result<String>> { callback ->
            repository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = userId,
                status = status,
                positionLottery = positionLottery,
                result = callback
            )
        }

        assertTrue("Adding entry to nonexistent lottery should fail", addResult.isFailure)

        assertTrue(
            "Failure should be NoSuchElementException",
            addResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d("LotteryEntryTest", "Error message: ${addResult.exceptionOrNull()?.message}")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryEntriesByLotteryIdTest() {
        val repository = LotteryEntryRepository()

        val lotteryId = "OxwO8rKV36hkptiIoYLK"

        val getResult = awaitCallback<Result<List<LotteryEntry>>> { callback ->
            repository.getLotteryEntriesByLotteryId(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Getting lottery entries should succeed", getResult.isSuccess)

        val lotteryEntries = getResult.getOrThrow()

        assertTrue("Lottery entries list should not be empty", lotteryEntries.isNotEmpty())

        assertTrue(
            "All entries should belong to the specified lottery",
            lotteryEntries.all { it.lotteryId == lotteryId }
        )

        lotteryEntries.forEach { entry ->
            Log.d("LotteryEntryTest", "Entry ID: ${entry.lotteryEntryId}")
            Log.d("LotteryEntryTest", "Lottery ID: ${entry.lotteryId}")
            Log.d("LotteryEntryTest", "User ID: ${entry.userId}")
            Log.d("LotteryEntryTest", "Status: ${entry.status}")
            Log.d("LotteryEntryTest", "Position: ${entry.positionLottery}")
        }

        Log.d("LotteryEntryTest", "Number of entries: ${lotteryEntries.size}")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryTest() {
        val repository = LotteryEntryRepository()

        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val lotteryEntryId = "sM35DERRXyYFTiyaa7Wq"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val status = "winner"
        val positionLottery = 1

        val lotteryEntry = LotteryEntry(
            lotteryEntryId = lotteryEntryId,
            lotteryId = lotteryId,
            userId = userId,
            status = status,
            positionLottery = positionLottery
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateLotteryEntry(
                lotteryEntry = lotteryEntry,
                result = callback
            )
        }

        assertTrue("Updating existing lottery entry should succeed", updateResult.isSuccess)

        Log.d("LotteryEntryTest", "Lottery entry updated successfully.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryDoesNotExistTest() {
        val repository = LotteryEntryRepository()

        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val lotteryEntryId = "nonexistentLotteryEntryId"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val status = "winner"
        val positionLottery = 1

        val lotteryEntry = LotteryEntry(
            lotteryEntryId = lotteryEntryId,
            lotteryId = lotteryId,
            userId = userId,
            status = status,
            positionLottery = positionLottery
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateLotteryEntry(
                lotteryEntry = lotteryEntry,
                result = callback
            )
        }

        assertTrue("Updating nonexistent lottery entry should fail", updateResult.isFailure)

        assertTrue(
            "Failure should be NoSuchElementException",
            updateResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d("LotteryEntryTest", "Error message: ${updateResult.exceptionOrNull()?.message}")
    }

     */

    // ----------------------------------- Reservation -----------------------------------
    // PASSED
    /*
    @Test
    fun createReservationTest() {

        val repository = ReservationRepository()

        // Create dummy reservation data
        val userId = "zhuhRmZdl0UlokS43N2J"
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val status = "active"
        val createdAt = "2026-11-09"

        // Create Reservation inside Firestore
        val createResult = awaitCallback<Result<String>> { callback ->
            repository.createReservation(
                userId = userId,
                campsiteId = campsiteId,
                lotteryId = lotteryId,
                status = status,
                createdAt = createdAt,
                result = callback
            )
        }

        assertTrue(
            "Reservation creation should succeed",
            createResult.isSuccess
        )

        val reservationId = createResult.getOrThrow()

        Log.d(
            "ReservationTest",
            "Created reservation successfully. Reservation ID: $reservationId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun getReservationByIdTest() {

        val repository = ReservationRepository()

        // Create dummy reservation data
        val reservationId = "ZYNjuir5hsOX02BFvbzq"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val status = "active"
        val createdAt = "2026-11-09"

        val expectedReservation = Reservation(
            reservationId = reservationId,
            userId = userId,
            campsiteId = campsiteId,
            lotteryId = lotteryId,
            status = status,
            createdAt = createdAt
        )

        // Retrieve Reservation from Firestore
        val getResult = awaitCallback<Result<Reservation>> { callback ->
            repository.getReservationById(
                reservationId = reservationId,
                result = callback
            )
        }

        assertTrue(
            "Reservation retrieval should succeed",
            getResult.isSuccess
        )

        val actualReservation = getResult.getOrThrow()

        assertEquals(expectedReservation, actualReservation)

        Log.d(
            "ReservationTest",
            "Retrieved reservation successfully: $reservationId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateReservationTest() {

        val repository = ReservationRepository()

        // Create dummy reservation data
        val reservationId = "ZYNjuir5hsOX02BFvbzq"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val status = "inactive"
        val createdAt = "2026-11-09"

        val updatedReservation = Reservation(
            reservationId = reservationId,
            userId = userId,
            campsiteId = campsiteId,
            lotteryId = lotteryId,
            status = status,
            createdAt = createdAt
        )

        // Update Reservation inside Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateReservation(
                updatedReservation,
                callback
            )
        }

        assertTrue(
            "Reservation update should succeed",
            updateResult.isSuccess
        )

        // Retrieve updated Reservation from Firestore
        val getResult = awaitCallback<Result<Reservation>> { callback ->
            repository.getReservationById(
                reservationId = reservationId,
                result = callback
            )
        }

        val actualReservation = getResult.getOrThrow()

        assertEquals(updatedReservation, actualReservation)

        Log.d(
            "ReservationTest",
            "Updated reservation successfully: $reservationId"
        )
    }

     */

    // PASSED
    /*
    @Test
    fun updateNonexistentReservationTest() {

        val repository = ReservationRepository()

        // Create dummy reservation data
        val reservationId = "nonexistentReservationId"
        val userId = "zhuhRmZdl0UlokS43N2J"
        val campsiteId = "E3trgc4ByPRblHL8rXkN"
        val lotteryId = "OxwO8rKV36hkptiIoYLK"
        val status = "inactive"
        val createdAt = "2026-11-09"

        val reservation = Reservation(
            reservationId = reservationId,
            userId = userId,
            campsiteId = campsiteId,
            lotteryId = lotteryId,
            status = status,
            createdAt = createdAt
        )

        // Update Reservation inside Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            repository.updateReservation(
                reservation,
                callback
            )
        }

        assertTrue(
            "Updating nonexistent reservation should fail",
            updateResult.isFailure
        )

        assertTrue(
            "Failure should be NoSuchElementException",
            updateResult.exceptionOrNull() is NoSuchElementException
        )

        Log.d(
            "ReservationTest",
            "Error message: ${updateResult.exceptionOrNull()?.message}"
        )
    }

     */

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