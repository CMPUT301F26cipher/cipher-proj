
package com.example.cipherappproject

import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.ui.registration.EntrantDetails
import com.example.cipherappproject.ui.registration.OwnerDetails
import com.example.cipherappproject.database.models.Device

/**
 * Controls account-related operations within the application.
 *
 * Acts as an intermediary between the UI and the [ProfileRepository],
 * handling device identification, account checking, and account creation
 * for entrants and owners.
 *
 * Outstanding issues:
 * - Account creation currently assigns an empty profile image path.
 * - Account roles and status values are currently hardcoded.
 */
class AccountController(
    private val profileRepository: ProfileRepository
) {
    private val device = Device()

    /**
     * Retrieves the current device's Firebase Installation ID.
     *
     * @param result Callback that receives the device ID, or null if retrieval fails.
     */
    fun getAccountId(result: (String?) -> Unit) {
        device.getDeviceId { deviceId ->
            if (deviceId != null) {
                result(deviceId)
            } else {
                result(null)
            }
        }
    }

    /**
     * Checks whether an account profile exists for the given device.
     *
     * @param deviceId The Firebase Installation ID associated with the account.
     * @param result Callback that receives the matching [Profile], or null if none is found.
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
     * Creates a new entrant account and saves the profile to Firebase.
     *
     * Uses the entrant's registration details to create a profile
     * with the "Entrant" role and an active status.
     *
     * @param entrantDetails The entrant's registration information.
     * @param deviceId The Firebase Installation ID associated with the account.
     * @param result Callback that receives the newly created profile ID,
     * or null if account creation fails.
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
     * Creates a new owner account and saves the profile to Firebase.
     *
     * Uses the owner's registration details to create a profile
     * with the "Owner" role and an active status.
     *
     * @param ownerDetails The owner's registration information.
     * @param deviceId The Firebase Installation ID associated with the account.
     * @param result Callback that receives the newly created profile ID,
     * or null if account creation fails.
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


/*
 * AI Documentation Assistance
 *
 * Prompt:
 * "I need you to add this:
 * Code Documentation: For each source code file, you should have a brief
 * introductory comment describing its purpose or role within the application
 * or a design pattern, as well as any currently outstanding issues. Provide
 * KDoc interface documentation for your model classes and their public
 * methods (at least).
 *
 * or help me add it to my code. Tell me what to put or where, and if it is
 * very clear what it does you can add the comment.
 *
 * [Provided the AccountController.kt source code]"
 *
 * AI Response:
 *
package com.example.cipherappproject

import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.repositories.ProfileRepository
import com.example.cipherappproject.ui.registration.EntrantDetails
import com.example.cipherappproject.ui.registration.OwnerDetails
import com.example.cipherappproject.database.models.Device

/**
 * Controls account-related operations within the application.
 *
 * Acts as an intermediary between the UI and the [ProfileRepository],
 * handling device identification, account checking, and account creation
 * for entrants and owners.
 *
 * Outstanding issues:
 * - Account creation currently assigns an empty profile image path.
 * - Account roles and status values are currently hardcoded.
 */
class AccountController(
    private val profileRepository: ProfileRepository
) {
    private val device = Device()

    /**
     * Retrieves the current device's Firebase Installation ID.
     *
     * @param result Callback that receives the device ID, or null if retrieval fails.
     */
    fun getAccountId(result: (String?) -> Unit) {
        device.getDeviceId { deviceId ->
            if (deviceId != null) {
                result(deviceId)
            } else {
                result(null)
            }
        }
    }

    /**
     * Checks whether an account profile exists for the given device.
     *
     * @param deviceId The Firebase Installation ID associated with the account.
     * @param result Callback that receives the matching [Profile], or null if none is found.
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
     * Creates a new entrant account and saves the profile to Firebase.
     *
     * Uses the entrant's registration details to create a profile
     * with the "Entrant" role and an active status.
     *
     * @param entrantDetails The entrant's registration information.
     * @param deviceId The Firebase Installation ID associated with the account.
     * @param result Callback that receives the newly created profile ID,
     * or null if account creation fails.
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
     * Creates a new owner account and saves the profile to Firebase.
     *
     * Uses the owner's registration details to create a profile
     * with the "Owner" role and an active status.
     *
     * @param ownerDetails The owner's registration information.
     * @param deviceId The Firebase Installation ID associated with the account.
     * @param result Callback that receives the newly created profile ID,
     * or null if account creation fails.
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

 *
 * AI Tool: ChatGPT (OpenAI) - Free version
 */

