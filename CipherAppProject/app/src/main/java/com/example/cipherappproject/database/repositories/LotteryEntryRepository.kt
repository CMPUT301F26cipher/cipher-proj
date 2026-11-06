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
2026-10-27  Sofia       1.01     added skeleton
2026-11-04  Sofia       1.02     added query get by id
2026-11-05  Sofia       1.03     added queries retrieve list and set by group of collection
2026-11-05  Sofia       1.04     added exception handle in lamba for all queries
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


    /**
     * Creates behinds a LotteryEntry object and add it to the firebase database
     * with a random and unique id.
     *
     * @param lotteryId unique identifier of the campsite within the database
     * @param userId single user entry
     * @param status store the lottery status
     * @param positionLottery user entry in the list after randomization
     * @param result lamba function that provide if:
     *      - The object was added successfully then it returns the id of that object
     *      - The object was not added not successfully so it returns null
     * @return nullable string
     */
    fun addLotteryEntry(
        lotteryId: String,
        userId: String,
        status: String,
        positionLottery: Int,
        result: (String?) -> Unit

    ){
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
        //Tasks.await(lotteryEntryRef.set(lotteryEntry))
        lotteryEntryRef
            .set(lotteryEntry)
            .addOnSuccessListener {
                result(lotteryEntryRef.id)
            }

            .addOnFailureListener {
                result(null)
            }
    }

    /**
     * Updates behinds a LotteryEntry object into the firebase database .
     *
     * @param lotteryEntry lotteryEntry object
     * @param result lamba function that provide if:
     *      - successfully then it returns a boolean as true
     *      - not successfully so it returns a boolean as false
     * @return nullable string
     */
    fun updateLotteryEntry(
        lotteryEntry: LotteryEntry,
        result: (Boolean) -> Unit
    ){
        val lotteryId = lotteryEntry.getLotteryId()
        val lotteryEntryId = lotteryEntry.getLotteryEntryId()

        val lotteryEntryRef = db.collection("lotteries/$lotteryId/entries")

        lotteryEntryRef.document(lotteryEntryId)
            .set(lotteryEntry)
            .addOnSuccessListener {
                result(true)
            }
            .addOnFailureListener {
                result(false)
            }
    }

    /*
    fun deleteLottery(lotteryEntryId: String){
        lotteriesEntryRef.document(lotteryEntryId).delete() // need to check if this also affects lotteries, reservations, availabity, iamges??? frick

    }
    */

    /**
     * retrieves LotteryEntry
     *
     * @param lotteryId
     * @param result lamba function that provide if:
     *      - successfully then it returns the Campsite object
     *      - not successfully so it returns null
     * @return nullable LotteryEntry object
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

    /**
     * retrieves LotteryEntry object by UserId
     *
     * @param lotteryId
     * @param userId
     * @param result lamba function that provide if:
     *      - successfully then it returns the Campsite object
     *      - not successfully so it returns null
     * @return nullable LotteryEntry object
     */
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

    /**
     * update a single LotteryEntry Status
     *
     * @param lotteryId
     * @param lotteryEntryId
     * @param newStatus
     * @param result lamba function that provide if:
     *      - successfully then it returns the Campsite object
     *      - not successfully so it returns null
     * @return nullable LotteryEntry object
     */
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