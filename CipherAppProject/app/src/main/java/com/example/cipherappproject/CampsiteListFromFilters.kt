package com.example.cipherappproject
/*
--------------------------------------------------------------------------------------
Program Name: CampsiteListFromFilters.kt
Date Written: Nov 4 2026
Author: Bailey Parrish
Purpose: Class that takes a SearchFilter that can retrieve a subset of campsites that match those filters
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-02  Bailey      1.01     Built
2026-11-17  Madison     1.02     getList returns filtered results, case-insensitive, blank shows all
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import com.example.cipherappproject.database.models.Campsite
import java.time.LocalDate

/**
 * Takes the full campsite list and a SearchFilter, and returns the campsites that match every filter.
 *
 * @param fullCampsiteList every campsite currently loaded from the database
 * @param searchFilter the filters the entrant chose
 * @param availableDates returns the dates a campsite can still be booked
 */
class CampsiteListFromFilters(
    private val fullCampsiteList: List<Campsite>,
    private val searchFilter: SearchFilter,
    private val availableDates: (campsiteId: String) -> List<LocalDate>,
) {
    /** @return the campsites that match every filter. An empty filter returns every campsite. */
    fun getList(): List<Campsite> {
        return fullCampsiteList.filter { campsite ->
            matchesName(campsite) &&
                matchesLocation(campsite) &&
                matchesGuests(campsite) &&
                matchesDates(campsite)
        }
    }

    private fun matchesName(campsite: Campsite): Boolean {
        return searchFilter.name.isBlank() ||
            campsite.campsiteName.contains(searchFilter.name, ignoreCase = true)
    }

    // mapLocation is [latitude, longitude, location name]; only the name is searched
    private fun matchesLocation(campsite: Campsite): Boolean {
        return searchFilter.location.isBlank() ||
            campsite.mapLocation.getOrNull(2).orEmpty()
                .contains(searchFilter.location, ignoreCase = true)
    }

    private fun matchesGuests(campsite: Campsite): Boolean {
        val guests = searchFilter.guests ?: return true
        return campsite.maxGuests >= guests
    }

    // Every night of the stay must be bookable
    private fun matchesDates(campsite: Campsite): Boolean {
        val checkIn = searchFilter.checkIn ?: return true
        val checkOut = searchFilter.checkOut ?: return true
        val freeDates = availableDates(campsite.campsiteId).toSet()
        return freeDates.containsAll(DateUtils.nightsBetween(checkIn, checkOut))
    }
}