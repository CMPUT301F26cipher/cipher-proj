package com.example.cipherappproject.database.repositories

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.CampsiteAvailability
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CampsiteAvailabilityRepository {

    private val db = Firebase.firestore
    private val campsitesAvailabilityRef = db.collection("campsiteAvailability")
    private val _campsitesAvailability = mutableStateListOf<CampsiteAvailability>()

    val reservations: List<CampsiteAvailability>
        get() = _campsitesAvailability


    fun createReservation(
        campsiteId: String,
        startDate: String,
        endDate: String,
        status: String

    ) {
        // create a reference and id to the new document
        val campsiteAvailabilityRef = campsitesAvailabilityRef.document()

        // create object
        val campsiteAvailability = CampsiteAvailability(
            campsiteAvailabilityId = campsiteAvailabilityRef.id,
            campsiteId = campsiteId,
            startDate = startDate,
            endDate = endDate,
            status = status

        )

        // save the object in the firebase
        campsiteAvailabilityRef.set(campsiteAvailability)
    }

    fun updateReservation(campsiteAvailability: CampsiteAvailability){
        campsitesAvailabilityRef.document(campsiteAvailability.campsiteAvailabilityId).set(campsiteAvailability)
    }


}

