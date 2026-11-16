package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
Program Name: CampsiteRepository.kt
Date Written: October 27, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for CampsiteRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-10-27  Sofia       1.01     added skeleton
2026-11-04  Sofia       1.02     added query get by id
2026-11-05  Sofia       1.03     added queries retrieve list and set by group of collection
2026-11-05  Sofia       1.04     added exception handle in lamba for all queries
2026-11-06  Sofia       1.05     fixed the get and set methods for the obejcts
2026-11-08  Sofia       1.06     added exceptions and error handling
2026-11-08  Sofia       1.07     added queries using the list
2026-11-12  Sofia       1.08     added rules, policies and comments. Also added state boolean for list local queries.
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.models.Notification
import com.example.cipherappproject.database.models.Profile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Manages Campsite data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Campsite documents
 * directly from Firestore, as well as methods to search and filter
 * from the local Campsite list. This local list is inside a state.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

class CampsiteRepository {
    private val db = Firebase.firestore
    private val campsitesRef = db.collection("campsites")
    private val _allCampsites = mutableStateOf<List<Campsite>>(emptyList())

    val campsites: State<List<Campsite>>
        get() = _allCampsites

    private val _isCampsitesLoaded = mutableStateOf(false)

    val isCampsitesLoaded: State<Boolean>
        get() = _isCampsitesLoaded


    // ----------------------------------- Queries -----------------------------------

    /**
     * Creates behinds a Campsite object and add it to the firebase database
     * with a random and unique id.
     *
     * @param ownerId use to identify to which owner is related to
     * @param campsiteName is the campsite name
     * @param mapLocation
     * @param description the campsite description
     * @param amenities the campsite further features
     * @param pricePerNight campsite price
     * @param imagePath the path locally to the campsite image
     * @param tags the keywords need for the filter feature adn search bar
     * @param maxGuests how many guest the campsite can hold
     * @param qrCodeImagePath the campsite qr code path
     * @param status the campsite qr code path
     * @param rules the campsite rules
     * @param policies the campsite polices --> check enum
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */

    /*Taken and learned from
    - firebase official documentation https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    - reddit question https://www.reddit.com/r/Firebase/comments/woiubf/how_to_store_document_id_in_a_field_of_a_document/
    */

