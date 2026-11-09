package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
Program Name: ReservationRepository.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for ReservationRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-03-27  Sofia      1.01     added skeleton
2026-04-10  Sofia      1.02     added query get by id
2026-04-11  Sofia      1.03     added queries retrieve list and set by group of collection
2026-11-06  Sofia      1.04     delete getters and setters. Set the attributes to public.
2026-11-08  Sofia      1.06     added exceptions and error handling
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.Profile
import com.example.cipherappproject.database.models.Reservation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ReservationRepository {
    private val db = Firebase.firestore
    private val reservationsRef = db.collection("reservations")
    private val _allReservations = mutableStateListOf<Reservation>()


    val reservations: List<Reservation>
        get() = _allReservations

    // ----------------------------------- Queries -----------------------------------


    /**
     * Creates behinds a Reservation object and add it to the firebase
     * database with a random and unique id.
     *
     * @param userId
     * @param campsiteId
     * @param lotteryId
     * @param status
     * @param createdAt
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.success(exception) if not successful
     * @return Result<String>
     */
    fun createReservation(
        userId: String,
        campsiteId: String,
        lotteryId: String,
        status: String,
        createdAt: String,
        result: (Result<String>) -> Unit

    ){
        // create a reference and id to the new document
        val reservationRef = reservationsRef.document()

        // create object
        val reservation = Reservation(
            reservationId = reservationRef.id,
            userId = userId,
            campsiteId = campsiteId,
            lotteryId = lotteryId,
            status = status,
            createdAt = createdAt

        )

        //Tasks.await(reservationRef.set(reservation))
        // save the object in the firebase
        reservationRef
            .set(reservation)
            .addOnSuccessListener {
                result(Result.success(reservationRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    /**
     * Updates a Reservation object
     *
     * @param reservation object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateReservation(
        reservation: Reservation,
        result: (Result<Unit>) -> Unit

    ){

        if (reservation.reservationId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Reservation ID cannot be empty.")
            ))
            return
        }

        val reservationRef = reservationsRef.document(reservation.reservationId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(reservationRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NoSuchElementException("reservation does not exist.")
            }

            transaction.set(reservationRef, reservation)

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

    /*
    fun getReservationsByUserId(
        userId: String,
        result: (List<Reservation?>) -> Unit

    ){

    }*/


    /**
     * get one single Reservation object
     *
     * @param reservationId
     * @param result lamba function that has Result and if:
     *      - Result.success(Reservation) if successful
     *      - Result.success(exception) if not successful
     * @return Result<Reservation>
     */
    fun getReservationById(
        reservationId: String,
        result: (Result<Reservation>) -> Unit

    ){
        if (reservationId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Reservation ID cannot be empty.")
            ))
            return
        }

        reservationsRef
            .document(reservationId)
            .get()
            .addOnSuccessListener { document ->

                // if the document do not exist --> Reservation do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Reservation does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val reservation = document.toObject(Reservation::class.java)

                // for testing
                Log.d("ReservationTest", "Raw data: ${document.data}")
                Log.d("ReservationTest", "Mapped object: $reservation")

                // if conversion was successful
                if (reservation == null) {
                    result(Result.failure(
                        IllegalStateException("Failed to convert document to Reservation.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved campsite
                result(Result.success(reservation))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }


    // ----------------------------------- From list -----------------------------------



    init {
        reservationsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allReservations.clear()

            snapshot?.documents?.forEach { document ->
                val reservation = document.toObject(Reservation::class.java) // pass from firestore to kotlin object

                if (reservation != null) {
                    _allReservations.add(reservation)
                }
            }
        }
    }

}