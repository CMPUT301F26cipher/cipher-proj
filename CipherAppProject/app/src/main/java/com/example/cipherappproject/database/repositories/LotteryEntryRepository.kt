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
2026-03-27  Sofia      1.01     added skeleton
2026-04-10  Sofia      1.02     added query get by id
2026-04-11  Sofia      1.03     added queries retrieve list and set by group of collection
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
    private val lotteriesEntryRef = db.collectionGroup("entries")
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
        val lotteryEntryRef = db.collection("lotteries/$lotteryId/entries").document()

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

    fun updateLotteryEntry(lotteryEntry: LotteryEntry){
        val lotteryId = lotteryEntry.getLotteryEntryLotteryId()
        val lotteryEntryId = lotteryEntry.getLotteryEntryId()

        val lotteryEntryRef = db.collection("lotteries/$lotteryId/entries")

        lotteryEntryRef.document(lotteryEntryId).set(lotteryEntry)
    }

    /*
    fun deleteLottery(lotteryEntryId: String){
        lotteriesEntryRef.document(lotteryEntryId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }
    */

    fun getLotteryEntriesByLotteryId(
        lotteryId: String,
        result: (List<LotteryEntry>?) -> Unit
    ){
        val campsiteAvailabilityRef = db.collection("lotteries")
            .document(lotteryId)
            .collection("entries")
            .get()
            .addOnSuccessListener { documents ->

                val entries : MutableList<LotteryEntry> = mutableListOf()

                for (document in documents) {
                    val entry = document.toObject(LotteryEntry::class.java)

                    entries.add(entry)
                }

                result(entries)
            }
            .addOnFailureListener { exception ->
                result(null)
            }

    }

    fun getEntryForUser(
        lotteryId: String,
        userId: String,
        result: (LotteryEntry?) -> Unit
    ){
        db.collection("lotteries")
            .document(lotteryId)
            .collection("entries")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                result(documents.documents.firstOrNull()?.toObject(LotteryEntry::class.java))
            }
            .addOnFailureListener {
                result(null)
            }
    }

    fun updateEntryStatus(
        lotteryId: String,
        lotteryEntryId: String,
        newStatus: String,
        result: (Boolean) -> Unit
    ){
        db.collection("lotteries")
            .document(lotteryId)
            .collection("entries")
            .document(lotteryEntryId)
            .update("status", newStatus)
            .addOnSuccessListener { result(true) }
            .addOnFailureListener { result(false) }
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



}