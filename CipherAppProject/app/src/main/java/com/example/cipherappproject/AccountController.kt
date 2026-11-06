package com.example.cipherappproject

import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.ui.registration.EntrantDetails
import com.example.cipherappproject.ui.registration.OwnerDetails

class AccountController(
    private val profileRepository: ProfileRepository
) {

    /**
    Checks whether a profile exists for the given deviceId
    @param deviceId The Firebase Installation ID
    result Returns the matching Profile, or null if not found
     */
    fun checkAccount(
        deviceId: String,
        result: (Profile?) -> Unit
    ) {
        profileRepository.getProfileByDeviceId(deviceId) { profile ->
            result(profile)
        }
    }
    /**
    creates a profile for an entrant and adds it to the firebase
    returns profile id or null if failure
     */
    fun createEntrantAccount(
        entrantDetails: EntrantDetails,
        deviceId: String,
        result: (String?) -> Unit
    ) {
        profileRepository.addProfile(
            name = entrantDetails.fullName,
            email = entrantDetails.emailAddress,
            role = listOf("Entrant"),
            imageProfilePath = "",
            deviceId = deviceId,
            status = "active"
        ) { profileId ->
            result(profileId)
        }
    }

    /**
    creates a profile for an owner and adds it to the firebase
    returns profile id or null if failure
     */
    fun createOwnerAccount(
        ownerDetails: OwnerDetails,
        deviceId: String,
        result: (String?) -> Unit
    ) {
        profileRepository.addProfile(
            name = ownerDetails.fullName,
            email = ownerDetails.emailAddress,
            role = listOf("Owner"),
            imageProfilePath = "",
            deviceId = deviceId,
            status = "active"
        ) { profileId ->
            result(profileId)
        }
    }

}