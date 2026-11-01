package com.example.cipherappproject

import androidx.compose.runtime.mutableStateOf
import java.util.UUID

class CampsiteInfoController {

    private val campsites = mutableListOf<CampsiteInfoModel>()

    fun registerCampsite(
        ownerId: String,
        name: String,
        description: String,
        amenities: List<String>,
        maxOccupancy: Int,
        latitude: Double,
        longitude: Double,
        image: String?,
        tags: List<String>
    ): CampsiteInfoModel {

        val campsite = CampsiteInfoModel(
            campsiteID = UUID.randomUUID().toString(),
            ownerID = ownerId,
            campsiteName = name,
            description = description,
            amenities = amenities,
            maxOccupancy = maxOccupancy,
            latitude = latitude,
            longitude = longitude,
            image = image,
            tags = tags
        )

        campsites.add(campsite)

        return campsite
    }

    /*
        Retrieves campsite owned by the user info
     */
    fun getCampsite(
        campsiteID: String,
        ownerID: String
    ): CampsiteInfoModel? {
        return campsites.find {
            it.campsiteID == campsiteID &&
                    it.ownerID == ownerID
        }
    }

    fun getOwnerCampsites(
        ownerId: String
    ): List<CampsiteInfoModel> {
        return campsites.filter {
            it.ownerID == ownerId // get my campsites only
        }
    }

    fun updateCampsite(
        updatedCampsite: CampsiteInfoModel,
        ownerID: String
    ): Boolean {
        val index = campsites.indexOfFirst {
            it.campsiteID == updatedCampsite.campsiteID &&
                    it.ownerID == ownerID
        }

        campsites[index] = updatedCampsite.copy(
            ownerID = ownerID
        )

        return true
    }
}