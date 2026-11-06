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
2026-10-27  Sofia      1.01     added skeleton
2026-11-03  Sofia      1.02     fixed getters and setters
2026-11-05  Sofia      1.03     fixed getters and setters x2, added status for campsite, changed float to double
2026-11-06   Sofia     1.04     delete getters and setters. Set the attributes to public. Left methods.
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
 * @property mapLocation a list that format latitude and longitude and name --> read only
 * @property description the campsite description
 * @property amenities the campsite further features
 * @property pricePerNight campsite price
 * @property imagePath the path locally to the campsite image
 * @property tags the keywords need for the filter feature adn search bar
 * @property maxGuests how many guest the campsite can hold
 * @property qrCodeImagePath the campsite qr code path
 * @property status meant to be if the owner deletes their profile the campsites related to it just become all unavailable --> available, notAvailable
 *
 *
 */

data class Campsite (
    val campsiteId: String = "",
    val ownerId: String = "",
    var campsiteName: String = "",
    val mapLocation: List<String> = emptyList(),
    var description: String = "",
    var amenities: MutableList<String> = mutableListOf(),
    var maxGuests: Int = 0,
    var pricePerNight: Double = 0.0,
    var imagePath: String = "",
    var tags: MutableList<String> = mutableListOf(),
    var qrCodeImagePath: String = "",
    var status: String = ""
){

    fun addCampsiteAmenity(amenity: String) {
        amenities.add(amenity)
    }

    fun removeCampsiteAmenity(amenity: String) {
        amenities.remove(amenity)
    }

    fun addCampsiteTag(tag: String) {
        tags.add(tag)
    }

    fun removeCampsiteTag(tag: String) {
        tags.remove(tag)
    }
}



