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
2026-11-06  Sofia       1.05     fixed the get and set methods for the objects
2026-11-08  Sofia       1.06     added exceptions and error handling
2026-11-08  Sofia       1.07     added queries using the list
2026-11-12  Sofia       1.08     added state boolean for list local queries.
2026-11-15  Sofia      1.09     added states to the local list and changes init too
--------------------------------------------------------------------------------------
Future Enhancements
NA
*/

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Notification
import com.example.cipherappproject.database.models.Profile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate

/**
 * Manages Lottery Entry data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Lottery Entry documents directly from Firestore, as well as methods to search and filter from the local Lottery Entry list. This local list is inside a state.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */


class LotteryEntryRepository {
    private val db = Firebase.firestore
    private val lotteriesEntryRef = db.collectionGroup("entries")
    private val _allLotteriesEntries = mutableStateOf<List<LotteryEntry>>(emptyList())


    val lotteries: State<List<LotteryEntry>>
        get() = _allLotteriesEntries


    private val _isLotteriesEntriesLoaded = mutableStateOf(false)

    val isLotteriesEntriesLoaded: State<Boolean>
        get() = _isLotteriesEntriesLoaded

    // ----------------------------------- Queries -----------------------------------


    /**
     * Creates behinds a LotteryEntry object and add it to the firebase database
     * with a random and unique id.
     *
     * @param lotteryId unique identifier of the campsite within the database
     * @param userId single user entry
     * @param status store the lottery status
     * @param positionLottery user entry in the list after randomization
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */
    fun addLotteryEntry(
        lotteryId: String,
        userId: String,
        status: String,
        positionLottery: Int,
        result: (Result<String>) -> Unit
    ) {
        // Create a reference to the parent lottery
        val lotteryRef = db.collection("lotteries").document(lotteryId)


        db.runTransaction { transaction ->
            val lotterySnapshot = transaction.get(lotteryRef)

            // Check if the parent lottery exists
            if (!lotterySnapshot.exists()) {
                throw NoSuchElementException("Lottery does not exist.")
            }

            val lotteryEntryRef = lotteryRef
                .collection("entries")
                .document()

            // Create object
            val lotteryEntry = LotteryEntry(
                lotteryEntryId = lotteryEntryRef.id,
                lotteryId = lotteryId,
                userId = userId,
                status = status,
                positionLottery = positionLottery
            )

            // set entry
            transaction.set(lotteryEntryRef, lotteryEntry)

            // return id
            lotteryEntryRef.id

        }
            .addOnSuccessListener { lotteryEntryId -> // since is success return id
                result(Result.success(lotteryEntryId))
        }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
        }
    }

    /**
     * Updates behinds a LotteryEntry object into the firebase database .
     *
     * @param lotteryEntry lotteryEntry object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateLotteryEntry(
        lotteryEntry: LotteryEntry,
        result: (Result<Unit>) -> Unit
    ) {
        val lotteryEntryId = lotteryEntry.lotteryEntryId
        val lotteryId = lotteryEntry.lotteryId

        // Check IDs
        if (lotteryEntryId.isBlank() || lotteryId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Lottery or lottery entry ID cannot be empty.")
            ))
            return
        }

        val lotteryRef = db.collection("lotteries").document(lotteryId)

        val lotteryEntryRef = lotteryRef
            .collection("entries")
            .document(lotteryEntryId)

        // Check lottery and entry before updating
        db.runTransaction { transaction ->

            val lotterySnapshot = transaction.get(lotteryRef)

            // Check whether lottery exists
            if (!lotterySnapshot.exists()) {
                throw NoSuchElementException("Lottery does not exist.")
            }

            val entrySnapshot = transaction.get(lotteryEntryRef)

            // Check whether lottery entry exists
            if (!entrySnapshot.exists()) {
                throw NoSuchElementException("Lottery entry does not exist.")
            }

            // Check whether entry belongs to the specified lottery
            val storedLotteryId = entrySnapshot.getString("lotteryId")

            if (storedLotteryId != lotteryId) {
                throw IllegalArgumentException(
                    "The lottery entry does not belong to this lottery."
                )
            }

            // Update lottery entry
            transaction.set(lotteryEntryRef, lotteryEntry)

            // We only care whether the operation succeeded
            Unit

        }
            .addOnSuccessListener {
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
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
     * @param result lamba function that has Result and if:
     *      - Result.success(<List<LotteryEntry>) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<List<LotteryEntry>>
     */
    fun getLotteryEntriesByLotteryId(
        lotteryId: String,
        result: (Result<List<LotteryEntry>>) -> Unit
    ) {
        // Check id
        if (lotteryId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Lottery ID cannot be empty.")
            ))
            return
        }

        val lotteryRef = db
            .collection("lotteries")
            .document(lotteryId)

        // Check whether lottery exists
        lotteryRef.get()
            .addOnSuccessListener { lotteryDocument ->

                if (!lotteryDocument.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Lottery does not exist.")
                    ))
                    return@addOnSuccessListener
                }

                // Retrieve all lottery entries
                lotteryRef
                    .collection("entries")
                    .get()
                    .addOnSuccessListener { documents ->

                        val lotteryEntries: MutableList<LotteryEntry> =
                            mutableListOf()

                        for (document in documents) {

                            val lotteryEntry =
                                document.toObject(LotteryEntry::class.java)

                            lotteryEntries.add(lotteryEntry)
                        }

                        result(Result.success(lotteryEntries))
                    }
                    .addOnFailureListener { exception ->
                        result(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    /*

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

     */


    // ----------------------------------- From list -----------------------------------
    /**
     * Get a LotteryEntry object
     *
     * @param lotteryId String
     * @param lotteryEntryId String
     * @return LotteryEntry? --> null if not match found
     */

    fun getLotteryEntryFromListById(lotteryId: String, lotteryEntryId: String): LotteryEntry? {

        return _allLotteriesEntries.value.find {

            it.lotteryId == lotteryId && it.lotteryEntryId == lotteryEntryId
        }
    }

    /**
     * Get a list of LotteryEntry objects
     *
     * @param lotteryId String
     * @return List<LotteryEntry> --> can be empty list if not match found
     */
    fun getLotteryEntriesFromListByLotteryId(lotteryId: String): List<LotteryEntry> {

        return _allLotteriesEntries.value.filter {
            it.lotteryId == lotteryId
        }
    }


    /**
     * Get a list of LotteryEntry objects
     *
     * @param userId String
     * @return List<LotteryEntry> --> can be empty list if not match found
     */
    fun getLotteryEntriesFromListByUserId(userId: String): List<LotteryEntry> {

        return _allLotteriesEntries.value.filter {
            it.userId == userId
        }
    }


    /**
     * Get a list of LotteryEntry objects
     *
     * @param status String
     * @return List<LotteryEntry> --> can be empty list if not match found
     */
    fun getLotteryEntriesFromListByStatus(status: String): List<LotteryEntry> {

        return _allLotteriesEntries.value.filter {
            it.status == status
        }
    }


    /**
     * Get a LotteryEntry object
     *
     * @param lotteryId String
     * @param userId String
     * @return LotteryEntry? --> null if not match found
     */
    fun getLotteryEntryFromListByLotteryIdAndUserId(lotteryId: String, userId: String): LotteryEntry? {

        return _allLotteriesEntries.value.find {
            it.lotteryId == lotteryId && it.userId == userId
        }
    }

    /**
     * Get a list of LotteryEntry objects
     *
     * @param lotteryId String
     * @param status String
     * @return List<LotteryEntry> --> can be empty list if not match found
     */
    fun getLotteryEntriesFromListByLotteryIdAndStatus(lotteryId: String,status: String): List<LotteryEntry> {
        return _allLotteriesEntries.value.filter {

            it.lotteryId == lotteryId && it.status == status
        }
    }


    /**
     * Get a LotteryEntry object
     *
     * @param lotteryId String
     * @param position Int
     * @return LotteryEntry? --> null if not match found
     */
    fun getLotteryEntryFromListByLotteryIdAndPosition(lotteryId: String, position: Int): LotteryEntry? {
        return _allLotteriesEntries.value.find {
            it.lotteryId == lotteryId && it.positionLottery == position
        }
    }


    /**
     * Get a LotteryEntry object
     *
     * @param lotteryId String
     * @return LotteryEntry? --> null if not match found
     */

    // https://www.baeldung.com/kotlin/sort
    fun getLotteryEntriesFromListByLotteryIdOrderedByPosition(lotteryId: String): List<LotteryEntry> {
        return _allLotteriesEntries.value
            .filter { it.lotteryId == lotteryId }

            /*Taken and learn from OpenAI, ChatGPT on 2026-11-09 on "How can we handle null values in a list"*/
            // if us not null use that positionLottery, else if it null use the Int.MAX_VALUE instead
            .sortedBy { it.positionLottery ?: Int.MAX_VALUE }
            //.sortedBy { it.positionLottery }
    }




    init {
        lotteriesEntryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val updatedLotteriesEntries = snapshot.documents.mapNotNull { document ->
                document.toObject(LotteryEntry::class.java)
            }
            _allLotteriesEntries.value = updatedLotteriesEntries

            _isLotteriesEntriesLoaded.value = true
        }
    }

}