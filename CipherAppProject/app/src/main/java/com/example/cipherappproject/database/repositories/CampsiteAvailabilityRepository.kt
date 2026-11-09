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
2026-11-06  Sofia       1.05     fixed the get and set methods for the objects
2026-11-08  Sofia       1.06     added exceptions and error handling
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Campsite
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


    // ----------------------------------- Queries -----------------------------------


    /**
     * Creates behinds a Campsite object and
     * add it to the firebase database with a random and unique id.
     *
     * @param campsiteId
     * @param startDate
     * @param endDate
     * @param status
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.success(exception) if not successful
     * @return Result<String>
     */
    fun createCampsiteAvailability(
        campsiteId: String,
        startDate: String,
        endDate: String,
        status: String,
        result: (Result<String>) -> Unit

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
                result(Result.success(campsiteAvailabilityRef.id))
            }

            .addOnFailureListener { exception ->

                result(Result.failure(exception))

            }

    }

    /**
     * Updates behinds a CampsiteAvailability object
     *
     * @param campsiteAvailability object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */

    fun updateCampsiteAvailability(
        campsiteAvailability: CampsiteAvailability,
        result: (Result<Unit>) -> Unit
    ){

        val campsiteAvailabilityId = campsiteAvailability.campsiteAvailabilityId
        val campsiteId = campsiteAvailability.campsiteId

        //https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
        if (campsiteAvailabilityId.isBlank() || campsiteId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Campsite or campsiteAvailability ID cannot be empty.")
            ))
            return
        }

        val campsiteRef = db.collection("campsites").document(campsiteId)

        val campsiteAvailabilityRef = campsiteRef
            .collection("availability")
            .document(campsiteAvailabilityId)


        db.runTransaction { transaction ->
            val campsiteSnapshot = transaction.get(campsiteRef)

            // if object do not exist
            if (!campsiteSnapshot.exists()) {
                throw NoSuchElementException("Campsite does not exist.")
            }


            val availabilitySnapshot = transaction.get(campsiteAvailabilityRef)

            if (!availabilitySnapshot.exists()) {
                throw NoSuchElementException(
                    "The availability for this campsite does not exist."
                )
            }

            // Check whether availability belongs to the specified campsite
            val storedCampsiteId = availabilitySnapshot.getString("campsiteId")

            // https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
            if (storedCampsiteId != campsiteId) {
                throw IllegalArgumentException(
                    "The availability does not belong to this campsite."
                )
            }

            // Update availability
            transaction.set(campsiteAvailabilityRef, campsiteAvailability)

            // since we do not care about get an object but just success or not
            Unit

        }
            .addOnSuccessListener {
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))

            }
    }


    /**
     * retrieves CampsiteAvailability object
     *
     * @param campsiteAvailabilityId unique identifier of the CampsiteAvailability within the database
     * @param campsiteId when it becomes available
     * @param result lamba function that has Result and if:
     *      - Result.success(CampsiteAvailability) if successful
     *      - Result.success(exception) if not successful
     * @return Result<CampsiteAvailability>
     */
    fun getCampsiteAvailabilityById(
        campsiteAvailabilityId: String,
        campsiteId: String,
        result: (Result<CampsiteAvailability>) -> Unit
    ){
        // Check IDs
        if (campsiteAvailabilityId.isBlank() || campsiteId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException(
                    "Campsite or campsiteAvailability ID cannot be empty."
                )
            ))
            return
        }

        val campsiteAvailabilityRef = db
            .collection("campsites")
            .document(campsiteId)
            .collection("availability")
            .document(campsiteAvailabilityId)

        campsiteAvailabilityRef
            .get()
            .addOnSuccessListener { document ->

                // Check whether availability exists
                if (!document.exists()) {
                    result(Result.failure(
                        NoSuchElementException(
                            "Campsite availability does not exist."
                        )
                    ))
                    return@addOnSuccessListener
                }

                val campsiteAvailability =
                    document.toObject(CampsiteAvailability::class.java)

                // Check whether conversion was successful
                if (campsiteAvailability == null) {
                    result(Result.failure(
                        IllegalStateException(
                            "Failed to convert document to CampsiteAvailability."
                        )
                    ))
                    return@addOnSuccessListener
                }

                result(Result.success(campsiteAvailability))
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }

    /**
     * retrieves a List of CampsiteAvailability
     *
     * @param campsiteId unique identifier of the CampsiteAvailability within the database
     * @param result lamba function that has Result and if:
     *      - Result.success(List<CampsiteAvailability>) if successful
     *      - Result.success(exception) if not successful
     * @return Result<List<CampsiteAvailability>>
     */
    fun getAvailabilitiesByCampsiteId(
        campsiteId: String,
        result: (Result<List<CampsiteAvailability>>) -> Unit
    ) {

        // Check campsite ID
        if (campsiteId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Campsite ID cannot be empty.")
            ))
            return
        }

        val campsiteRef = db
            .collection("campsites")
            .document(campsiteId)

        // Check whether campsite exists
        campsiteRef.get()
            .addOnSuccessListener { campsiteDocument ->

                if (!campsiteDocument.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Campsite does not exist.")
                    ))
                    return@addOnSuccessListener
                }

                // Retrieve all availabilities
                campsiteRef
                    .collection("availability")
                    .get()
                    .addOnSuccessListener { documents ->

                        val availabilities: MutableList<CampsiteAvailability> =
                            mutableListOf()

                        for (document in documents) {
                            val campsiteAvailability =
                                document.toObject(CampsiteAvailability::class.java)

                            availabilities.add(campsiteAvailability)
                        }

                        result(Result.success(availabilities))
                    }
                    .addOnFailureListener { exception ->
                        result(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }



    // ----------------------------------- From list -----------------------------------


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

