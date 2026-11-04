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
2026-02-24  Sofia      1.01     added skeleton
2026-03-22  Sofia      1.01     added query get by id
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Lottery
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


    fun createReservation(
        userId: String,
        campsiteId: String,
        lotteryId: String,
        status: String,
        createdAt: String

    ): String {
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

        // save the object in the firebase
        Tasks.await(reservationRef.set(reservation))

        return reservationRef.id


    }

    fun updateReservation(reservation: Reservation){
        reservationsRef.document(reservation.getReservationId()).set(reservation)
    }

    fun getReservationsByUser(){

    }

    fun getReservationByUser(){

    }

    fun changeReservationStatus(){

    }

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