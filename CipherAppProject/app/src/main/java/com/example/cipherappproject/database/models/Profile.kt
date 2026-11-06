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
2026-11-06  Sofia      1.03     delete getters and setters. Set the attributes to public.
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
    val profileId: String = "",
    var name: String = "",
    var email: String = "",
    /*Learned and taken from baeldung blog website as https://www.baeldung.com/kotlin/initialize-list*/
    val role: List<String> = emptyList<String>(),
    var imageProfilePath: String = "",
    val deviceId: String = "",
    var status: String = "",
)
