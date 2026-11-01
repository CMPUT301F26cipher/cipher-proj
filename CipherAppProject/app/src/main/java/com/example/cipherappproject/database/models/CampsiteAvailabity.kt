package com.example.cipherappproject.database.models
/**
 * Represents a CampsiteAvailability main attributes and status in relation with the Firebase database.
 *
 * A CampsiteAvailability is meant to be those campsite which the owner made available and
 * the dates that offer is. It also provides that status of the campsite if for example all dates
 * are booked or the campsite may be unexpected be unable due a wildfire.
 *
 * @property campsiteAvailabilityId use to uniquely identify each campsite availability within the database
 * @property campsiteId use to uniquely identify each campsite within the database
 * @property startDate when the campsite is open
 * @property endDate when the campsite is close
 * @property status the campsite status as available, notAvailable
 *
 *
 */

data class CampsiteAvailability (
    val campsiteAvailabilityId:String = "",
    val campsiteId: String = "",
    val startDate: String = "",
    val endDate: String = "",
    var status: String = "" // can change --> useful for log too
)

