package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
     Program Name: LotteryEntryRepository.kt
	 Date Written: October 27, 2026
	       Author: Sofia Arturo Gomez
          Purpose: Manage database methods for LotteryEntryRepository
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
import com.example.cipherappproject.database.models.LotteryEntry
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class LotteryEntryRepository {
    private val db = Firebase.firestore
    private val lotteriesEntryRef = db.collection("entries")
    private val _allLotteriesEntries = mutableStateListOf<LotteryEntry>()


    val lotteries: List<LotteryEntry>
        get() = _allLotteriesEntries

    fun addLotteryEntry(
        lotteryId: String,
        userId: String,
        status: String,
        positionLottery: Int,

    ) : String {
        // create a reference and id to the new profile
        val lotteryEntryRef = lotteriesEntryRef.document()

        // create lottery object
        val lotteryEntry = LotteryEntry(
            lotteryEntryId = lotteryEntryRef.id,
            lotteryId = lotteryId,
            userId = userId,
            status = status,
            positionLottery = positionLottery,
        )

        // save the object in the firebase
        Tasks.await(lotteryEntryRef.set(lotteryEntry))

        return lotteryEntryRef.id

    }

    fun updateLottery(lotteryEntry: LotteryEntry){
        lotteriesEntryRef.document(lotteryEntry.getLotteryEntryId()).set(lotteryEntry)
    }


    fun deleteLottery(lotteryEntryId: String){
        lotteriesEntryRef.document(lotteryEntryId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }

    init {
        lotteriesEntryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allLotteriesEntries.clear()

            snapshot?.documents?.forEach { document ->
                val lotteryEntry = document.toObject(LotteryEntry::class.java) // pass from firestore to kotlin object

                if (lotteryEntry != null) {
                    _allLotteriesEntries.add(lotteryEntry)
                }
            }
        }
    }

    fun addLotteryEntry(){ // needs to make sure is only once and add 1 only
        // lotteries/lotteryId/entries/userId



    }

    // update each

    fun getLotteriesByUser(){

    }

}