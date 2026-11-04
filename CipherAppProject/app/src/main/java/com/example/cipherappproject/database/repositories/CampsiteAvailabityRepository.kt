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
2026-03-27  Sofia      1.01     added skeleton
2026-04-10  Sofia      1.02     added query get by id
2026-04-11  Sofia      1.03     added queries retrieve list and set by group of collection
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.CampsiteAvailability
import com.example.cipherappproject.database.models.Profile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CampsiteAvailabilityRepository {

    private val db = Firebase.firestore

    // https://firebase.google.com/docs/firestore/pipelines/stages/input/collection-group
    private val campsitesAvailabilityRef = db.collectionGroup("availability")
    private val _allCampsitesAvailability = mutableStateListOf<CampsiteAvailability>()

    val campsitesAvailability : List<CampsiteAvailability>
        get() = _allCampsitesAvailability


    fun createCampsiteAvailability( // NEED TO HANDLE FAILED OR NOT!
        campsiteId: String,
        startDate: String,
        endDate: String,
        status: String

    ): String {
        // https://firebase.google.com/docs/firestore/data-model
        // create a reference and id to the new document
        val campsiteAvailabilityRef = db.collection("campsites/$campsiteId/availability").document()

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
        //Tasks.await(campsiteAvailabilityRef.set(campsiteAvailability))

        return campsiteAvailabilityRef.id

    }

    fun updateCampsiteAvailability(campsiteAvailability: CampsiteAvailability){
        val campsiteAvailabilityId = campsiteAvailability.getCampsiteAvailabilityId()
        val campsiteId = campsiteAvailability.getCampsiteAvailabilityCampsiteId()


        val campsiteAvailabilityRef = db.collection("campsites/$campsiteId/availability")


        campsiteAvailabilityRef.document(campsiteAvailabilityId).set(campsiteAvailability)
    }

    fun getCampsiteAvailabilityById(
        campsiteAvailabilityId: String,
        campsiteId: String,
        result: (CampsiteAvailability?) -> Unit
    ){
        val campsiteAvailabilityRef = db.collection("campsites/$campsiteId/availability")
            .document(campsiteAvailabilityId)
            .get()
            .addOnSuccessListener { document ->
                val campsite = document.toObject(CampsiteAvailability::class.java)

                result(campsite)
            }

            .addOnFailureListener { exception ->
                result(null)
            }

    }

    /*
    * return:
    *  - list of CampsiteAvailability
    *  - empty list of CampsiteAvailability
    *  - null if error
    * */
    fun getAvailabilitiesByCampsiteId(
        campsiteId: String,
        result: (List<CampsiteAvailability>?) -> Unit
    ) {
        val campsiteAvailabilityRef = db.collection("campsites")
            .document(campsiteId)
            .collection("availability")
            .get()
            .addOnSuccessListener { documents ->

                val availabilities : MutableList<CampsiteAvailability> = mutableListOf()

                for (document in documents) {
                    val campsite = document.toObject(CampsiteAvailability::class.java)

                    availabilities .add(campsite)
                }

                result(availabilities )
            }
            .addOnFailureListener { exception ->
                result(null)
            }
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

