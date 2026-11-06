package com.example.cipherappproject

import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.repositories.ProfileRepository

class AccountController(
    private val profileRepository: ProfileRepository
) {

    /*
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

    // Creates a profile after registration
    fun createAccount() {

    }

}