    fun addCampsite( // pass the field and only then create the campsite object in this function --> since id is only known and created here
        ownerId: String,
        campsiteName: String,
        mapLocation: List<String> ,
        description: String,
        amenities: MutableList<String>,
        maxGuests: Int,
        pricePerNight: Double,
        imagePath: String,
        tags: MutableList<String>,
        qrCodeImagePath: String,
        status: String,
        rules: MutableList<String>,
        policies: MutableList<String>,
        result: (Result<String>) -> Unit

    ){
        // create a reference and id to the new campsite
        val campsiteRef = campsitesRef.document()

        // create campsite object
        val campsite = Campsite(
            campsiteId = campsiteRef.id,
            ownerId = ownerId,
            campsiteName = campsiteName,
            mapLocation = mapLocation,
            description = description,
            amenities = amenities,
            maxGuests = maxGuests,
            pricePerNight = pricePerNight,
            imagePath = imagePath,
            tags = tags,
            qrCodeImagePath = qrCodeImagePath,
            status = status,
            rules = rules,
            policies = policies

        )

        //Tasks.await(campsiteRef.set(campsite))
        // save the object in the firebase
        campsiteRef
            .set(campsite)
            .addOnSuccessListener {
                result(Result.success(campsiteRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }

    /**
     * retrieves Result type that has Campsite object
     *
     * @param campsiteId
     * @param result lamba function that has Result and if:
     *      - Result.success(Campsite) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Campsite>
     */
    
    /*Taken, learned and improved from OpenAI, ChatGPT on 2026-06-11 on the subject "How does addOnSuccessListener works and how to implement further errors or assertions as python does with ValueError", "How Result works Result can you do an example about it and how can you pass an error? how does work Try else here like python?, Examples please? "*/
    /*Also taken from https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/  and https://www.baeldung.com/kotlin/result-class*/

    fun getCampsiteById(
        campsiteId: String,
        result: (Result<Campsite>) -> Unit
    ){

        if (campsiteId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Campsite ID cannot be empty.")
            ))
            return
        }

        campsitesRef
            .document(campsiteId)
            .get()
            .addOnSuccessListener { document ->


                // if the document do not exist --> campsite do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Campsite does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val campsite = document.toObject(Campsite::class.java)


                // for testing
                Log.d("CampsiteTest", "Raw data: ${document.data}")
                Log.d("CampsiteTest", "Mapped object: $campsite")


                // if conversion was successful
                if (campsite == null) {
                    result(Result.failure(
                        IllegalStateException("Failed to convert document to Campsite.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved campsite
                result(Result.success(campsite))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    /**
     * Updates behinds a Campsite object into the firebase database
     *
     * @param campsite Campsite object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateCampsite(
        campsite: Campsite,
        result: (Result<Unit>) -> Unit
    ) {

        //https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
        if (campsite.campsiteId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Campsite ID cannot be empty.")
            ))
            return
        }

        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val campsiteRef = campsitesRef.document(campsite.campsiteId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(campsiteRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NoSuchElementException("Campsite does not exist.")
            }

            transaction.set(campsiteRef, campsite)

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
     * Get a Campsite object
     *
     * @param campsiteId String
     * @return Campsite? --> null if not match found
     */
    // https://kotlinlang.org/docs/collection-elements.html#retrieve-with-selector
    fun getCampsiteFromListById(campsiteId: String): Campsite? {
        return _allCampsites.value.find { it.campsiteId == campsiteId }

    }


    /**
     * Get a list of Campsite objects
     *
     * @param name String
     * @return List<Campsite> --> can be empty list if not match found
     */

    // https://www.tutorialspoint.com/kotlin/kotlin_list_contains_function.htm
    fun getCampsiteFromListByName(name: String): List<Campsite> {
        return _allCampsites.value.filter { it.campsiteName.contains(name, ignoreCase = true) }

    }


    /**
     * Get a list of Campsite objects
     *
     * @param ownerId String
     * @return List<Campsite> --> can be empty list if not match found
     */

    fun getCampsiteFromListByOwnerId(ownerId: String): List<Campsite> {
        return _allCampsites.value.filter { it.ownerId == ownerId }

    }


    /**
     * Get a list of Campsite objects
     *
     * @param status String
     * @return List<Campsite> --> can be empty list if not match found
     *
     * Example: getCampsiteFromListByStatus(CampsiteStatus.ACTIVE.name)
     */

    fun getCampsiteFromListByStatus(status: String): List<Campsite> {
        return _allCampsites.value.filter { it.status == status }

    }

    /**
     * Get a list of Campsite objects
     *
     * @param pricePerNight Double
     * @return List<Campsite> --> can be empty list if not match found
     */

    fun getCampsiteFromListLessThanPrice(pricePerNight: Double): List<Campsite> {
        return _allCampsites.value.filter { it.pricePerNight < pricePerNight }

    }

    /**
     * Get a list of Campsite objects
     *
     * @param pricePerNight Double
     * @return List<Campsite> --> can be empty list if not match found
     */

    fun getCampsiteFromListMoreThanPrice(pricePerNight: Double): List<Campsite> {
        return _allCampsites.value.filter { it.pricePerNight > pricePerNight }

    }

    /**
     * Get a list of Campsite objects
     *
     * @param startPrice Double
     * @param endPrice Double
     * @return List<Campsite> --> can be empty list if not match found
     */

    fun getCampsiteFromListBetweenPrice(startPrice: Double, endPrice: Double): List<Campsite> {

        // https://kotlinlang.org/docs/collection-aggregate.html

        // either if both are in the correct place
        val minPrice = minOf(startPrice, endPrice)
        val maxPrice = maxOf(startPrice, endPrice)

        return _allCampsites.value.filter {
            it.pricePerNight in minPrice..maxPrice
        }

    }


    /**
     * Get a list of Campsite objects
     *
     * @param amenities List<String>
     * @return List<Campsite> --> can be empty list if not match found
     */

    fun getCampsitesFromListByAmenities(amenities: List<String>): List<Campsite> {

        // filter goes for each campsite in _allCampsites
        return _allCampsites.value.filter { campsite ->

            // check each amenity for the current campsite
            campsite.amenities.any { campsiteAmenity  ->

                // check if the current campsite amenity is in the amenities list argument
                amenities.any { it.equals(campsiteAmenity , ignoreCase = true) }
            }
        }
    }


    /*
    fun deleteCampsite(campsiteId: String) {
        campsitesRef.document(campsiteId)
            .delete() // need to check if this also affects lotteries, reservations, availability, images??? frick

    }*/


    init {
        campsitesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val updatedCampsites = snapshot.documents.mapNotNull { document ->
                document.toObject(Campsite::class.java)
            }

            _allCampsites.value = updatedCampsites

            _isCampsitesLoaded.value = true
        }
    }
}