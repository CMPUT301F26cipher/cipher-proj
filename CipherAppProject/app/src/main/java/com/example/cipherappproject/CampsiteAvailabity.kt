package com.example.cipherappproject
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
    private val campsiteAvailabilityId:String = "",
    private val campsiteId: String = "",
    private val startDate: String = "",
    private val endDate: String = "",
    private val status: String = "" // NEED TO REVIEW THIS --> this class is just offered or can th owner cancel it?
) {
    // WILL FOLLOW A SIMILAR FORMAT TO PROFILE
    fun getCampsiteAvailabilityId():String {
        return campsiteAvailabilityId
    }

    fun getCampsiteId():String {
        return campsiteId
    }

    fun getStartDate():String {
        return startDate
    }

    fun getEndDate():String {
        return endDate
    }

    fun getStatus():String {
        return status
    }


}