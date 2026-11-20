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
2026-11-17  Madison     1.02      getList returns filtered results, case-insensitive, blank shows all
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import com.example.cipherappproject.database.models.Campsite

/**
 * Takes the full campsite list and a SearchFilter, and returns the campsites that match.
 *
 * @param fullCampsiteList every campsite currently loaded from the database
 * @param searchFilter the filters the entrant chose
 */
class CampsiteListFromFilters(
    private val fullCampsiteList: List<Campsite>,
    private val searchFilter: SearchFilter
) {
    /**
     * @return campsites whose name contains the searched name, ignoring case.
     * A blank search returns every campsite.
     */
    fun getList(): List<Campsite> {
        val name = searchFilter.getName().trim()
        if (name.isEmpty()) {
            return fullCampsiteList
        }
        return fullCampsiteList.filter { campsite ->
            campsite.campsiteName.contains(name, ignoreCase = true)
        }
    }
}