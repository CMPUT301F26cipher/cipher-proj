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
2026-03-27  Sofia      1.01     added skeleton
2026-04-10  Sofia      1.02     added query get by id
2026-04-11  Sofia      1.03     added queries retrieve list and set by group of collection
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
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
            numWinner = numWinner,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )

        // save the object in the firebase
        lotteryRef.set(lottery)
        //Tasks.await(lotteryRef.set(lottery))

        return lotteryRef.id

    }

    fun updateLottery(lottery: Lottery){
        lotteriesRef.document(lottery.lotteryId).set(lottery)
    }

    fun getLotteriesByOwnerId(
        ownerId: String,
        result: (List<Lottery>?) -> Unit
    ){

    }

    /*
    fun deleteLottery(lotteryId: String){
        lotteriesRef.document(lotteryId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }*/

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