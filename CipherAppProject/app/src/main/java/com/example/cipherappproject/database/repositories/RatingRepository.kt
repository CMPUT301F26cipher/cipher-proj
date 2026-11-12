package com.example.cipherappproject.database.repositories

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.models.Rating
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/*
--------------------------------------------------------------------------------------
Program Name: RatingRepository.kt
Date Written: November 12, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for RatingRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-12  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


/**
 * Manages Rating data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Rating documents
 * directly from Firestore, as well as methods to search and filter
 * from the local Rating list.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

class RatingRepository {
    private val db = Firebase.firestore
    private val ratingsRef = db.collection("ratings")
    private val _allRatings = mutableStateListOf<Rating>()

    val ratings: List<Rating>
        get() = _allRatings

    private val _isRatingsLoaded = mutableStateOf(false)

    val isRatingsLoaded: State<Boolean>
        get() = _isRatingsLoaded

    // ----------------------------------- Queries -----------------------------------

    /**
     * Creates behinds a Campsite object and add it to the firebase database
     * with a random and unique id.
     *
     * @param userId
     * @param campsiteId
     * @param review
     * @param rating
     * @param status
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */

    fun addRating(
        userId: String,
        campsiteId: String,
        review: String,
        rating: Double,
        status: String,
        result: (Result<String>) -> Unit

    ){
        // create a reference and id to the new rating
        val ratingRef = ratingsRef.document()

        // create rating object
        val rating = Rating(
            ratingId = ratingRef.id,
            userId = userId,
            campsiteId = campsiteId,
            review = review,
            rating = rating,
            status = status
        )

        // save the object in the firebase
        ratingRef
            .set(rating)
            .addOnSuccessListener {
                result(Result.success(ratingRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }


    /**
     * retrieves Result type that has Rating object
     *
     * @param ratingId
     * @param result lamba function that has Result and if:
     *      - Result.success(Rating) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Rating>
     */

    fun getRatingById(
        ratingId: String,
        result: (Result<Rating>) -> Unit
    ){

        if (ratingId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Rating ID cannot be empty.")
            ))
            return
        }

        ratingsRef
            .document(ratingId)
            .get()
            .addOnSuccessListener { document ->


                // if the document do not exist --> Rating do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Rating does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val rating = document.toObject(Rating::class.java)


                // for testing
                Log.d("RatingTest", "Raw data: ${document.data}")
                Log.d("RatingTest", "Mapped object: $rating")


                // if conversion was successful
                if (rating == null) {
                    result(Result.failure(
                        IllegalStateException("Failed to convert document to Rating.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved rating
                result(Result.success(rating))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    /**
     * Updates behinds a Rating object into the firebase database
     *
     * @param rating Rating object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateCampsite(
        rating: Rating,
        result: (Result<Unit>) -> Unit
    ) {

        //https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
        if (rating.ratingId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Rating ID cannot be empty.")
            ))
            return
        }

        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val ratingRef = ratingsRef.document(rating.ratingId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(ratingRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NoSuchElementException("Rating does not exist.")
            }

            transaction.set(ratingRef, rating)

            // since we do not care about get an object but just success or not
            Unit

        }
            .addOnSuccessListener {
                result(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))

            }

    }


    // ----------------------------------- From list -----------------------------------
    /**
     * Get a Rating object
     *
     * @param ratingId String
     * @return Rating? --> null if not match found
     */
    // https://kotlinlang.org/docs/collection-elements.html#retrieve-with-selector
    fun getRatingFromListById(ratingId: String): Rating? {
        return _allRatings.find { it.ratingId == ratingId }

    }

    /**
     * Get a list of Rating objects
     *
     * @param campsiteId String
     * @return List<Rating> --> can be empty list if not match found
     */
    fun getRatingsFromListByCampsiteId(campsiteId: String): List<Rating> {
        return _allRatings.filter { it.campsiteId == campsiteId }
    }

    /**
     * Get a list of Rating objects
     *
     * @param userId String
     * @return List<Rating> --> can be empty list if not match found
     */
    fun getRatingsFromListByUserId(userId: String): List<Rating> {
        return _allRatings.filter { it.userId == userId }
    }

    /**
     * Get a list of Rating objects
     *
     * @param status String
     * @return List<Rating> --> can be empty list if not match found
     */
    fun getRatingsFromListByStatus(status: String): List<Rating> {
        return _allRatings.filter { it.status == status }
    }

    /**
     * Get a list of Rating objects
     *
     * @param campsiteId String
     * @return List<Rating> --> can be empty list if not match found
     */
    fun getRatingsFromListByCampsiteIdSorted(campsiteId: String): List<Rating> {
        return _allRatings
            .filter { it.campsiteId == campsiteId }
            .sortedByDescending { it.createdAt }
    }


    init {
        ratingsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allRatings.clear()

            snapshot?.documents?.forEach { document ->
                val rating = document.toObject(Rating::class.java) // pass from firestore to kotlin object

                if (rating != null) {
                    _allRatings.add(rating)
                }
            }

            _isRatingsLoaded.value = true
        }
    }
}