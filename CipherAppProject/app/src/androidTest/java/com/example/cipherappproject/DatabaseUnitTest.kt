package com.example.cipherappproject

import android.util.Log
import com.example.cipherappproject.database.enums.CampsiteAvailabilityStatus
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.ProfileRole
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.enums.MessageStatus
import com.example.cipherappproject.database.enums.NotificationEventType
import com.example.cipherappproject.database.enums.ProfileStatus
import com.example.cipherappproject.database.enums.RatingStatus
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.CampsiteAvailability
import com.example.cipherappproject.database.models.Conversation
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Message
import com.example.cipherappproject.database.models.Notification
import com.example.cipherappproject.database.models.Rating
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
    private val campsiteRepository = RepositoryDatabase.campsiteRepository
    private val campsiteAvailabilityRepository = RepositoryDatabase.campsiteAvailabilityRepository
    private val lotteryRepository = RepositoryDatabase.lotteryRepository
    private val reservationRepository = RepositoryDatabase.reservationRepository
    private val notificationRepository = RepositoryDatabase.notificationRepository
    private val lotteryEntryRepository = RepositoryDatabase.lotteryEntryRepository
    private val ratingRepository = RepositoryDatabase.ratingRepository
    private val conversationRepository = RepositoryDatabase.conversationRepository
    private val messageRepository = RepositoryDatabase.messageRepository

    // PASSED
    /*
    @Test
    fun addProfileTest() {

        // Dummy profile data
        val name = "Test Profile 5"
        val email = "testprofile5@example.com"
        val role = listOf(ProfileRole.ADMIN.name, ProfileRole.OWNER.name, ProfileRole.ENTRANT.name)
        val imageProfilePath = "test/profile5.jpg"
        val deviceId = "TEST_DEVICE_005"
        val status = ProfileStatus.ACTIVE.name
        val phoneNumber = "846314444"
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

    // PASSED
    /*
    @Test
    fun getProfileFromListByEmailTest() {
        val profileEmail = "testprofile1updated@example.com"

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

     */

    // PASSED
    /*
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

     */

    // ----------------------------------- Campsite -----------------------------------

    // PASSED
    /*
    @Test
    fun createCampsiteAvailabilityExistsTest() {
        val campsiteId = "Hh7EEaZY2nD9OiKmdgFE"

        val startDate = "2026-11-22"
        val endDate = "2026-12-30"
        val status = CampsiteAvailabilityStatus.ACTIVE.name

        val createResult = awaitCallback<Result<String>> { callback ->
            campsiteAvailabilityRepository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = startDate,
                endDate = endDate,
                status = status,
                result = callback
            )
        }

        assertTrue("Campsite availability should be created", createResult.isSuccess)

        val campsiteAvailabilityId = createResult.getOrThrow()
        assertTrue("Availability ID should not be blank", campsiteAvailabilityId.isNotBlank())

        // Retrieve the newly created availability
        val getResult = awaitCallback<Result<CampsiteAvailability>> { callback ->
            campsiteAvailabilityRepository.getCampsiteAvailabilityById(
                campsiteAvailabilityId = campsiteAvailabilityId,
                campsiteId = campsiteId,
                result = callback
            )
        }

        assertTrue("Created availability should be retrieved", getResult.isSuccess)

        val actualAvailability = getResult.getOrThrow()

        assertEquals(campsiteAvailabilityId, actualAvailability.campsiteAvailabilityId)
        assertEquals(campsiteId, actualAvailability.campsiteId)
        assertEquals(startDate, actualAvailability.startDate)
        assertEquals(endDate, actualAvailability.endDate)
        assertEquals(status, actualAvailability.status)

        Log.d("CampsiteAvailabilityTest", "Availability created: $campsiteAvailabilityId")
    }

     */

    // PASSED
    /*
    @Test
    fun createCampsiteAvailabilityCampsiteNotFoundTest() {
        val campsiteId = "Missing_campsite_parent"

        val result = awaitCallback<Result<String>> { callback ->
            campsiteAvailabilityRepository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = "2026-12-01",
                endDate = "2026-12-10",
                status = "available",
                result = callback
            )
        }

        assertTrue("Creation should fail when campsite does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("CampsiteAvailabilityTest", "Creation correctly failed for missing campsite.")
    }

     */

    // PASSED
    /*
    @Test
    fun getCampsiteAvailabilityByIdExistsTest() {
        val campsiteId = "1tboZkTDSQ7B1G6AwPPj"
        val startDate = "2027-02-05"
        val endDate = "2027-01-18"
        val status = "available"

        // Create an availability period first
        val createResult = awaitCallback<Result<String>> { callback ->
            campsiteAvailabilityRepository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = startDate,
                endDate = endDate,
                status = status,
                result = callback
            )
        }

        assertTrue("Availability should be created", createResult.isSuccess)

        val campsiteAvailabilityId = createResult.getOrThrow()

        // Retrieve the availability
        val getResult = awaitCallback<Result<CampsiteAvailability>> { callback ->
            campsiteAvailabilityRepository.getCampsiteAvailabilityById(
                campsiteAvailabilityId = campsiteAvailabilityId,
                campsiteId = campsiteId,
                result = callback
            )
        }

        assertTrue("Availability should be retrieved", getResult.isSuccess)

        val actualAvailability = getResult.getOrThrow()

        assertEquals(campsiteAvailabilityId, actualAvailability.campsiteAvailabilityId)
        assertEquals(campsiteId, actualAvailability.campsiteId)
        assertEquals(startDate, actualAvailability.startDate)
        assertEquals(endDate, actualAvailability.endDate)
        assertEquals(status, actualAvailability.status)

        Log.d("CampsiteAvailabilityTest", "Availability retrieved: $campsiteAvailabilityId")
    }

     */

    // PASSED
    /*
    @Test
    fun getAvailabilitiesByCampsiteIdCampsiteNotFoundTest() {
        val campsiteId = "missing-campsite"

        val result = awaitCallback<Result<List<CampsiteAvailability>>> { callback ->
            campsiteAvailabilityRepository.getAvailabilitiesByCampsiteId(
                campsiteId = campsiteId,
                result = callback
            )
        }

        assertTrue("Retrieval should fail when campsite does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("CampsiteAvailabilityTest", "Retrieval correctly failed for missing campsite.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteAvailabilityExistsTest() {
        val campsiteId = "Hh7EEaZY2nD9OiKmdgFE"
        val startDate = "2027-02-05"
        val endDate = "2027-01-18"
        val status = CampsiteAvailabilityStatus.ACTIVE.name

        // First, create an availability period
        val createResult = awaitCallback<Result<String>> { callback ->
            campsiteAvailabilityRepository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = startDate,
                endDate = endDate,
                status = status,
                result = callback
            )
        }

        assertTrue("Availability should be created", createResult.isSuccess)

        val campsiteAvailabilityId = createResult.getOrThrow()

        // Retrieve the original availability
        val getExistingResult = awaitCallback<Result<CampsiteAvailability>> { callback ->
            campsiteAvailabilityRepository.getCampsiteAvailabilityById(
                campsiteAvailabilityId = campsiteAvailabilityId,
                campsiteId = campsiteId,
                result = callback
            )
        }

        assertTrue("Existing availability should be retrieved", getExistingResult.isSuccess)

        val existingAvailability = getExistingResult.getOrThrow()

        // Create the updated object
        val updatedAvailability = existingAvailability.copy(
            startDate = startDate,
            endDate = endDate,
            status = CampsiteAvailabilityStatus.INACTIVE.name
        )

        // Update the availability
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            campsiteAvailabilityRepository.updateCampsiteAvailability(
                campsiteAvailability = updatedAvailability,
                result = callback
            )
        }

        assertTrue("Availability should be updated", updateResult.isSuccess)

        // Retrieve the updated availability
        val getUpdatedResult = awaitCallback<Result<CampsiteAvailability>> { callback ->
            campsiteAvailabilityRepository.getCampsiteAvailabilityById(
                campsiteAvailabilityId = campsiteAvailabilityId,
                campsiteId = campsiteId,
                result = callback
            )
        }

        assertTrue("Updated availability should be retrieved", getUpdatedResult.isSuccess)

        val actualAvailability = getUpdatedResult.getOrThrow()

        // Verify the updated fields
        assertEquals(campsiteAvailabilityId, actualAvailability.campsiteAvailabilityId)
        assertEquals(campsiteId, actualAvailability.campsiteId)
        assertEquals(updatedAvailability.startDate, actualAvailability.startDate)
        assertEquals(updatedAvailability.endDate, actualAvailability.endDate)
        assertEquals(updatedAvailability.status, actualAvailability.status)

        Log.d("CampsiteAvailabilityTest", "Availability updated: $campsiteAvailabilityId")
    }

     */


    // PASSED
    /*
    @Test
    fun updateCampsiteAvailabilityCampsiteNotFoundTest() {
        val campsiteId = "missing-campsite"

        val availability = CampsiteAvailability(
            campsiteAvailabilityId = "test-availability-id",
            campsiteId = campsiteId,
            startDate = "2026-12-01",
            endDate = "2026-12-10",
            status = "available"
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            campsiteAvailabilityRepository.updateCampsiteAvailability(
                campsiteAvailability = availability,
                result = callback
            )
        }

        assertTrue("Update should fail when campsite does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("CampsiteAvailabilityTest", "Update correctly failed for missing campsite.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteAvailabilityNotFoundTest() {
        val campsiteId = "kEJ1TACs991ICtqhxT1c"

        val availability = CampsiteAvailability(
            campsiteAvailabilityId = "missing-availability",
            campsiteId = campsiteId,
            startDate = "2026-12-01",
            endDate = "2026-12-10",
            status = "available"
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            campsiteAvailabilityRepository.updateCampsiteAvailability(
                campsiteAvailability = availability,
                result = callback
            )
        }

        assertTrue("Update should fail when availability does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("CampsiteAvailabilityTest", "Update correctly failed for missing availability.")
    }

     */


    /*OLD*/
    // PASSED
    /*
    @Test
    fun addCampsiteTest() {
        val ownerId = "kkRuo0Tu8Pfz2EmOtN6d"
        val campsiteName = "301 Campsite"
        val mapLocation = listOf("67.564", "-22.3", "Test name3")
        val description = "Dummy campsite for repository testing"
        val amenities = mutableListOf("Parking", "Firepit")
        val maxGuests = 100
        val pricePerNight = 90.0
        val imagePath = "test/campsite3.jpg"
        val tags = mutableListOf("Nature", "Lake")
        val qrCodeImagePath = "test/qrcode.png"
        val status = "active"
        val rules = mutableListOf("Rule1", "Rule2", "Rule3")
        val policies = mutableListOf("Policies1", "Policies2", "Policies3")


        val addResult = awaitCallback<Result<String>> { callback ->
            campsiteRepository.addCampsite(
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
                rules = rules,
                policies = policies,
                result = callback
            )
        }

        // Check whether the operation succeeded
        assertTrue("Campsite should be added successfully", addResult.isSuccess)

        // Get ID
        val campsiteId = addResult.getOrThrow()

        assertTrue("Campsite ID should not be empty", campsiteId.isNotBlank())

        Log.d("CampsiteTest", "Campsite added successfully: $campsiteId")

        Log.d("CampsiteTest", "Created campsite: ")
    }

     */

    // PASSED
    /*
    @Test
    fun getCampsiteByIdExistsTest() {

        val campsiteId = "Hh7EEaZY2nD9OiKmdgFE"
        val ownerId = "SFt6LcBAoBWTBShnVJ3j"
        val campsiteName = "Edmonton Campsite"
        val mapLocation = listOf("67.564", "-22.3", "Test name")
        val description = "Dummy campsite for repository testing"
        val amenities = mutableListOf("Parking", "Firepit", "Wi-Fi")
        val maxGuests = 15
        val pricePerNight = 55.0
        val imagePath = "test/campsite.jpg"
        val tags = mutableListOf("Nature", "Lake", "Updated")
        val qrCodeImagePath = "test/qrcode.png"
        val status = "active"
        val rules = mutableListOf("Rule1", "Rule2")
        val policies = mutableListOf("Policies1", "Policies2")


        // Retrieve campsite from Firestore
        val getResult = awaitCallback<Result<Campsite>> { callback ->
            campsiteRepository.getCampsiteById(campsiteId, callback)
        }

        // Verify retrieval was successful
        assertTrue("Campsite retrieval should succeed", getResult.isSuccess)

        val actualCampsite = getResult.getOrThrow()

        // Verify retrieved campsite matches expected campsite
        //assertEquals(expectedCampsite, actualCampsite)

        // Compare both objects
        assertEquals(ownerId, actualCampsite.ownerId)
        assertEquals(campsiteName, actualCampsite.campsiteName)
        assertEquals(mapLocation, actualCampsite.mapLocation)
        assertEquals(description, actualCampsite.description)
        assertEquals(amenities, actualCampsite.amenities)
        assertEquals(maxGuests, actualCampsite.maxGuests)
        assertEquals(pricePerNight, actualCampsite.pricePerNight)
        assertEquals(imagePath, actualCampsite.imagePath)
        assertEquals(tags, actualCampsite.tags)
        assertEquals(qrCodeImagePath, actualCampsite.qrCodeImagePath)
        assertEquals(status, actualCampsite.status)
        assertEquals(rules, actualCampsite.rules)
        assertEquals(policies, actualCampsite.policies)

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
            campsiteRepository.getCampsiteById(campsiteId, callback)
        }

        // Verify retrieval failed
        assertTrue("Campsite retrieval should fail", getResult.isFailure)

        val exception = getResult.exceptionOrNull()

        // Verify the expected exception
        assertTrue(
            "Expected NotFoundException",
            exception is NotFoundException
        )

        Log.d("CampsiteTest", "Nonexistent campsite handled successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteExistsTest() {

        // Existing campsite ID
        val campsiteId = "Hh7EEaZY2nD9OiKmdgFE"

        // Retrieve existing campsite from Firestore
        val getExistingResult = awaitCallback<Result<Campsite>> { callback ->
            campsiteRepository.getCampsiteById(campsiteId, callback)
        }

        assertTrue("Existing campsite should be retrieved", getExistingResult.isSuccess)

        val existingCampsite = getExistingResult.getOrThrow()

        // Create updated campsite data
        val updatedCampsite = existingCampsite.copy(
            campsiteName = "Edmonton Campsite",
            mapLocation = listOf("67.564", "-22.3", "Test name"),
            description = "Updated dummy campsite for repository testing",
            amenities = mutableListOf("Parking", "Firepit", "Wi-Fi"),
            maxGuests = 15,
            pricePerNight = 55.0,
            imagePath = "test/updated_campsite.jpg",
            tags = mutableListOf("Nature", "Lake", "Updated"),
            qrCodeImagePath = "test/updated_qrcode.png",
            status = "active",
            rules = mutableListOf("No smoking", "Keep the campsite clean"),
            policies = mutableListOf("No refunds after check-in")
        )

        // Update campsite in Firestore
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            campsiteRepository.updateCampsite(updatedCampsite, callback)
        }

        // Verify update was successful
        assertTrue("Campsite update should succeed", updateResult.isSuccess)

        // Retrieve updated campsite from Firestore
        val getResult = awaitCallback<Result<Campsite>> { callback ->
            campsiteRepository.getCampsiteById(campsiteId, callback)
        }

        // Verify updated campsite was retrieved successfully
        assertTrue("Updated campsite should be retrieved", getResult.isSuccess)

        val actualCampsite = getResult.getOrThrow()

        // Verify updated campsite fields
        assertEquals(campsiteId, actualCampsite.campsiteId)
        assertEquals(updatedCampsite.ownerId, actualCampsite.ownerId)
        assertEquals(updatedCampsite.campsiteName, actualCampsite.campsiteName)
        assertEquals(updatedCampsite.mapLocation, actualCampsite.mapLocation)
        assertEquals(updatedCampsite.description, actualCampsite.description)
        assertEquals(updatedCampsite.amenities, actualCampsite.amenities)
        assertEquals(updatedCampsite.maxGuests, actualCampsite.maxGuests)
        assertEquals(updatedCampsite.pricePerNight, actualCampsite.pricePerNight, 0.0)
        assertEquals(updatedCampsite.imagePath, actualCampsite.imagePath)
        assertEquals(updatedCampsite.tags, actualCampsite.tags)
        assertEquals(updatedCampsite.qrCodeImagePath, actualCampsite.qrCodeImagePath)
        assertEquals(updatedCampsite.status, actualCampsite.status)
        assertEquals(updatedCampsite.rules, actualCampsite.rules)
        assertEquals(updatedCampsite.policies, actualCampsite.policies)

        // Verify original creation timestamp was preserved
        assertEquals(existingCampsite.createdAt, actualCampsite.createdAt)

        Log.d("CampsiteTest", "Campsite updated: ${actualCampsite.campsiteName}")
    }

     */

    // PASSED
    /*
    @Test
    fun updateCampsiteDoesNotExistTest() {

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
            campsiteRepository.updateCampsite(campsite, callback)
        }

        // Verify update failed
        assertTrue("Campsite update should fail", updateResult.isFailure)

        val exception = updateResult.exceptionOrNull()

        // Verify the expected exception
        assertTrue(
            "Expected NotFoundException",
            exception is NotFoundException
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
    fun createLotteryExistsTest() {
        val ownerId = "kkRuo0Tu8Pfz2EmOtN6d"
        val campsiteIds = mutableListOf("kEJ1TACs991ICtqhxT1c", "1tboZkTDSQ7B1G6AwPPj")
        val numWinners = 2
        val startDate = "2026-12-01"
        val endDate = "2026-12-10"
        val maxParticipants = 40
        val status = LotteryStatus.OPEN.name

        val createResult = awaitCallback<Result<String>> { callback ->
            lotteryRepository.createLottery(
                ownerId = ownerId,
                campsiteIds = campsiteIds,
                numWinners = numWinners,
                startDate = startDate,
                endDate = endDate,
                maxParticipants = maxParticipants,
                status = status,
                result = callback
            )
        }

        assertTrue("Lottery should be created", createResult.isSuccess)

        val lotteryId = createResult.getOrThrow()
        assertTrue("Lottery ID should not be blank", lotteryId.isNotBlank())

        // Retrieve the newly created lottery
        val getResult = awaitCallback<Result<Lottery>> { callback ->
            lotteryRepository.getLotteryById(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Created lottery should be retrieved", getResult.isSuccess)

        val actualLottery = getResult.getOrThrow()

        // Verify the lottery fields
        assertEquals(lotteryId, actualLottery.lotteryId)
        assertEquals(ownerId, actualLottery.ownerId)
        assertEquals(campsiteIds, actualLottery.campsiteIds)
        assertEquals(numWinners, actualLottery.numWinners)
        assertEquals(startDate, actualLottery.startDate)
        assertEquals(endDate, actualLottery.endDate)
        assertEquals(maxParticipants, actualLottery.maxParticipants)
        assertEquals(status, actualLottery.status)

        Log.d("LotteryTest", "Lottery created: ${actualLottery.lotteryId}")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryExistsTest() {
        // First, create a lottery
        val createResult = awaitCallback<Result<String>> { callback ->
            lotteryRepository.createLottery(
                ownerId = "SFt6LcBAoBWTBShnVJ3j",
                campsiteIds = mutableListOf("Hh7EEaZY2nD9OiKmdgFE"),
                numWinners = 2,
                startDate = "2026-12-01",
                endDate = "2026-12-10",
                maxParticipants = 20,
                status = LotteryStatus.OPEN.name,
                result = callback
            )
        }

        assertTrue("Lottery should be created", createResult.isSuccess)

        val lotteryId = createResult.getOrThrow()

        // Retrieve the original lottery
        val getExistingResult = awaitCallback<Result<Lottery>> { callback ->
            lotteryRepository.getLotteryById(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Existing lottery should be retrieved", getExistingResult.isSuccess)

        val existingLottery = getExistingResult.getOrThrow()

        // Create the updated lottery
        val updatedLottery = existingLottery.copy(
            ownerId = "SFt6LcBAoBWTBShnVJ3j",
            campsiteIds = mutableListOf("Hh7EEaZY2nD9OiKmdgFE"),
            numWinners = 2,
            startDate = "2026-12-01",
            endDate = "2026-12-10",
            maxParticipants = 20,
            status = LotteryStatus.PROCESSING.name
        )

        // Update the lottery
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            lotteryRepository.updateLottery(
                lottery = updatedLottery,
                result = callback
            )
        }

        assertTrue("Lottery should be updated", updateResult.isSuccess)

        // Retrieve the updated lottery
        val getUpdatedResult = awaitCallback<Result<Lottery>> { callback ->
            lotteryRepository.getLotteryById(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Updated lottery should be retrieved", getUpdatedResult.isSuccess)

        val actualLottery = getUpdatedResult.getOrThrow()

        // Verify the updated fields
        assertEquals(lotteryId, actualLottery.lotteryId)
        assertEquals(updatedLottery.ownerId, actualLottery.ownerId)
        assertEquals(updatedLottery.campsiteIds, actualLottery.campsiteIds)
        assertEquals(updatedLottery.numWinners, actualLottery.numWinners)
        assertEquals(updatedLottery.startDate, actualLottery.startDate)
        assertEquals(updatedLottery.endDate, actualLottery.endDate)
        assertEquals(updatedLottery.maxParticipants, actualLottery.maxParticipants)
        assertEquals(updatedLottery.status, actualLottery.status)

        // Verify createdAt was preserved
        assertEquals(existingLottery.createdAt, actualLottery.createdAt)

        Log.d("LotteryTest", "Lottery updated: ${actualLottery.lotteryId}")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryByIdNotFoundTest() {
        val lotteryId = "missing-lottery"

        val result = awaitCallback<Result<Lottery>> { callback ->
            lotteryRepository.getLotteryById(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Retrieval should fail when lottery does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("LotteryTest", "Retrieval correctly failed for missing lottery.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryNotFoundTest() {
        val lottery = Lottery(
            lotteryId = "missing-lottery",
            ownerId = "test-owner-id",
            campsiteIds = mutableListOf("test-campsite-1"),
            numWinners = 1,
            startDate = "2026-12-01",
            endDate = "2026-12-10",
            maxParticipants = 20,
            status = LotteryStatus.OPEN.name
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            lotteryRepository.updateLottery(
                lottery = lottery,
                result = callback
            )
        }

        assertTrue("Update should fail when lottery does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("LotteryTest", "Update correctly failed for missing lottery.")
    }

     */

    /*
    @Test
    fun updateLotteryBlankIdTest() {
        val lottery = Lottery(
            lotteryId = "",
            ownerId = "test-owner-id",
            campsiteIds = mutableListOf("test-campsite-1"),
            numWinners = 1,
            startDate = "2026-12-01",
            endDate = "2026-12-10",
            maxParticipants = 20,
            status = LotteryStatus.OPEN.name
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            lotteryRepository.updateLottery(
                lottery = lottery,
                result = callback
            )
        }

        assertTrue("Update should fail when lottery ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected ValidationException but received ${exception::class.simpleName}",
                exception is ValidationException
            )
        }

        Log.d("LotteryTest", "Update correctly failed for blank lottery ID.")
    }

     */

    /*
    @Test
    fun getLotteryByIdBlankIdTest() {
        val result = awaitCallback<Result<Lottery>> { callback ->
            lotteryRepository.getLotteryById(
                lotteryId = "",
                result = callback
            )
        }

        assertTrue("Retrieval should fail when lottery ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected ValidationException but received ${exception::class.simpleName}",
                exception is ValidationException
            )
        }

        Log.d("LotteryTest", "Retrieval correctly failed for blank lottery ID.")
    }

     */


    /*OLD*/
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

    /*
    @Test
    fun addLotteryEntryExistsTest() {
        val lotteryId = "Kwu8NFaL6OxFx2JcCrDT"
        val userId = "kDrTERBuWvd7OIrk9cAu"
        val status = LotteryEntryStatus.REGISTERED.name
        val positionLottery = 1

        val addResult = awaitCallback<Result<String>> { callback ->
            lotteryEntryRepository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = userId,
                status = status,
                positionLottery = positionLottery,
                result = callback
            )
        }

        assertTrue("Lottery entry should be added", addResult.isSuccess)

        val lotteryEntryId = addResult.getOrThrow()
        assertTrue("Lottery entry ID should not be blank", lotteryEntryId.isNotBlank())

        // Retrieve the newly added entry
        val getResult = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = lotteryEntryId,
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Created lottery entry should be retrieved", getResult.isSuccess)

        val actualEntry = getResult.getOrThrow()

        // Verify the entry fields
        assertEquals(lotteryEntryId, actualEntry.lotteryEntryId)
        assertEquals(lotteryId, actualEntry.lotteryId)
        assertEquals(userId, actualEntry.userId)
        assertEquals(status, actualEntry.status)
        assertEquals(positionLottery, actualEntry.positionLottery)

        Log.d("LotteryEntryTest", "Lottery entry added: $lotteryEntryId")
    }

     */

    // PASSED
    /*
    @Test
    fun addLotteryEntryLotteryNotFoundTest() {
        val lotteryId = "missing-lottery"

        val result = awaitCallback<Result<String>> { callback ->
            lotteryEntryRepository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = "test-user-id",
                status = LotteryEntryStatus.REGISTERED.name,
                positionLottery = 1,
                result = callback
            )
        }

        assertTrue("Adding entry should fail when lottery does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("LotteryEntryTest", "Adding entry correctly failed for missing lottery.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryExistsTest() {
        val lotteryId = "6b07buGBHfPLg43AB1ta"

        // First, add an entry
        val addResult = awaitCallback<Result<String>> { callback ->
            lotteryEntryRepository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = "7wdDAeivMcsOye70DCze",
                status = LotteryEntryStatus.REGISTERED.name,
                positionLottery = 1,
                result = callback
            )
        }

        assertTrue("Lottery entry should be added", addResult.isSuccess)

        val lotteryEntryId = addResult.getOrThrow()

        // Retrieve the original entry
        val getExistingResult = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = lotteryEntryId,
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Existing entry should be retrieved", getExistingResult.isSuccess)

        val existingEntry = getExistingResult.getOrThrow()

        // Create the updated entry
        val updatedEntry = existingEntry.copy(
            status = LotteryEntryStatus.WINNER.name,
            positionLottery = 1
        )

        // Update the entry
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            lotteryEntryRepository.updateLotteryEntry(
                lotteryEntry = updatedEntry,
                result = callback
            )
        }

        assertTrue("Lottery entry should be updated", updateResult.isSuccess)

        // Retrieve the updated entry
        val getUpdatedResult = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = lotteryEntryId,
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Updated entry should be retrieved", getUpdatedResult.isSuccess)

        val actualEntry = getUpdatedResult.getOrThrow()

        // Verify the updated fields
        assertEquals(lotteryEntryId, actualEntry.lotteryEntryId)
        assertEquals(lotteryId, actualEntry.lotteryId)
        assertEquals(existingEntry.userId, actualEntry.userId)
        assertEquals(updatedEntry.status, actualEntry.status)
        assertEquals(updatedEntry.positionLottery, actualEntry.positionLottery)

        Log.d("LotteryEntryTest", "Lottery entry updated: $lotteryEntryId")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryBlankEntryIdTest() {
        val entry = LotteryEntry(
            lotteryEntryId = "",
            lotteryId = "6b07buGBHfPLg43AB1ta",
            userId = "test-user-id",
            status = LotteryEntryStatus.REGISTERED.name,
            positionLottery = 1
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            lotteryEntryRepository.updateLotteryEntry(
                lotteryEntry = entry,
                result = callback
            )
        }

        assertTrue("Update should fail when entry ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected ValidationException but received ${exception::class.simpleName}",
                exception is ValidationException
            )
        }

        Log.d("LotteryEntryTest", "Update correctly failed for blank entry ID.")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryEntriesByLotteryIdBlankIdTest() {
        val result = awaitCallback<Result<List<LotteryEntry>>> { callback ->
            lotteryEntryRepository.getLotteryEntriesByLotteryId(
                lotteryId = "",
                result = callback
            )
        }

        assertTrue("Retrieval should fail when lottery ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected IllegalArgumentException but received ${exception::class.simpleName}",
                exception is IllegalArgumentException
            )
        }

        Log.d("LotteryEntryTest", "Retrieval correctly failed for blank lottery ID.")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryEntriesByLotteryIdLotteryNotFoundTest() {
        val lotteryId = "missing-lottery"

        val result = awaitCallback<Result<List<LotteryEntry>>> { callback ->
            lotteryEntryRepository.getLotteryEntriesByLotteryId(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Retrieval should fail when lottery does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NoSuchElementException but received ${exception::class.simpleName}",
                exception is NoSuchElementException
            )
        }

        Log.d("LotteryEntryTest", "Retrieval correctly failed for missing lottery.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryLotteryNotFoundTest() {
        val lotteryId = "missing-lottery"

        val entry = LotteryEntry(
            lotteryEntryId = "test-entry-id",
            lotteryId = lotteryId,
            userId = "test-user-id",
            status = LotteryEntryStatus.REGISTERED.name,
            positionLottery = 1
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            lotteryEntryRepository.updateLotteryEntry(
                lotteryEntry = entry,
                result = callback
            )
        }

        assertTrue("Update should fail when lottery does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("LotteryEntryTest", "Update correctly failed for missing lottery.")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryEntryByIdBlankLotteryIdTest() {
        val result = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = "test-entry-id",
                lotteryId = "",
                result = callback
            )
        }

        assertTrue("Retrieval should fail when lottery ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected ValidationException but received ${exception::class.simpleName}",
                exception is ValidationException
            )
        }

        Log.d("LotteryEntryTest", "Retrieval correctly failed for blank lottery ID.")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryEntryByIdNotFoundTest() {
        val lotteryEntryId = "missing-entry"

        val result = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = lotteryEntryId,
                lotteryId = "6b07buGBHfPLg43AB1ta",
                result = callback
            )
        }

        assertTrue("Retrieval should fail when entry does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("LotteryEntryTest", "Retrieval correctly failed for missing entry.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryBlankLotteryIdTest() {
        val entry = LotteryEntry(
            lotteryEntryId = "test-entry-id",
            lotteryId = "",
            userId = "test-user-id",
            status = LotteryEntryStatus.REGISTERED.name,
            positionLottery = 1
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            lotteryEntryRepository.updateLotteryEntry(
                lotteryEntry = entry,
                result = callback
            )
        }

        assertTrue("Update should fail when lottery ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected ValidationException but received ${exception::class.simpleName}",
                exception is ValidationException
            )
        }

        Log.d("LotteryEntryTest", "Update correctly failed for blank lottery ID.")
    }

     */

    // PASSED
    /*
    @Test
    fun updateLotteryEntryNotFoundTest() {
        val entry = LotteryEntry(
            lotteryEntryId = "missing-entry",
            lotteryId = "6b07buGBHfPLg43AB1ta",
            userId = "test-user-id",
            status = LotteryEntryStatus.REGISTERED.name,
            positionLottery = 1
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            lotteryEntryRepository.updateLotteryEntry(
                lotteryEntry = entry,
                result = callback
            )
        }

        assertTrue("Update should fail when entry does not exist", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected NotFoundException but received ${exception::class.simpleName}",
                exception is NotFoundException
            )
        }

        Log.d("LotteryEntryTest", "Update correctly failed for missing entry.")
    }

     */

    // PASSED
    /*
    @Test
    fun getLotteryEntryByIdExistsTest() {
        val lotteryId = testLotteryId

        // Add an entry first
        val addResult = awaitCallback<Result<String>> { callback ->
            lotteryEntryRepository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = "test-user-id",
                status = LotteryEntryStatus.REGISTERED.name,
                positionLottery = 1,
                result = callback
            )
        }

        assertTrue("Lottery entry should be added", addResult.isSuccess)

        val lotteryEntryId = addResult.getOrThrow()

        // Retrieve the entry
        val getResult = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = lotteryEntryId,
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Lottery entry should be retrieved", getResult.isSuccess)

        val actualEntry = getResult.getOrThrow()

        // Verify the entry fields
        assertEquals(lotteryEntryId, actualEntry.lotteryEntryId)
        assertEquals(lotteryId, actualEntry.lotteryId)
        assertEquals("test-user-id", actualEntry.userId)
        assertEquals(LotteryEntryStatus.REGISTERED.name, actualEntry.status)
        assertEquals(1, actualEntry.positionLottery)

        Log.d("LotteryEntryTest", "Lottery entry retrieved: $lotteryEntryId")
    }

    @Test
    fun getLotteryEntryByIdBlankEntryIdTest() {
        val result = awaitCallback<Result<LotteryEntry>> { callback ->
            lotteryEntryRepository.getLotteryEntryById(
                lotteryEntryId = "",
                lotteryId = testLotteryId,
                result = callback
            )
        }

        assertTrue("Retrieval should fail when entry ID is blank", result.isFailure)

        result.onFailure { exception ->
            assertTrue(
                "Expected ValidationException but received ${exception::class.simpleName}",
                exception is ValidationException
            )
        }

        Log.d("LotteryEntryTest", "Retrieval correctly failed for blank entry ID.")
    }

     */

    /*
    @Test
    fun getLotteryEntriesByLotteryIdExistsTest() {
        val lotteryId = "Kwu8NFaL6OxFx2JcCrDT"

        // Add the first entry
        val firstAddResult = awaitCallback<Result<String>> { callback ->
            lotteryEntryRepository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = "kDrTERBuWvd7OIrk9cAu",
                status = LotteryEntryStatus.REGISTERED.name,
                positionLottery = 1,
                result = callback
            )
        }

        assertTrue("First entry should be added", firstAddResult.isSuccess)
        val firstEntryId = firstAddResult.getOrThrow()

        // Add the second entry
        val secondAddResult = awaitCallback<Result<String>> { callback ->
            lotteryEntryRepository.addLotteryEntry(
                lotteryId = lotteryId,
                userId = "7wdDAeivMcsOye70DCze",
                status = LotteryEntryStatus.REGISTERED.name,
                positionLottery = 2,
                result = callback
            )
        }

        assertTrue("Second entry should be added", secondAddResult.isSuccess)
        val secondEntryId = secondAddResult.getOrThrow()

        // Retrieve all entries for this lottery
        val getResult = awaitCallback<Result<List<LotteryEntry>>> { callback ->
            lotteryEntryRepository.getLotteryEntriesByLotteryId(
                lotteryId = lotteryId,
                result = callback
            )
        }

        assertTrue("Lottery entries should be retrieved", getResult.isSuccess)

        val actualEntries = getResult.getOrThrow()

        // Find the two entries created by this test
        val firstEntry = actualEntries.find {
            it.lotteryEntryId == firstEntryId
        }

        val secondEntry = actualEntries.find {
            it.lotteryEntryId == secondEntryId
        }

        assertNotNull("First entry should be in the returned list", firstEntry)
        assertNotNull("Second entry should be in the returned list", secondEntry)

        // Verify the first entry
        assertEquals(lotteryId, firstEntry!!.lotteryId)
        assertEquals("kDrTERBuWvd7OIrk9cAu", firstEntry.userId)
        assertEquals(LotteryEntryStatus.REGISTERED.name, firstEntry.status)
        assertEquals(1, firstEntry.positionLottery)

        // Verify the second entry
        assertEquals(lotteryId, secondEntry!!.lotteryId)
        assertEquals("7wdDAeivMcsOye70DCze", secondEntry.userId)
        assertEquals(LotteryEntryStatus.REGISTERED.name, secondEntry.status)
        assertEquals(2, secondEntry.positionLottery)

        Log.d(
            "LotteryEntryTest",
            "Retrieved ${actualEntries.size} entries for lottery $lotteryId"
        )
    }

     */


    /*OLD*/
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
    fun createReservationExistsTest() {

        val userId = "7wdDAeivMcsOye70DCze"
        val campsiteId = "kEJ1TACs991ICtqhxT1c"
        val lotteryId = "test-lottery-id"
        val status = ReservationStatus.PENDING.name
        val bookStartDate = "2026-11-01"
        val bookEndDate = "2026-11-05"
        val pricePerNight = 50.0
        val totalPrice = 200.0

        val createResult = awaitCallback<Result<String>> { callback ->
            reservationRepository.createReservation(
                userId = userId,
                campsiteId = campsiteId,
                lotteryId = lotteryId,
                status = status,
                bookStartDate = bookStartDate,
                bookEndDate = bookEndDate,
                pricePerNight = pricePerNight,
                totalPrice = totalPrice,
                result = callback
            )
        }

        assertTrue("Reservation should be created", createResult.isSuccess)

        val reservationId = createResult.getOrThrow()

        assertTrue("Reservation ID should not be blank", reservationId.isNotBlank())

        // Retrieve the created reservation
        val getResult = awaitCallback<Result<Reservation>> { callback ->
            reservationRepository.getReservationById(
                reservationId = reservationId,
                result = callback
            )
        }

        val reservation = getResult.getOrThrow()

        assertEquals(reservationId, reservation.reservationId)
        assertEquals(userId, reservation.userId)
        assertEquals(campsiteId, reservation.campsiteId)
        assertEquals(lotteryId, reservation.lotteryId)
        assertEquals(status, reservation.status)
        assertEquals(bookStartDate, reservation.bookStartDate)
        assertEquals(bookEndDate, reservation.bookEndDate)
        assertEquals(pricePerNight, reservation.pricePerNight, 0.001)
        assertEquals(totalPrice, reservation.totalPrice, 0.001)

    }

     */

    // PASSED
    /*
    @Test
    fun updateReservationExistsTest() {

        // Create a reservation first
        val createResult = awaitCallback<Result<String>> { callback ->
            reservationRepository.createReservation(
                userId = "kDrTERBuWvd7OIrk9cAu",
                campsiteId = "kEJ1TACs991ICtqhxT1c",
                lotteryId = "test-lottery-id",
                status = ReservationStatus.PENDING.name,
                bookStartDate = "2026-12-01",
                bookEndDate = "2026-12-05",
                pricePerNight = 50.0,
                totalPrice = 200.0,
                result = callback
            )
        }

        val reservationId = createResult.getOrThrow()

        // Retrieve the original reservation
        val getResult = awaitCallback<Result<Reservation>> { callback ->
            reservationRepository.getReservationById(
                reservationId = reservationId,
                result = callback
            )
        }

        val originalReservation = getResult.getOrThrow()

        // Create an updated reservation
        val updatedReservation = originalReservation.copy(
            status = ReservationStatus.ACTIVE.name,
            bookStartDate = "2026-12-02",
            bookEndDate = "2026-12-06",
            pricePerNight = 60.0,
            totalPrice = 240.0
        )

        // Update reservation
        val updateResult = awaitCallback<Result<Unit>> { callback ->
            reservationRepository.updateReservation(
                reservation = updatedReservation,
                result = callback
            )
        }

        assertTrue("Reservation should be updated", updateResult.isSuccess)

        // Retrieve updated reservation
        val updatedResult = awaitCallback<Result<Reservation>> { callback ->
            reservationRepository.getReservationById(
                reservationId = reservationId,
                result = callback
            )
        }

        val reservation = updatedResult.getOrThrow()

        assertEquals(reservationId, reservation.reservationId)
        assertEquals(ReservationStatus.ACTIVE.name, reservation.status)
        assertEquals("2026-12-02", reservation.bookStartDate)
        assertEquals("2026-12-06", reservation.bookEndDate)
        assertEquals(60.0, reservation.pricePerNight, 0.001)
        assertEquals(240.0, reservation.totalPrice, 0.001)

        // Verify createdAt was preserved
        assertEquals(originalReservation.createdAt, reservation.createdAt)

    }

     */

    // PASSED
    /*
    @Test
    fun getReservationByIdBlankIdTest() {

        val getResult = awaitCallback<Result<Reservation>> { callback ->
            reservationRepository.getReservationById(
                reservationId = "",
                result = callback
            )
        }

        assertTrue("Should fail with blank reservation ID", getResult.isFailure)

        getResult.onFailure { exception ->
            assertTrue(exception is ValidationException)
        }

    }

     */

    // PASSED
    /*
    @Test
    fun getReservationByIdNotFoundTest() {

        val missingReservationId =
            "missing-reservation"

        val getResult = awaitCallback<Result<Reservation>> { callback ->
            reservationRepository.getReservationById(
                reservationId = missingReservationId,
                result = callback
            )
        }

        assertTrue("Should fail when reservation does not exist", getResult.isFailure)

        getResult.onFailure { exception ->
            assertTrue(exception is NotFoundException)
        }

    }

     */

    // PASSED
    /*
    @Test
    fun updateReservationNotFoundTest() {

        val reservation = Reservation(
            reservationId = "missing-reservation",
            userId = "test-user",
            campsiteId = "test-campsite",
            lotteryId = "test-lottery",
            status = ReservationStatus.PENDING.name
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            reservationRepository.updateReservation(
                reservation = reservation,
                result = callback
            )
        }

        assertTrue("Should fail when reservation does not exist", updateResult.isFailure)

        updateResult.onFailure { exception ->
            assertTrue(exception is NotFoundException)
        }

    }

     */

    // PASSED
    /*
    @Test
    fun updateReservationBlankIdTest() {

        val reservation = Reservation(
            reservationId = "",
            userId = "test-user",
            campsiteId = "test-campsite",
            lotteryId = "test-lottery",
            status = ReservationStatus.PENDING.name
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            reservationRepository.updateReservation(
                reservation = reservation,
                result = callback
            )
        }

        assertTrue("Should fail with blank reservation ID", updateResult.isFailure)

        updateResult.onFailure { exception ->
            assertTrue(exception is ValidationException)
        }

    }

     */

    /*OLD*/
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


    // ----------------------------------- Notification  -----------------------------------


    // PASSED
    /*
    @Test
    fun addNotificationExistsTest() {

        val eventId = "h3ZnwXUi0n7belnOGacR"
        val eventType = NotificationEventType.RESERVATION.name
        val toUserId = "U6IpXzXFGpEuwvVuiObR"
        val title = "Reservation Confirmed"
        val message = "Your reservation has been confirmed."

        // Create notification
        val notificationId = awaitCallback<Result<String>> { callback ->
            notificationRepository.addNotification(
                eventId = eventId,
                eventType = eventType,
                toUserId = toUserId,
                title = title,
                message = message,
                result = callback
            )
        }.getOrThrow()

        assertTrue(notificationId.isNotBlank())

        // Retrieve created notification
        val notification = awaitCallback<Result<Notification>> { callback ->
            notificationRepository.getNotificationById(
                notificationId,
                callback
            )
        }.getOrThrow()

        // Verify fields
        assertEquals(notificationId, notification.notificationId)
        assertEquals(eventId, notification.eventId)
        assertEquals(eventType, notification.eventType)
        assertEquals(toUserId, notification.toUserId)
        assertEquals(title, notification.title)
        assertEquals(
            message,
            notification.message
        )
        assertEquals(false, notification.isRead)

        Log.d("NotificationTest", "addNotificationExistsTest passed")
    }

     */

    // PASSED
    /*
    @Test
    fun updateNotificationExistsTest() {
        val eventId = "h7ctE62p7flpfXUKQjvV"
        val eventType = NotificationEventType.RESERVATION.name
        val toUserId = "U6IpXzXFGpEuwvVuiObR"
        val title = "Reservation Confirmed"
        val message = "Your reservation has been confirmed."

        // Create notification
        val notificationId = awaitCallback<Result<String>> { callback ->
            notificationRepository.addNotification(
                eventId = eventId,
                eventType = eventType,
                toUserId = toUserId,
                title = title,
                message = message,
                result = callback
            )
        }.getOrThrow()

        // Retrieve original notification
        val originalNotification = awaitCallback<Result<Notification>> { callback ->
            notificationRepository.getNotificationById(
                notificationId,
                callback
            )
        }.getOrThrow()

        val updateTitle = "Reservation Cancelled"
        val updateMessage = "Your reservation has been cancelled."
        val updateIsRead = true

        // Update notification
        val updatedNotification = originalNotification.copy(
            title = updateTitle,
            message = updateMessage,
            isRead = updateIsRead
        )

        awaitCallback<Result<Unit>> { callback ->
            notificationRepository.updateNotification(
                updatedNotification,
                callback
            )
        }.getOrThrow()

        // Retrieve updated notification
        val retrievedNotification = awaitCallback<Result<Notification>> { callback ->
            notificationRepository.getNotificationById(
                notificationId,
                callback
            )
        }.getOrThrow()

        // Verify updated fields
        assertEquals(notificationId, retrievedNotification.notificationId)
        assertEquals(eventId, retrievedNotification.eventId)
        assertEquals(eventType, retrievedNotification.eventType)
        assertEquals(toUserId, retrievedNotification.toUserId)
        assertEquals(updateTitle, retrievedNotification.title)
        assertEquals(
            updateMessage,
            retrievedNotification.message
        )
        assertEquals(updateIsRead, retrievedNotification.isRead)

        // Verify createdAt was preserved
        assertEquals(
            originalNotification.createdAt,
            retrievedNotification.createdAt
        )

        Log.d("NotificationTest", "updateNotificationExistsTest passed")
    }

     */

    // PASSED
    /*
    @Test
    fun updateNotificationBlankIdTest() {
        val notification = Notification(
            notificationId = "",
            eventId = "test-event",
            eventType = "RESERVATION",
            toUserId = "test-user",
            title = "Test Title",
            message = "Test Message"
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            notificationRepository.updateNotification(
                notification,
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException -> {
                    Log.d(
                        "NotificationTest",
                        "Correctly caught ValidationException"
                    )
                }

                else -> throw exception
            }
        }

        Log.d("NotificationTest", "updateNotificationBlankIdTest passed")
    }

     */

    // PASSED
    /*
    @Test
    fun getNotificationByIdBlankIdTest() {
        val result = awaitCallback<Result<Notification>> { callback ->
            notificationRepository.getNotificationById(
                "",
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException -> {
                    Log.d(
                        "NotificationTest",
                        "Correctly caught ValidationException"
                    )
                }

                else -> throw exception
            }
        }

        Log.d("NotificationTest", "getNotificationByIdBlankIdTest passed")
    }

     */

    // PASSED
    /*
    @Test
    fun updateNotificationNotFoundTest() {
        val notification = Notification(
            notificationId = "missing-notification-001",
            eventId = "test-event",
            eventType = "RESERVATION",
            toUserId = "test-user",
            title = "Test Title",
            message = "Test Message"
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            notificationRepository.updateNotification(
                notification,
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException -> {
                    Log.d(
                        "NotificationTest",
                        "Correctly caught NotFoundException"
                    )
                }

                else -> throw exception
            }
        }

        Log.d("NotificationTest", "updateNotificationNotFoundTest passed")
    }

     */

    // PASSED
    /*
    @Test
    fun getNotificationByIdNotFoundTest() {
        val result = awaitCallback<Result<Notification>> { callback ->
            notificationRepository.getNotificationById(
                "missing-notification-002",
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException -> {
                    Log.d(
                        "NotificationTest",
                        "Correctly caught NotFoundException"
                    )
                }

                else -> throw exception
            }
        }

        Log.d("NotificationTest", "getNotificationByIdNotFoundTest passed")
    }

     */


    // PASSED
    /*
    @Test
    fun getNotificationFromListByIdExistsTest() {
        val notificationId = "ANKfi1zgYxHP8Ba1re0f"

        // Wait until notifications are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!notificationRepository.isNotificationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Notifications failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve notification from local list
        val notification = notificationRepository
            .getNotificationFromListById(notificationId)

        // Verify
        assertNotNull("Notification should exist in the local list", notification)
        assertEquals(notificationId, notification?.notificationId)

        Log.d("NotificationTest", "Existing notification retrieved from local list successfully")
    }

    @Test
    fun getNotificationFromListByIdDoesNotExistTest() {
        val notificationId = "nonexistent-notification-001"

        // Wait until notifications are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!notificationRepository.isNotificationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Notifications failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve notification from local list
        val notification = notificationRepository
            .getNotificationFromListById(notificationId)

        assertNull("Notification should not exist in the local list", notification)

        Log.d("NotificationTest", "Notification not found in local list as expected")
    }

    @Test
    fun getNotificationsFromListByUserIdTest() {
        val userId = "U6IpXzXFGpEuwvVuiObR"

        // Wait until notifications are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!notificationRepository.isNotificationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Notifications failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve notifications from local list
        val userNotifications = notificationRepository
            .getNotificationsFromListByUserId(userId)

        // Verify
        assertTrue(userNotifications.all {
            it.toUserId == userId
        })

        Log.d("NotificationTest", "Notifications for user retrieved from local list")
    }


    @Test
    fun getNotificationsFromListByReadStatusTrueTest() {
        // Wait until notifications are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!notificationRepository.isNotificationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Notifications failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve read notifications from local list
        val readNotifications = notificationRepository
            .getNotificationsFromListByReadStatus(true)

        // Verify
        assertTrue(readNotifications.all {
            it.isRead
        })

        Log.d("NotificationTest", "Read notifications retrieved from local list")
    }

     */

    // ----------------------------------- Rating -----------------------------------
    // PASSED
    /*
    @Test
    fun addRatingExistsTest() {
        val userId = "U6IpXzXFGpEuwvVuiObR"
        val campsiteId = "Hh7EEaZY2nD9OiKmdgFE"
        val reservationId = "gnR0rjkKTVBoUk6f8mIr"
        val review = "Great campsite!"
        val rating = 4.5
        val status = RatingStatus.ACTIVE.name

        // Add rating to Firestore
        val ratingId = awaitCallback<Result<String>> { callback ->
            ratingRepository.addRating(
                userId,
                campsiteId,
                reservationId,
                review,
                rating,
                status,
                callback
            )
        }.getOrThrow()

        // Retrieve the newly created rating
        val result = awaitCallback<Result<Rating>> { callback ->
            ratingRepository.getRatingById(ratingId, callback)
        }.getOrThrow()

        // Verify
        assertEquals(ratingId, result.ratingId)
        assertEquals(userId, result.userId)
        assertEquals(campsiteId, result.campsiteId)
        assertEquals(reservationId, result.reservationId)
        assertEquals(review, result.review)
        assertEquals(rating, result.rating, 0.0)
        assertEquals(status, result.status)

        Log.d("RatingTest", "Rating added successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun getRatingByIdBlankIdTest() {
        val ratingId = ""

        // Retrieve rating from Firestore
        val result = awaitCallback<Result<Rating>> { callback ->
            ratingRepository.getRatingById(ratingId, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("RatingTest", "Correctly caught ValidationException")

                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun updateRatingBlankIdTest() {
        val rating = Rating(
            ratingId = "",
            userId = "test-user-001",
            campsiteId = "test-campsite-001",
            reservationId = "test-reservation-001",
            review = "Testing blank ID",
            rating = 4.0,
            status = RatingStatus.ACTIVE.name
        )

        // Update rating in Firestore
        val result = awaitCallback<Result<Unit>> { callback ->
            ratingRepository.updateRating(rating, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("RatingTest", "Correctly caught ValidationException")

                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun getRatingByIdNotFoundTest() {
        val ratingId = "nonexistent-rating-001"

        // Retrieve rating from Firestore
        val result = awaitCallback<Result<Rating>> { callback ->
            ratingRepository.getRatingById(ratingId, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException ->
                    Log.d("RatingTest", "Correctly caught NotFoundException")

                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun updateRatingBlankIdTest() {
        val rating = Rating(
            ratingId = "",
            userId = "test-user-001",
            campsiteId = "test-campsite-001",
            reservationId = "test-reservation-001",
            review = "Testing blank ID",
            rating = 4.0,
            status = RatingStatus.ACTIVE.name
        )

        // Update rating in Firestore
        val result = awaitCallback<Result<Unit>> { callback ->
            ratingRepository.updateRating(rating, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("RatingTest", "Correctly caught ValidationException")

                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun updateRatingExistsTest() {
        val ratingId = "jjIyt01CXrGS4MeRYq3M"
        val updatedReview = "Excellent campsite!"
        val updatedRating = 5.0
        val updatedStatus = RatingStatus.ACTIVE.name

        // Retrieve original rating
        val originalRating = awaitCallback<Result<Rating>> { callback ->
            ratingRepository.getRatingById(ratingId, callback)
        }.getOrThrow()

        // Create updated rating
        val rating = originalRating.copy(
            review = updatedReview,
            rating = updatedRating,
            status = updatedStatus
        )

        // Update rating in Firestore
        val result = awaitCallback<Result<Unit>> { callback ->
            ratingRepository.updateRating(rating, callback)
        }

        // Verify
        assertTrue(result.isSuccess)

        // Retrieve updated rating
        val updatedResult = awaitCallback<Result<Rating>> { callback ->
            ratingRepository.getRatingById(ratingId, callback)
        }.getOrThrow()

        assertEquals(ratingId, updatedResult.ratingId)
        assertEquals(updatedReview, updatedResult.review)
        assertEquals(updatedRating, updatedResult.rating, 0.0)
        assertEquals(updatedStatus, updatedResult.status)

        // Verify original fields were preserved
        assertEquals(originalRating.userId, updatedResult.userId)
        assertEquals(originalRating.campsiteId, updatedResult.campsiteId)
        assertEquals(originalRating.reservationId, updatedResult.reservationId)
        assertEquals(originalRating.createdAt, updatedResult.createdAt)

        Log.d("RatingTest", "Rating updated successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun getRatingFromListByIdExistsTest() {
        val ratingId = "jjIyt01CXrGS4MeRYq3M"

        // Wait until ratings are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!ratingRepository.isRatingsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Ratings failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve rating from local list
        val rating = ratingRepository.getRatingFromListById(ratingId)

        // Verify
        assertNotNull("Rating should exist in the local list", rating)
        assertEquals(ratingId, rating?.ratingId)

        Log.d("RatingTest", "Rating retrieved from local list successfully")
    }

    @Test
    fun getRatingFromListByIdDoesNotExistTest() {
        val ratingId = "nonexistent-rating-003"

        // Wait until ratings are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!ratingRepository.isRatingsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Ratings failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve rating from local list
        val rating = ratingRepository.getRatingFromListById(ratingId)

        // Verify
        assertNull("Rating should not exist in the local list", rating)

        Log.d("RatingTest", "Rating not found in local list as expected")
    }

     */
    // ----------------------------------- Conversation -----------------------------------
    // PASSED
    /*
    @Test
    fun addConversationExistsTest() {
        val campsiteId = "1tboZkTDSQ7B1G6AwPPj"
        val reservationId = "h7ctE62p7flpfXUKQjvV"
        val ownerId = "5hOgW5zNpxgVc3u6sdeP"
        val userId = "kDrTERBuWvd7OIrk9cAu"
        val lastMessage = "Hello, I have a question about my reservation."
        val status = "OPEN"

        // Add conversation to Firestore
        val conversationId = awaitCallback<Result<String>> { callback ->
            conversationRepository.addConversation(
                campsiteId,
                reservationId,
                ownerId,
                userId,
                lastMessage,
                status,
                callback
            )
        }.getOrThrow()

        // Retrieve the newly created conversation
        val conversation = awaitCallback<Result<Conversation>> { callback ->
            conversationRepository.getConversationById(conversationId, callback)
        }.getOrThrow()

        // Verify
        assertEquals(conversationId, conversation.conversationId)
        assertEquals(campsiteId, conversation.campsiteId)
        assertEquals(reservationId, conversation.reservationId)
        assertEquals(ownerId, conversation.ownerId)
        assertEquals(userId, conversation.userId)
        assertEquals(lastMessage, conversation.lastMessage)
        assertEquals(status, conversation.status)

        Log.d("ConversationTest", "Conversation added successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun getConversationByIdNotFoundTest() {
        val conversationId = "nonexistent-conversation-001"

        // Retrieve conversation from Firestore
        val result = awaitCallback<Result<Conversation>> { callback ->
            conversationRepository.getConversationById(conversationId, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException ->
                    Log.d("ConversationTest", "Correctly caught NotFoundException")

                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun getConversationByIdBlankIdTest() {
        val conversationId = ""

        // Retrieve conversation from Firestore
        val result = awaitCallback<Result<Conversation>> { callback ->
            conversationRepository.getConversationById(conversationId, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("ConversationTest", "Correctly caught ValidationException")

                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun updateConversationExistsTest() {
        val campsiteId = "Hh7EEaZY2nD9OiKmdgFE"
        val reservationId = "gnR0rjkKTVBoUk6f8mIr"
        val ownerId = "5hOgW5zNpxgVc3u6sdeP"
        val userId = "U6IpXzXFGpEuwvVuiObR"
        val lastMessage = "Hello, I need some information."
        val status = "OPEN"

        // Create conversation
        val conversationId = awaitCallback<Result<String>> { callback ->
            conversationRepository.addConversation(
                campsiteId,
                reservationId,
                ownerId,
                userId,
                lastMessage,
                status,
                callback
            )
        }.getOrThrow()

        // Retrieve original conversation
        val originalConversation = awaitCallback<Result<Conversation>> { callback ->
            conversationRepository.getConversationById(conversationId, callback)
        }.getOrThrow()

        // Updated values
        val updatedLastMessage = "Thank you for your response!"
        val updatedStatus = "CLOSED"

        // Create updated conversation
        val updatedConversation = originalConversation.copy(
            lastMessage = updatedLastMessage,
            status = updatedStatus
        )

        // Update conversation in Firestore
        val result = awaitCallback<Result<Unit>> { callback ->
            conversationRepository.updateConversation(
                updatedConversation,
                callback
            )
        }

        // Verify update operation
        assertTrue(result.isSuccess)

        // Retrieve updated conversation
        val conversation = awaitCallback<Result<Conversation>> { callback ->
            conversationRepository.getConversationById(conversationId, callback)
        }.getOrThrow()

        // Verify updated fields
        assertEquals(conversationId, conversation.conversationId)
        assertEquals(updatedLastMessage, conversation.lastMessage)
        assertEquals(updatedStatus, conversation.status)

        // Verify original fields were preserved
        assertEquals(campsiteId, conversation.campsiteId)
        assertEquals(reservationId, conversation.reservationId)
        assertEquals(ownerId, conversation.ownerId)
        assertEquals(userId, conversation.userId)

        Log.d("ConversationTest", "Conversation updated successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun updateConversationBlankIdTest() {
        val conversation = Conversation(
            conversationId = "",
            campsiteId = "test-campsite-001",
            reservationId = "test-reservation-001",
            ownerId = "test-owner-001",
            userId = "test-user-001",
            lastMessage = "Testing blank ID",
            status = "OPEN"
        )

        // Update conversation in Firestore
        val result = awaitCallback<Result<Unit>> { callback ->
            conversationRepository.updateConversation(conversation, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("ConversationTest", "Correctly caught ValidationException")

                else -> throw exception
            }
        }
    }
     */

    // PASSED
    /*
    @Test
    fun updateConversationNotFoundTest() {
        val conversation = Conversation(
            conversationId = "nonexistent-conversation-002",
            campsiteId = "test-campsite-001",
            reservationId = "test-reservation-001",
            ownerId = "test-owner-001",
            userId = "test-user-001",
            lastMessage = "Testing missing conversation",
            status = "OPEN"
        )

        // Update conversation in Firestore
        val result = awaitCallback<Result<Unit>> { callback ->
            conversationRepository.updateConversation(conversation, callback)
        }

        // Verify
        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException ->
                    Log.d("ConversationTest", "Correctly caught NotFoundException")

                else -> throw exception
            }
        }
    }
     */

    // PASSED
    /*
    @Test
    fun getConversationFromListByIdExistsTest() {
        val conversationId = "CX98rBE8OBwmFLckcNv2"

        // Wait until conversations are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!conversationRepository.isConversationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Conversations failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve conversation from local list
        val conversation = conversationRepository
            .getConversationFromListById(conversationId)

        // Verify
        assertNotNull("Conversation should exist in the local list", conversation)
        assertEquals(conversationId, conversation?.conversationId)

        Log.d("ConversationTest", "Conversation retrieved from local list successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun getConversationFromListByIdDoesNotExistTest() {
        val conversationId = "nonexistent-conversation-003"

        // Wait until conversations are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!conversationRepository.isConversationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Conversations failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve conversation from local list
        val conversation = conversationRepository
            .getConversationFromListById(conversationId)

        // Verify
        assertNull("Conversation should not exist in the local list", conversation)

        Log.d("ConversationTest", "Conversation not found in local list as expected")
    }

     */

    // PASSED
    /*
    @Test
    fun getConversationFromListByReservationIdExistsTest() {
        val reservationId = "h7ctE62p7flpfXUKQjvV"

        // Wait until conversations are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!conversationRepository.isConversationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Conversations failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve conversation from local list
        val conversation = conversationRepository
            .getConversationFromListByReservationId(reservationId)

        // Verify
        assertNotNull("Conversation should exist in the local list", conversation)
        assertEquals(reservationId, conversation?.reservationId)

        Log.d("ConversationTest", "Conversation retrieved by reservation ID successfully")
    }

     */

    // PASSED
    /*
    @Test
    fun getConversationFromListByReservationIdDoesNotExistTest() {
        val reservationId = "nonexistent-reservation-001"

        // Wait until conversations are loaded from Firestore
        val startTime = System.currentTimeMillis()

        while (!conversationRepository.isConversationsLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Conversations failed to load from Firestore."
            }

            Thread.sleep(100)
        }

        // Retrieve conversation from local list
        val conversation = conversationRepository
            .getConversationFromListByReservationId(reservationId)

        // Verify
        assertNull("Conversation should not exist in the local list", conversation)

        Log.d("ConversationTest", "Conversation not found by reservation ID as expected")
    }

     */

    // ----------------------------------- Messages -----------------------------------

    // PASSED
    /*
    @Test
    fun addMessageExistsTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val fromUserId = "U6IpXzXFGpEuwvVuiObR"
        val messageText = "Hello!"
        val status = MessageStatus.SENT.name

        val result = awaitCallback<Result<String>> { callback ->
            messageRepository.addMessage(
                conversationId,
                fromUserId,
                messageText,
                status,
                callback
            )
        }

        val messageId = result.getOrThrow()
        assertTrue(messageId.isNotBlank())

        val getResult = awaitCallback<Result<Message>> { callback ->
            messageRepository.getMessageById(
                conversationId,
                messageId,
                callback
            )
        }

        val message = getResult.getOrThrow()

        assertEquals(messageId, message.messageId)
        assertEquals(conversationId, message.conversationId)
        assertEquals(fromUserId, message.fromUserId)
        assertEquals(messageText, message.message)
        assertEquals(status, message.status)
    }
     */

    // PASSED
    /*
    @Test
    fun addMessageConversationDoesNotExistTest() {
        val conversationId = "fakeConversationId123"
        val fromUserId = "U6IpXzXFGpEuwvVuiObR"
        val messageText = "This message should not be added."
        val status = MessageStatus.SENT.name

        val result = awaitCallback<Result<String>> { callback ->
            messageRepository.addMessage(
                conversationId,
                fromUserId,
                messageText,
                status,
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException ->
                    Log.d("MessageTest", "Correctly caught NotFoundException")
                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun getMessageByIdExistsTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val fromUserId = "U6IpXzXFGpEuwvVuiObR"
        val messageText = "Hello!"
        val status = MessageStatus.SENT.name

        val addResult = awaitCallback<Result<String>> { callback ->
            messageRepository.addMessage(
                conversationId,
                fromUserId,
                messageText,
                status,
                callback
            )
        }

        val messageId = addResult.getOrThrow()

        val result = awaitCallback<Result<Message>> { callback ->
            messageRepository.getMessageById(
                conversationId,
                messageId,
                callback
            )
        }

        val message = result.getOrThrow()

        assertEquals(messageId, message.messageId)
        assertEquals(conversationId, message.conversationId)
        assertEquals(fromUserId, message.fromUserId)
        assertEquals(messageText, message.message)
    }

    @Test
    fun getMessageByIdBlankConversationIdTest() {
        val conversationId = ""
        val messageId = "someMessageId"

        val result = awaitCallback<Result<Message>> { callback ->
            messageRepository.getMessageById(
                conversationId,
                messageId,
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("MessageTest", "Correctly caught ValidationException")
                else -> throw exception
            }
        }
    }

    @Test
    fun getMessageByIdBlankMessageIdTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val messageId = ""

        val result = awaitCallback<Result<Message>> { callback ->
            messageRepository.getMessageById(
                conversationId,
                messageId,
                callback
            )
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("MessageTest", "Correctly caught ValidationException")
                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun updateMessageExistsTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val fromUserId = "U6IpXzXFGpEuwvVuiObR"
        val messageText = "Original message."
        val status = MessageStatus.SENT.name

        val addResult = awaitCallback<Result<String>> { callback ->
            messageRepository.addMessage(
                conversationId,
                fromUserId,
                messageText,
                status,
                callback
            )
        }

        val messageId = addResult.getOrThrow()

        val getResult = awaitCallback<Result<Message>> { callback ->
            messageRepository.getMessageById(
                conversationId,
                messageId,
                callback
            )
        }

        val originalMessage = getResult.getOrThrow()

        val updatedMessage = originalMessage.copy(
            message = "Updated message.",
            status = MessageStatus.READ.name
        )

        val updateResult = awaitCallback<Result<Unit>> { callback ->
            messageRepository.updateMessage(
                updatedMessage,
                callback
            )
        }

        updateResult.getOrThrow()

        val updatedResult = awaitCallback<Result<Message>> { callback ->
            messageRepository.getMessageById(
                conversationId,
                messageId,
                callback
            )
        }

        val message = updatedResult.getOrThrow()

        assertEquals(messageId, message.messageId)
        assertEquals(conversationId, message.conversationId)
        assertEquals(fromUserId, message.fromUserId)
        assertEquals("Updated message.", message.message)
        assertEquals(MessageStatus.READ.name, message.status)
        assertEquals(originalMessage.createdAt, message.createdAt)
    }

    @Test
    fun updateMessageBlankMessageIdTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"

        val message = Message(
            messageId = "",
            conversationId = conversationId,
            fromUserId = "U6IpXzXFGpEuwvVuiObR",
            message = "Test message.",
            status = MessageStatus.SENT.name
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            messageRepository.updateMessage(message, callback)
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("MessageTest", "Correctly caught ValidationException")
                else -> throw exception
            }
        }
    }

    @Test
    fun updateMessageBlankConversationIdTest() {
        val message = Message(
            messageId = "someMessageId",
            conversationId = "",
            fromUserId = "U6IpXzXFGpEuwvVuiObR",
            message = "Test message.",
            status = MessageStatus.SENT.name
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            messageRepository.updateMessage(message, callback)
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is ValidationException ->
                    Log.d("MessageTest", "Correctly caught ValidationException")
                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun updateMessageNotFoundTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val messageId = "fakeMessageId123"

        val message = Message(
            messageId = messageId,
            conversationId = conversationId,
            fromUserId = "U6IpXzXFGpEuwvVuiObR",
            message = "Test message.",
            status = MessageStatus.SENT.name
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            messageRepository.updateMessage(message, callback)
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException ->
                    Log.d("MessageTest", "Correctly caught NotFoundException")
                else -> throw exception
            }
        }
    }

    @Test
    fun updateMessageConversationDoesNotExistTest() {
        val conversationId = "fakeConversationId123"
        val messageId = "someMessageId"

        val message = Message(
            messageId = messageId,
            conversationId = conversationId,
            fromUserId = "U6IpXzXFGpEuwvVuiObR",
            message = "Test message.",
            status = MessageStatus.SENT.name
        )

        val result = awaitCallback<Result<Unit>> { callback ->
            messageRepository.updateMessage(message, callback)
        }

        assertTrue(result.isFailure)

        result.onFailure { exception ->
            when (exception) {
                is NotFoundException ->
                    Log.d("MessageTest", "Correctly caught NotFoundException")
                else -> throw exception
            }
        }
    }

     */

    // PASSED
    /*
    @Test
    fun getMessageFromListByIdDoesNotExistTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val messageId = "fakeMessageId123"

        val startTime = System.currentTimeMillis()

        while (!messageRepository.isMessagesLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Messages failed to load from Firestore."
            }
            Thread.sleep(100)
        }

        val message = messageRepository.getMessageFromListById(
            messageId,
            conversationId
        )

        assertNull(message)
    }

     */

    // PASSED
    /*
    @Test
    fun getMessageFromListByIdExistsTest() {
        val conversationId = "zuXxQnkMDO4yHcfgmHZ1"
        val messageId = " LtnRqw7psp9KRemEFF91 "

        val startTime = System.currentTimeMillis()

        while (!messageRepository.isMessagesLoaded.value) {
            check(System.currentTimeMillis() - startTime < 15000) {
                "Messages failed to load from Firestore."
            }
            Thread.sleep(100)
        }

        val message = messageRepository.getMessageFromListById(
            messageId,
            conversationId
        )

        assertNotNull(message)
        assertEquals(messageId, message?.messageId)
        assertEquals(conversationId, message?.conversationId)
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
        check(latch.await(100, TimeUnit.SECONDS)) {
            "Firebase callback timed out"
        }

        // get and return the value of result
        return result.get()
    }
}