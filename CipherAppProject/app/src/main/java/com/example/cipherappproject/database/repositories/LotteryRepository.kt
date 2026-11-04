package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
Program Name: LotteryRepository.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for LotteryRepository
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
import com.example.cipherappproject.database.models.Lottery
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LotteryRepository {

    private val db = Firebase.firestore
    private val lotteriesRef = db.collection("lotteries")
    private val _allLotteries = mutableStateListOf<Lottery>()


    val lotteries: List<Lottery>
        get() = _allLotteries

    fun createLottery(
        ownerId: String,
        campsiteIds: MutableList<String>,
        numWinner: Int,
        startDate: String,
        endDate: String,
        maxParticipants: Int,
        status: String,
        createdAt: String

    ): String {
        // create a reference and id to the new profile
        val lotteryRef = lotteriesRef.document()

        // create lottery object
        val lottery = Lottery(
            lotteryId = lotteryRef.id,
            ownerId = ownerId,
            campsiteIds = campsiteIds,
            numWinners = numWinner,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )

        // save the object in the firebase
        Tasks.await(lotteryRef.set(lottery))

        return lotteryRef.id

    }

    fun updateLottery(lottery: Lottery){
        lotteriesRef.document(lottery.getLotteryId()).set(lottery)
    }


    fun deleteLottery(lotteryId: String){
        lotteriesRef.document(lotteryId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }

    init {
        lotteriesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allLotteries.clear()

            snapshot?.documents?.forEach { document ->
                val lottery = document.toObject(Lottery::class.java) // pass from firestore to kotlin object

                if (lottery != null) {
                    _allLotteries.add(lottery)
                }
            }
        }
    }
}