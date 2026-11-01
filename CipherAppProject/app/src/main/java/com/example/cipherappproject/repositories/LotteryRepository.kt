package com.example.cipherappproject.repositories

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Lottery
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LotteryRepository {

    private val db = Firebase.firestore
    private val lotteriesRef = db.collection("lotteries")
    private val _lotteries = mutableStateListOf<Lottery>()


    val lotteries: List<Lottery>
        get() = _lotteries

    fun createLottery(
        ownerId: String,
        campsiteIds: List<String>,
        numWinner: Int,
        startDate: String,
        endDate: String,
        maxParticipants: Int,
        status: String,
        createdAt: String

    ) {
        // create a reference and id to the new profile
        val lotteryRef = lotteriesRef.document()

        // create lottery object
        val lottery = Lottery(
            lotteryId = lotteryRef.id,
            ownerId = ownerId,
            campsiteIds = campsiteIds,
            numWinner = numWinner,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )

        // save the object in the firebase
        lotteryRef.set(lottery)
    }

    fun updateLottery(lottery: Lottery){
        lotteriesRef.document(lottery.lotteryId).set(lottery)
    }


    fun deleteLottery(lotteryId: String){
        lotteriesRef.document(lotteryId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }

    init {
        lotteriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _lotteries.clear()

            snapshot?.documents?.forEach { document ->
                val lottery = document.toObject(Lottery::class.java) // pass from firestore to kotlin object

                if (lottery != null) {
                    _lotteries.add(lottery)
                }
            }
        }
    }
}