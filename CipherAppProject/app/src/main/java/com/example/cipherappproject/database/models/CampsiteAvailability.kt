package com.example.cipherappproject.database.models

/*
--------------------------------------------------------------------------------------
Program Name: CampsiteAvailability.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as CampsiteAvailability
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia      1.01     added skeleton
2026-11-03  Sofia      1.02     fixed getters and setters
2026-11-05  Sofia      1.03     fixed getters and setters x2 and added more comments for clarification
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


/**
 * Represents a CampsiteAvailability main attributes and status in relation with the Firebase database.
 *
 * A CampsiteAvailability is meant to be those campsite which the owner made available and
 * the dates that offer is.Since one campsite have many available times, this is a collection within the firebase. It also provides that status of the campsite if for example all dates
 * are booked or the campsite may be unexpected be unable due a wildfire for that specific range of dates.
 *
 * @property campsiteAvailabilityId use to uniquely identify each campsite availability within the database
 * @property campsiteId use to uniquely identify each campsite within the database
 * @property startDate when the campsite is open
 * @property endDate when the campsite is close
 * @property status the campsite status as available, notAvailable for certain time frame.
 *
 *
 */

data class CampsiteAvailability (
    private val campsiteAvailabilityId: String = "",
    private val campsiteId: String = "",
    private val startDate: String = "",
    private val endDate: String = "",
    private var status: String = "" // can change --> useful for log too
){

    fun getCampsiteAvailabilityId():String {
        return campsiteAvailabilityId
    }

    fun getCampsiteId():String {
        return campsiteId
    }

    fun getCampsiteAvailabilityStartDate():String {
        return startDate
    }

    fun getCampsiteAvailabilityEndDate():String {
        return endDate
    }

    fun getCampsiteAvailabilityStatus():String {
        return status
    }


}