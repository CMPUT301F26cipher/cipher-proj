package com.example.cipherappproject
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
 *
 */
data class Profile (
    private val profileId: String = "",
    private var name: String = "",
    private var email: String = "",
    /*Learned and taken from baeldung blog website as https://www.baeldung.com/kotlin/initialize-list*/
    private val role: List<String> = emptyList<String>(),
    private var imageProfilePath: String = "",
    private val deviceId: String = "" // WE NEED TO REVIEW THIS TYPE!

) {
    /**
     * Return the profile id
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param NA
     * @return the profile id. OTHER SPECIFICATIONS WILL BE ADDED LATER
     */
    fun getProfileId(): String {
        return profileId
    }

    /**
     * Return the profile name
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param NA
     * @return the profile name. OTHER SPECIFICATIONS WILL BE ADDED LATER
     */
    fun getProfileName(): String {
        return name
    }

    /**
     * Set the profile name
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param nameProfile the name of the user
     * @return NA
     */
    fun setProfileName(nameProfile: String) {
        name = nameProfile
    }

    /**
     * Return the profile email
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param NA
     * @return the profile email. OTHER SPECIFICATIONS WILL BE ADDED LATER
     */
    fun getProfileEmail(): String {
        return email
    }

    /**
     * Set the profile email
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param emailProfile the name of the user
     * @return NA
     */
    fun setProfileEmail(emailProfile: String) {
        email =  emailProfile
    }

    /**
     * Return the profile role
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param NA
     * @return the profile role. OTHER SPECIFICATIONS WILL BE ADDED LATER
     */
    fun getProfileRole(): List<String> {
        return role
    }

    /**
     * Return the profile image
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param NA
     * @return the profile image path. OTHER SPECIFICATIONS WILL BE ADDED LATER
     */
    fun getProfileImage(): String {
        return imageProfilePath
    }

    /**
     * Set the profile Image
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param imagePath the name of the user
     * @return NA
     */
    fun setProfileImage(imagePath: String) {
        imageProfilePath = imagePath

    }

    /**
     * Return the profile device id
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param NA
     * @return the profile device id. OTHER SPECIFICATIONS WILL BE ADDED LATER
     */
    fun getProfileDeviceId(): String {
        return deviceId
    }

}