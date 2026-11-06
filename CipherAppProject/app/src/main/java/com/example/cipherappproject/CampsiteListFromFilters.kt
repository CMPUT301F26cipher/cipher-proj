package com.example.cipherappproject

import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository

/**
 * Class that takes a SearchFilter that can retrieve a subset of campsites that match those filters
 *
 * @param searchFilter For navigability
 *
 * @property fullCampsiteList contains all the campsites from the repository
 * @property filteredCampsiteList contains all the campsites that match the previous search. By default, contains all campsites
 */
/*
 * Built by Bailey
 * Nov 4 2026
 * Updates:
 *
 */
class CampsiteListFromFilters(
    val fullCampsiteList: List<Campsite>,
    var searchFilter: SearchFilter
) {
    private var filteredCampsiteList = fullCampsiteList.toMutableList()

    fun filterCampsites(){
        filteredCampsiteList = fullCampsiteList.toMutableList()
        if (searchFilter.getName().length <= 3) {
            throw SearchException("SearchStringTooShort")
        }
        else {
            for (campsite in fullCampsiteList){
                if (!campsite.campsiteName.contains(searchFilter.getName())){
                    filteredCampsiteList.remove(campsite)
                }
            }
        }
    }

    // TODO: Fix this method, it should return the filteredCampsiteList but that breaks everything
    fun getList(): List<Campsite>{
        return fullCampsiteList
    }
}