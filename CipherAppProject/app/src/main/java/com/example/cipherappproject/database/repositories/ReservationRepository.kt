package com.example.cipherappproject.database.repositories

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.Reservation
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ReservationRepository {
    private val db = Firebase.firestore
    private val reservationsRef = db.collection("reservations")
    private val _reservations = mutableStateListOf<Reservation>()


    val reservations: List<Reservation>
        get() = _reservations


    fun createReservation(
        userId: String,
        campsiteId: String,
        lotteryId: String,
        status: String,
        createdAt: String

    ) {
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
        reservationRef.set(reservation)
    }

    fun updateReservation(reservation: Reservation){
        reservationsRef.document(reservation.reservationId).set(reservation)
    }

    fun getReservationsByUser(){

    }

    fun getReservationByUser(){

    }

    fun changeReservationStatus(){

    }


    init {
        reservationsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _reservations.clear()

            snapshot?.documents?.forEach { document ->
                val reservation = document.toObject(Reservation::class.java) // pass from firestore to kotlin object

                if (reservation != null) {
                    _reservations.add(reservation)
                }
            }
        }
    }

}