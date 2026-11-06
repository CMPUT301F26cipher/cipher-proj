package com.example.cipherappproject.database.models

/*
--------------------------------------------------------------------------------------
Program Name: Profile.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Profile
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia      1.01     added skeleton
2026-11-03  Sofia      1.02     fixed getters and setters
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents a Profile main attributes and status in relation with the Firebase database.
 *
 * A Profile is meant to be used for the three main actors
 * in the project: User, Campsite Owner (Owner), and Admin.
 *
 * @property profileId use to uniquely identify each profile within the database
 * @property name the profile name for the actor account in the app
 * @property email the profile email for the actor account in the app
 * @property role which determines profile actions, permissions and UI
 * @property imageProfilePath which is meant to store the local path of that image rather than the .png
 * @property deviceId the profile device
 * @property status meant to be if the profile gets deleted --> active, deactivated
 *
 */
data class Profile (
    private val profileId: String = "",
    private var name: String = "",
    private var email: String = "",
    /*Learned and taken from baeldung blog website as https://www.baeldung.com/kotlin/initialize-list*/
    private val role: List<String> = emptyList<String>(),
    private var imageProfilePath: String = "",
    private val deviceId: String = "",
    private var status: String = "",
){
    fun getStatus(): String {
        return status
    }

    fun setStatus(profileStatus: String) {
        status = profileStatus
    }

    fun getProfileId(): String {
        return profileId
    }

    fun getRole(): List<String> {
        return role
    }

    fun getImageProfilePath(): String {
        return imageProfilePath
    }

    fun getDeviceId(): String {
        return deviceId
    }

    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }
}
