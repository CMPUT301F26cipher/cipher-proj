package com.example.cipherappproject.database.models

/*
--------------------------------------------------------------------------------------
Program Name: Campsite.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Campsite
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-02-24  Sofia      1.01     added skeleton
2026-03-22  Sofia      1.01     fixed getters and setters
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

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
    private val campsiteId: String = "",
    private val ownerId: String = "",
    private var campsiteName: String = "",
    private val mapLocation: List<String> = emptyList(), // format latitude and longitude and name location // read only
    private var description: String = "",
    private val amenities: MutableList<String> = mutableListOf(),
    private var maxGuests: Int = 0,
    private var pricePerNight: Float = 0.0f, // NEED TO REVIEW THIS --> we need to worry about precision and losing money?
    private var imagePath: String = "",
    private val tags: MutableList<String> = mutableListOf(),
    private var qrCodeImagePath: String = ""
){
    fun getCampsiteAmenities(): List<String> {
        return amenities
    }

    fun addCampsiteAmenity(amenity: String) {
        amenities.add(amenity)
    }

    fun removeCampsiteAmenity(amenity: String) {
        amenities.remove(amenity)
    }
    fun getCampsiteId(): String {
        return campsiteId
    }

    fun getCampsiteOwnerId(): String {
        return ownerId
    }

    fun getCampsiteName(): String {
        return campsiteName
    }

    fun setCampsiteName(name: String) {
        campsiteName = name
    }

    fun getCampsiteLocation(): List<String> {
        return mapLocation
    }

    fun getCampsiteDescription(): String {
        return description
    }

    fun setCampsiteDescription(desc: String) {
        description = desc
    }

    fun getCampsiteMaxGuests(): Int {
        return maxGuests
    }

    fun setCampsiteMaxGuests(numGuests: Int) {
        maxGuests = numGuests
    }

    fun getCampsitePricePerNight(): Float {
        return pricePerNight
    }

    fun setCampsitePricePerNight(price: Float) {
        pricePerNight = price
    }

    fun getCampsiteImagePath(): String {
        return imagePath
    }

    fun setCampsiteImagePath(path: String) {
        imagePath = path
    }

    fun getCampsiteTags(): List<String> {
        return tags
    }

    fun addCampsiteTag(tag: String) {
        tags.add(tag)
    }

    fun removeCampsiteTag(tag: String) {
        tags.remove(tag)
    }

    fun getCampsiteQrCodeImagePath(): String {
        return qrCodeImagePath
    }

    fun setCampsiteQrCodeImagePath(path: String) {
        qrCodeImagePath = path
    }
}



