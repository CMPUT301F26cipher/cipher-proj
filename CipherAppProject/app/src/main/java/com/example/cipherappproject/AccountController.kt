package com.example.cipherappproject

import com.example.cipherappproject.database.enums.ProfileRole
import com.example.cipherappproject.database.enums.ProfileStatus
import com.example.cipherappproject.database.models.Device
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.registration.EntrantDetails
import com.example.cipherappproject.ui.registration.OwnerDetails

/**
 * Looks up and creates the profile that belongs to this device.
 *
 * Uses the shared ProfileRepository (RepositoryDatabase). Lookups use the repository's local
 * list, so callers must wait for [isProfilesLoaded] before trusting [findProfile].
 */
class AccountController {
    private val profileRepository get() = RepositoryDatabase.profileRepository
    private val device = Device()

    /** True once the first snapshot of the profiles collection has arrived. */
    fun isProfilesLoaded(): Boolean = profileRepository.isProfilesLoaded.value

    /** Gets this device's Firebase Installation ID, or null if it could not be obtained. */
    fun getAccountId(result: (String?) -> Unit) {
        device.getDeviceId(result)
    }

    /** The profile registered for [deviceId], or null (check [isProfilesLoaded] first). */
    fun findProfile(deviceId: String): Profile? {
        return profileRepository.getProfileFromListByDeviceId(deviceId)
    }

    /** Creates an ENTRANT profile for this device. The Result holds the new profile id. */
    fun createEntrantAccount(
        entrantDetails: EntrantDetails,
        deviceId: String,
        result: (Result<String>) -> Unit
    ) {
        createAccount(
            entrantDetails.fullName,
            entrantDetails.emailAddress,
            entrantDetails.phoneNumber,
            ProfileRole.ENTRANT,
            deviceId,
            result
        )
    }

    /** Creates an OWNER profile for this device. The Result holds the new profile id. */
    fun createOwnerAccount(
        ownerDetails: OwnerDetails,
        deviceId: String,
        result: (Result<String>) -> Unit
    ) {
        createAccount(
            ownerDetails.fullName,
            ownerDetails.emailAddress,
            ownerDetails.phoneNumber,
            ProfileRole.OWNER,
            deviceId,
            result
        )
    }

    private fun createAccount(
        name: String,
        email: String,
        phoneNumber: String,
        role: ProfileRole,
        deviceId: String,
        result: (Result<String>) -> Unit
    ) {
        profileRepository.addProfile(
            name = name,
            email = email,
            role = listOf(role.name),
            imageProfilePath = "",
            deviceId = deviceId,
            status = ProfileStatus.ACTIVE.name,
            phoneNumber = phoneNumber,
            notificationsEnabled = true,
            result = result
        )
    }
}
