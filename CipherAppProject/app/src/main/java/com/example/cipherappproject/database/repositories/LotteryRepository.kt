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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source

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
        Verifies that selected campsites exist and belong to the owner, and then saves the lottery with a generated document ID

        Report confirmed save success or an ownership/database error through callbacks. 

     */
    fun saveNewLottery( // THis function verifies campsite ownership and saves a new lottery. 
        lottery: Lottery,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (lottery.ownerId.isBlank()) {
            onFailure("An owner is required.")
            return
        }

        val campsiteIds = lottery.campsiteIds.distinct()

        if (campsiteIds.isEmpty() || campsiteIds.any { it.isBlank() }) {
            onFailure("Select at least one campsite.")
            return
        }

        val campsiteReads = campsiteIds.map { campsiteId ->
            db.collection("campsites")
                .document(campsiteId)
                .get(Source.SERVER)
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(campsiteReads)
            .addOnSuccessListener { documents ->
                val ownsEveryCampsite = documents.all { document ->
                    document.exists() &&
                        document.getString("ownerId") == lottery.ownerId
                }

                if (!ownsEveryCampsite) {
                    onFailure("You can only list campsites you own.")
                } else {
                    val reference = lotteriesRef.document()

                    val savedLottery = lottery.copy(
                        lotteryId = reference.id,
                        campsiteIds = campsiteIds
                    )

                    reference.set(savedLottery)
                        .addOnSuccessListener {
                            onSuccess(reference.id)
                        }
                        .addOnFailureListener {
                            onFailure("Could not save lottery. Please try again.")
                        }
                }
            }
            .addOnFailureListener {
                onFailure("Could not verify the campsite ownership. Check your connection.")
            }
    }


    // Loads one lottery
    fun getLotteryById(
        lotteryId: String,
        result: (Lottery?) -> Unit
    ){
        lotteriesRef
            .document(lotteryId)
            .get()
            .addOnSuccessListener { document ->
                result(document.toObject(Lottery::class.java))
            }
            .addOnFailureListener {
                result(null)
            }
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