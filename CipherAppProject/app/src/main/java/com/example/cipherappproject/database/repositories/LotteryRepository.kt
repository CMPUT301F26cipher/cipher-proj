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
2026-05-11  Justin     1.04     added a validation step as it verifies campsite ownership and saves lottery settings.
2026-11-06  Sofia      1.05     fixed the get and set methods for the objects
2026-11-08  Sofia      1.06     added exceptions and error handling
2026-11-08  Sofia      1.07     added queries using the list
2026-11-12  Sofia      1.08     deleted created at and added state boolean for list local queries.
2026-11-15  Sofia      1.09     added custom exceptions
--------------------------------------------------------------------------------------
Future Enhancements
More secure measures/validation if needed????
 */

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.exceptions.DataConversionException
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Lottery
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import java.time.LocalDate

/**
 * Manages Lottery data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Lottery Availability documents directly from Firestore, as well as methods to search and filter from the local Lottery Availability list. This local list is inside a state.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

class LotteryRepository {

    private val db = Firebase.firestore
    private val lotteriesRef = db.collection("lotteries")
    private val _allLotteries = mutableStateOf<List<Lottery>>(emptyList())


    val lotteries: State<List<Lottery>>
        get() = _allLotteries

    private val _isLotteriesLoaded = mutableStateOf(false)

    val isLotteriesLoaded: State<Boolean>
        get() = _isLotteriesLoaded

    // ----------------------------------- Queries -----------------------------------

    /**
     * Creates behinds a Lottery object and add it to the firebase database
     * with a random and unique id.
     *
     * @param ownerId unique identifier of the campsite within the database
     * @param campsiteIds
     * @param numWinners
     * @param startDate
     * @param endDate
     * @param maxParticipants
     * @param status
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.success(exception) if not successful
     * @return Result<String>
     */
    fun createLottery(
        ownerId: String,
        campsiteIds: MutableList<String>,
        numWinners: Int,
        startDate: String,
        endDate: String,
        maxParticipants: Int,
        status: String,
        result: (Result<String>) -> Unit

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
        )
        //Tasks.await(lotteryRef.set(lottery))
        // save the object in the firebase
        lotteryRef
            .set(lottery)
            .addOnSuccessListener {
                result(Result.success(lotteryRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }

    /**
     * Updates a Lottery object into the firebase database .
     *
     * @param lottery object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateLottery(
        lottery: Lottery,
        result: (Result<Unit>) -> Unit
    ){

        //https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
        if (lottery.lotteryId.isBlank()) {
            result(Result.failure(
                ValidationException("Lottery ID cannot be empty.")
            ))
            return
        }

        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val lotteryRef = lotteriesRef.document(lottery.lotteryId)

        // instead of get + set we do one atomic read and write
        db.runTransaction { transaction ->
            val snapshot = transaction.get(lotteryRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NotFoundException("Lottery does not exist.")
            }

            transaction.set(lotteryRef, lottery)

            // since we do not care about get an object but just success or not
            Unit

        }
            .addOnSuccessListener {
                Log.d("LotteryTest", "Transaction succeeded")
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                Log.e("LotteryTest", "Transaction failed", exception)
                result(Result.failure(exception))

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
     * @param result lamba function that has Result and if:
     *      - Result.success(Lottery) if successful
     *      - Result.success(exception) if not successful
     * @return Result<Lottery>
     */
    fun getLotteryById(
        lotteryId: String,
        result: (Result<Lottery>) -> Unit
    ){

        if (lotteryId.isBlank()) {
            result(Result.failure(
                ValidationException("Lottery ID cannot be empty.")
            ))
            return
        }

        lotteriesRef
            .document(lotteryId)
            .get()
            .addOnSuccessListener { document ->

                // if the document do not exist --> Lottery do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NotFoundException("Lottery does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val lottery = document.toObject(Lottery::class.java)


                // for testing
                Log.d("LotteryTest", "Raw data: ${document.data}")
                Log.d("LotteryTest", "Mapped object: $lottery")


                // if conversion was successful
                if (lottery == null) {
                    result(Result.failure(
                        DataConversionException("Failed to convert document to Lottery.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved Lottery
                result(Result.success(lottery))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))

            }
    }

    // ----------------------------------- From list -----------------------------------

    /**
     * get one single Lottery object
     *
     * @param lotteryId String
     * @return Lottery?
     *      - Profile if exist
     *      - null if it does not exist
     */
    fun getLotteryFromListById(lotteryId: String): Lottery? {
        return _allLotteries.value.find { it.lotteryId == lotteryId }

    }



    /**
     * Get a list of Lottery objects
     *
     * @param ownerId
     * @return List<Lottery>
     */
    fun getLotteriesFromListByOwnerId(ownerId: String): List<Lottery> {
        return _allLotteries.value.filter { it.ownerId == ownerId}
    }

    /**
     * Lotteries this owner created OR was added to as co-owner
     *
     */

    fun getLotteriesFromListByAnyOwnerId(ownerId: String): List<Lottery> {
        // TODO (US 02.04.08)
        return getLotteriesFromListByOwnerId(ownerId)
    }



    /**
     * Get one single Lottery object
     *
     * @param status
     * @return List<Lottery>
     */
    fun getLotteriesFromListByStatus(status: String): List<Lottery> {
        return _allLotteries.value.filter { it.status == status}
    }


    /*The following three were helped to be refined by OpenAI, ChatGPT and Android Studio suggestions on 2026-11-09 from my initial code.*/

    /**
     * Get a list of Lottery objects
     *
     * @param date LocalDate
     * @return List<Lottery>
     */
    fun getLotteriesFromListActiveOnDate(date: LocalDate): List<Lottery> {
        return _allLotteries.value.filter { lottery ->
            val startDate = LocalDate.parse(lottery.startDate)
            val endDate = LocalDate.parse(lottery.endDate)

            date in startDate..endDate
        }
    }

    /**
     * Get a list of Lottery objects
     *
     * @return List<Lottery> --> can be empty list if not match found
     */
    fun getLotteriesFromListUpcoming(): List<Lottery> {
        val today = LocalDate.now()

        return _allLotteries.value.filter {
            LocalDate.parse(it.startDate) > today
        }
    }

    // val date = timestamp?.toDate() --> createdAt


    /**
     * Get a list of Lottery objects
     *
     * @return List<Lottery>
     */
    fun getLotteriesFromListExpired(): List<Lottery> {
        val today = LocalDate.now()

        return _allLotteries.value.filter {
            LocalDate.parse(it.endDate) < today
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

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val updatedLotteries = snapshot.documents.mapNotNull { document ->
                document.toObject(Lottery::class.java)
            }

            _allLotteries.value = updatedLotteries

            _isLotteriesLoaded.value = true
        }
    }



    /*
        Verifies that selected campsites exist and belong to the owner, and then saves the lottery with a generated document ID

        Report confirmed save success or an ownership/database error through callbacks.

     */

    // im going to review this later!

    /*
    fun saveNewLottery( // THis function verifies campsite ownership and saves a new lottery.
        lottery: Lottery,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (lottery.getLotteryOwnerId().isBlank()) {
            onFailure("An owner is required.")
            return
        }

        val campsiteIds = lottery.campsiteIds

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
                        document.getString("ownerId") == lottery.getLotteryOwnerId()
                }

                if (!ownsEveryCampsite) {
                    onFailure("You can only list campsites you own.")
                } else {
                    val reference = lotteriesRef.document()

                    val savedLottery = lottery.copy(
                        lotteryId = reference.id,
                        campsiteIds = campsiteIds as MutableList<String> // Fixed by Bailey, take a look at it?
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
    }*/


}