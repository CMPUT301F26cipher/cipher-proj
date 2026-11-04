package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
Program Name: CampsiteAvailabilityRepository.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for CampsiteAvailabilityRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-02-24  Sofia      1.01     added skeleton
2026-03-22  Sofia      1.01     added query get by id
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.CampsiteAvailability
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.models.Reservation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CampsiteAvailabilityRepository {

    private val db = Firebase.firestore
    private val campsitesAvailabilityRef = db.collection("campsiteAvailability")
    private val _allCampsitesAvailability = mutableStateListOf<CampsiteAvailability>()

    val campsitesAvailability : List<CampsiteAvailability>
        get() = _allCampsitesAvailability


    fun createReservation(
        campsiteId: String,
        startDate: String,
        endDate: String,
        status: String

    ): String {
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
        Tasks.await(campsiteAvailabilityRef.set(campsiteAvailability))

        return campsiteAvailabilityRef.id

    }

    fun updateReservation(campsiteAvailability: CampsiteAvailability){
        campsitesAvailabilityRef.document(campsiteAvailability.getCampsiteAvailabilityId()).set(campsiteAvailability)
    }

    init {
        campsitesAvailabilityRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allCampsitesAvailability.clear()

            snapshot?.documents?.forEach { document ->
                val campsiteAvailability = document.toObject(CampsiteAvailability::class.java) // pass from firestore to kotlin object

                if (campsiteAvailability != null) {
                    _allCampsitesAvailability.add(campsiteAvailability)
                }
            }
        }
    }


}

