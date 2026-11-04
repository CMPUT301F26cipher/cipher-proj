package com.example.cipherappproject.database.models
/**
 * Represents a Campsite main attributes and status in relation with the Firebase database.
 *
 * A Campsite is meant to provide its owner, campsite name, location, description, amenities,
 * maximum of guests, price, tags for the filter and qr code.
 *
 * @property campsiteId use to uniquely identify each campsite within the database
 * @property ownerId use to identify to which owner is related to
 * @property campsiteName is the campsite name,
 * @property mapLocation the path to the image with the location of the campsite
 * @property description the campsite description
 * @property amenities the campsite further features
 * @property pricePerNight campsite price
 * @property imagePath the path locally to the campsite image
 * @property tags the keywords need for the filter feature adn search bar
 * @property maxGuests how many guest the campsite can hold
 * @property qrCodeImagePath the campsite qr code path
 *
 *
 */

data class Campsite (
    val campsiteId: String = "",
    val ownerId: String = "",
    var campsiteName: String = "",
    val mapLocation: String = "",
    var description: String = "",
    val amenities: List<String> = emptyList(),
    val maxGuests: Int = 0,
    val pricePerNight: Float = 0.0f, // NEED TO REVIEW THIS --> we need to worry about precision and losing money?
    val imagePath: String = "",
    val tags: List<String> = emptyList(),
    val qrCodeImagePath: String = ""
){
    // WILL FOLLOW A SIMILAR FORMAT TO PROFILE
    fun getCampsiteId(): String {
        return campsiteId
    }

    fun getOwnerId(): String {
        return ownerId
    }

    fun getCampsiteName(): String {
        return campsiteName
    }

    fun setCampsiteName() {

    }

    fun getCampsiteLocation(): String {
        return mapLocation
    }

    fun setCampsiteLocation() {
    }

    fun getCampsiteDescription(): String {
        return description
    }

    fun setCampsiteDescription() {
    }

    fun getCampsiteMaxGuests(): Int {
        return maxGuests
    }

    fun setCampsiteMaxGuests() {
    }

    fun getCampsitePricePerNight(): Float {
        return pricePerNight
    }

    fun setCampsitePricePerNight() {

    }

    fun getCampsiteImagePath(): String {
        return imagePath
    }

    fun setCampsiteImagePath() {
    }

    fun getCampsiteTags(): List<String> {
        return tags
    }

    fun setCampsiteTags() {
    }

    fun getCampsiteQrCodeImagePath(): String {
        return qrCodeImagePath
    }

    fun setCampsiteQrCodeImagePath() {
    }
}



