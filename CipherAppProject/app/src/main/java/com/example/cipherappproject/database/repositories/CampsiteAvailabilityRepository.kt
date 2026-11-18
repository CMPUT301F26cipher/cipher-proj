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
2026-11-08  Sofia       1.07     added queries using the list. Modified added as a transaction query.
2026-11-12  Sofia       1.08     added state boolean for list local queries.
2026-11-15  Sofia       1.09     added states to the local list and changes init too
2026-11-15  Sofia       1.10     added custom exceptions
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.enums.CampsiteAvailabilityStatus
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.exceptions.DataConversionException
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.CampsiteAvailability
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate


/**
 * Manages Campsite Availability data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Campsite Availability documents directly from Firestore, as well as methods to search and filter from the local Campsite Availability list. This local list is inside a state.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

class CampsiteAvailabilityRepository (
    // to get all available dates to book
    private val reservationRepository: ReservationRepository
){

    private val db = Firebase.firestore

    // https://firebase.google.com/docs/firestore/pipelines/stages/input/collection-group
    private val campsitesAvailabilityRef = db.collectionGroup("availability")
    private val _allCampsiteAvailabilities = mutableStateOf<List<CampsiteAvailability>>(emptyList())

    val campsitesAvailability : State<List<CampsiteAvailability>>
        get() = _allCampsiteAvailabilities


    private val _isCampsiteAvailabilitiesLoaded = mutableStateOf(false)

    val isCampsiteAvailabilitiesLoaded: State<Boolean>
        get() = _isCampsiteAvailabilitiesLoaded

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
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */
    fun createCampsiteAvailability(
        campsiteId: String,
        startDate: String,
        endDate: String,
        status: String,
        result: (Result<String>) -> Unit
    ) {
        // create a reference to the parent campsite
        val campsiteRef = db.collection("campsites").document(campsiteId)

        db.runTransaction { transaction ->
            val campsiteSnapshot = transaction.get(campsiteRef)

            // check if the parent campsite exists
            if (!campsiteSnapshot.exists()) {
                throw NotFoundException("Campsite does not exist.")
            }

            val campsiteAvailabilityRef = campsiteRef
                .collection("availability")
                .document()

            // create object
            val campsiteAvailability = CampsiteAvailability(
                campsiteAvailabilityId = campsiteAvailabilityRef.id,
                campsiteId = campsiteId,
                startDate = startDate,
                endDate = endDate,
                status = status
            )

            // set availability
            transaction.set(campsiteAvailabilityRef, campsiteAvailability)

            // return id
            campsiteAvailabilityRef.id

        }
            .addOnSuccessListener { campsiteAvailabilityId -> // since is success return id
                result(Result.success(campsiteAvailabilityId))
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
                ValidationException("Campsite or campsiteAvailability ID cannot be empty.")
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
                throw NotFoundException("Campsite does not exist.")
            }


            val availabilitySnapshot = transaction.get(campsiteAvailabilityRef)

            if (!availabilitySnapshot.exists()) {
                throw NotFoundException("The availability for this campsite does not exist.")
            }

            // Check whether availability belongs to the specified campsite
            val storedCampsiteId = availabilitySnapshot.getString("campsiteId")

            // https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
            if (storedCampsiteId != campsiteId) {
                throw ValidationException("The availability does not belong to this campsite.")
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
     *      - Result.failure(exception) if not successful
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
                ValidationException(
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
                        NotFoundException(
                            "Campsite availability does not exist."
                        )
                    ))
                    return@addOnSuccessListener
                }

                val campsiteAvailability = document.toObject(CampsiteAvailability::class.java)

                // Check whether conversion was successful
                if (campsiteAvailability == null) {
                    result(Result.failure(
                        DataConversionException(
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
                ValidationException("Campsite ID cannot be empty.")
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
                        NotFoundException("Campsite does not exist.")
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
    /**
     * Get a CampsiteAvailability object
     *
     * @param campsiteId String
     * @param campsiteAvailabilityId String
     * @return CampsiteAvailability? --> null if not match found
     */
    fun getCampsiteAvailabilityFromListById(campsiteId: String, campsiteAvailabilityId: String): CampsiteAvailability? {
        return _allCampsiteAvailabilities.value.find {

            it.campsiteId == campsiteId && it.campsiteAvailabilityId == campsiteAvailabilityId
        }
    }


    /**
     * Get a list of CampsiteAvailability objects
     *
     * @param campsiteId String
     * @return List<CampsiteAvailability> --> can be empty list if not match found
     */
    fun getCampsiteAvailabilitiesFromListByCampsiteId(campsiteId: String): List<CampsiteAvailability> {

        return _allCampsiteAvailabilities.value.filter {
            it.campsiteId == campsiteId
        }
    }



    /**
     * Get a list of CampsiteAvailability objects
     *
     * @param date LocalDate
     * @return List<CampsiteAvailability> --> can be empty list if not match found
     */
    fun getCampsiteAvailabilitiesFromListActiveOnDate( date: LocalDate): List<CampsiteAvailability> {

        return _allCampsiteAvailabilities.value.filter { availability ->
            val startDate = LocalDate.parse(availability.startDate)
            val endDate = LocalDate.parse(availability.endDate)

            date in startDate..endDate
        }
    }


    /**
     * Get a list of CampsiteAvailability objects
     *
     * @param status String
     * @return List<CampsiteAvailability> --> can be empty list if not match found
     */
    fun getCampsiteAvailabilitiesFromListByStatus(status: String): List<CampsiteAvailability> {

        return _allCampsiteAvailabilities.value.filter {
            it.status == status
        }
    }


    /**
     * Get a list of CampsiteAvailability objects
     *
     * @param campsiteId String
     * @param status String
     *
     * @return List<CampsiteAvailability> --> can be empty list if not match found
     */
    fun getCampsiteAvailabilitiesFromListByCampsiteIdAndStatus(campsiteId: String, status: String): List<CampsiteAvailability> {

        return _allCampsiteAvailabilities.value.filter {
            it.campsiteId == campsiteId && it.status == status
        }
    }

    /**
     * Get a list of LocalDate
     *
     * This function assume the reservation are inclusive start date and end date.
     * It also assumes that the user can only book linear dates so that there is not
     * dates far from each other.
     *
     * @param campsiteId String
     *
     * @return List<LocalDate> --> can be empty list if not match found
     */

    /*Taken and learn from OpenAI, ChatGPT on 2026-11-15 on "How to get all the available dates within a range but as a list? because there can be free dates in between"*/
    fun getAvailableBookingDates(campsiteId: String): List<LocalDate> {

        // Check if both repositories have finished loading
        if (!_isCampsiteAvailabilitiesLoaded.value ||
            !reservationRepository.isReservationsLoaded.value
        ) {
            return emptyList()
        }

        // Get active availability periods for this campsite
        val availabilities = _allCampsiteAvailabilities.value.filter {
            it.campsiteId == campsiteId && it.status == CampsiteAvailabilityStatus.ACTIVE.name
        }

        // Get reservations that block dates
        val reservations = reservationRepository.getReservationsFromListByCampsiteId(campsiteId).filter {
            it.status == ReservationStatus.ACTIVE.name || it.status == ReservationStatus.PENDING.name
        }

        // Expand availability periods into individual dates
        val availableDates = availabilities.flatMap { availability ->
            val startDate = LocalDate.parse(availability.startDate)
            val endDate = LocalDate.parse(availability.endDate)

            generateSequence(startDate) { currentDate ->
                if (currentDate < endDate) {
                    currentDate.plusDays(1)
                } else {
                    null
                }
            }.toList()
        }

        // Expand reservation periods into individual dates
        val reservedDates = reservations.flatMap { reservation ->
            val startDate = LocalDate.parse(reservation.bookStartDate)
            val endDate = LocalDate.parse(reservation.bookEndDate)

            generateSequence(startDate) { currentDate ->
                if (currentDate < endDate) {
                    currentDate.plusDays(1)
                } else {
                    null
                }
            }.toList()
        }

        // Remove reserved dates and duplicate dates
        return availableDates
            .distinct()
            .filter { it !in reservedDates }
            .sorted()
    }


    init {
        campsitesAvailabilityRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val updatedCampsiteAvailabilities = snapshot.documents.mapNotNull { document ->
                document.toObject(CampsiteAvailability::class.java)
            }

            _allCampsiteAvailabilities.value = updatedCampsiteAvailabilities

            _isCampsiteAvailabilitiesLoaded.value = true
        }
    }

}

