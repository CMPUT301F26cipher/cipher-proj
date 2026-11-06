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
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

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
     *      - The object was added successfully then it returns the id of that object
     *      - The object was not added not successfully so it returns null
     * @return nullable string with the id of the created object
     */
    fun createReservation(
        userId: String,
        campsiteId: String,
        lotteryId: String,
        status: String,
        createdAt: String,
        result: (String?) -> Unit

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
                result(reservationRef.id)
            }
            .addOnFailureListener {
                result(null)
            }



    }

    /**
     * Updates a Reservation object
     *
     * @param reservation object
     * @param result lamba function that provide if:
     *      - successfully then it returns a boolean as true
     *      - not successfully so it returns a boolean as false
     * @return Boolean
     */
    fun updateReservation(
        reservation: Reservation,
        result: (Boolean) -> Unit

    ){
        reservationsRef.document(reservation.getReservationId())
            .set(reservation)
            .addOnSuccessListener {
                result(true)
            }
            .addOnFailureListener {
                result(false)
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
     * @param result lamba function that provide if:
     *      - successfully then it returns a Reservation
     *      - not successfully so it returns null
     * @return nullable Reservation
     */
    fun getReservationById(
        reservationId: String,
        result: (Reservation?) -> Unit
    ){
        reservationsRef
            .document(reservationId)
            .get()
            .addOnSuccessListener { document ->

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val reservation = document.toObject(Reservation::class.java)

                // to avoid :type use lamba
                result(reservation)

            }
            .addOnFailureListener { exception ->
                result(null)
            }
    }


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