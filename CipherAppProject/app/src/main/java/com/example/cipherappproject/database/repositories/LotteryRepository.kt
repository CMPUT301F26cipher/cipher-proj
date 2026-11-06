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
2026-10-27  Sofia       1.01     added skeleton
2026-11-04  Sofia       1.02     added query get by id
2026-11-05  Sofia       1.03     added queries retrieve list and set by group of collection
2026-11-05  Sofia       1.04     added exception handle in lamba for all queries
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


    /**
     * Creates behinds a Lottery object and add it to the firebase database
     * with a random and unique id.
     *
     * @param ownerId unique identifier of the campsite within the database
     * @param campsiteIds
     * @param numWinners
     * @param startDate
     * @param endDate
     * @param status
     * @param createdAt
     * @param result lamba function that provide if:
     *      - The object was added successfully then it returns the id of that object
     *      - The object was not added not successfully so it returns null
     * @return nullable string with the id of the created object
     */
    fun createLottery(
        ownerId: String,
        campsiteIds: MutableList<String>,
        numWinners: Int,
        startDate: String,
        endDate: String,
        maxParticipants: Int,
        status: String,
        createdAt: String,
        result: (String?) -> Unit

    ) {
        // create a reference and id to the new profile
        val lotteryRef = lotteriesRef.document()

        // create lottery object
        val lottery = Lottery(
            lotteryId = lotteryRef.id,
            ownerId = ownerId,
            campsiteIds = campsiteIds,
            numWinners = numWinners,
            startDate = startDate,
            endDate = endDate,
            maxParticipants = maxParticipants,
            status = status,
            createdAt = createdAt
        )
        //Tasks.await(lotteryRef.set(lottery))
        // save the object in the firebase
        lotteryRef
            .set(lottery)
            .addOnSuccessListener {
                result(lotteryRef.id)
            }
            .addOnFailureListener {
                result(null)
            }

    }

    /**
     * Updates a Lottery object into the firebase database .
     *
     * @param lottery object
     * @param result lamba function that provide if:
     *      - successfully then it returns a boolean as true
     *      - not successfully so it returns a boolean as false
     * @return Boolean
     */
    fun updateLottery(
        lottery: Lottery,
        result: (Boolean) -> Unit
    ){
        lotteriesRef.document(lottery.getLotteryId())
            .set(lottery)
            .addOnSuccessListener {
                result(true)

            }
            .addOnFailureListener {
                result(false)
            }
    }

    /*
    fun getLotteriesByOwnerId(
        ownerId: String,
        result: (List<Lottery>?) -> Unit
    ){

    }*/

    /**
     * get one single Lottery object
     *
     * @param lotteryId
     * @param result lamba function that provide if:
     *      - successfully then it returns a Lottery
     *      - not successfully so it returns null
     * @return nullable Lottery
     */
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