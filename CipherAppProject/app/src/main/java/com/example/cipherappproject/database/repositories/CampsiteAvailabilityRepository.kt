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
2026-10-27  Sofia       1.01     added skeleton
2026-11-04  Sofia       1.02     added query get by id
2026-11-05  Sofia       1.03     added queries retrieve list and set by group of collection
2026-11-05  Sofia       1.04     added exception handle in lamba for all queries
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.CampsiteAvailability
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


    /**
     * Creates behinds a Campsite object and
     * add it to the firebase database with a random and unique id.
     *
     * @param campsiteId
     * @param startDate
     * @param endDate
     * @param status
     * @param result
     *      - The object was added successfully then it returns the id of that object
     *      - The object was not added not successfully so it returns null
     * @return nullable string
     */
    fun createCampsiteAvailability(
        campsiteId: String,
        startDate: String,
        endDate: String,
        status: String,
        result: (String?) -> Unit

    ){
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

        //Tasks.await(campsiteAvailabilityRef.set(campsiteAvailability))
        // save the object in the firebase
        campsiteAvailabilityRef.set(campsiteAvailability)
            .addOnSuccessListener {
                result(campsiteAvailabilityRef.id)
            }

            .addOnFailureListener {
                result(null)
            }

    }

    /**
     * Updates behinds a CampsiteAvailability object
     *
     * @param campsiteAvailability object
     * @param result lamba function that provide if:
     *      - successfully then it returns a boolean as true
     *      - not successfully so it returns a boolean as false
     * @return Boolean
     */

    fun updateCampsiteAvailability(
        campsiteAvailability: CampsiteAvailability,
        result: (Boolean) -> Unit
    ){
        val campsiteAvailabilityId = campsiteAvailability.getCampsiteAvailabilityId()
        val campsiteId = campsiteAvailability.getCampsiteId()


        val campsiteAvailabilityRef = db.collection("campsites/$campsiteId/availability")


        campsiteAvailabilityRef.document(campsiteAvailabilityId)
            .set(campsiteAvailability)
            .addOnSuccessListener {
            result(true)
        }
            .addOnFailureListener {
                result(false)
            }
    }


    /**
     * retrieves CampsiteAvailability object
     *
     * @param campsiteAvailabilityId unique identifier of the CampsiteAvailability within the database
     * @param campsiteId when it becomes available
     * @param result lamba function that provide if:
     *      - successfully then it returns the CampsiteAvailability object
     *      - not successfully so it returns null
     * @return nullable CampsiteAvailability object
     */
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
    /**
     * retrieves a List of CampsiteAvailability
     *
     * @param campsiteId unique identifier of the CampsiteAvailability within the database
     * @param result lamba function that provide if:
     *      - successfully then it list of CampsiteAvailability objects. NOTE: can be empty list
     *      - not successfully so it returns null
     * @return nullable CampsiteAvailability object
     */
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

