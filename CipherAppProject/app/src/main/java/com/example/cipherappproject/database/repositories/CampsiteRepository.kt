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
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Campsite
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CampsiteRepository {
    private val db = Firebase.firestore
    private val campsitesRef = db.collection("campsites")
    private val _allCampsites = mutableStateListOf<Campsite>()


    val campsites: List<Campsite>
        get() = _allCampsites

    /**
     * Creates behinds a Campsite object and add it to the firebase database with a random and unique id.
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
     * @param result lamba function that provide if:
     *      - The object was added successfully then it returns the id of that object
     *      - The object was not added not successfully so it returns null
     * @return nullable string with the id of the created object
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
        result: (String?) -> Unit

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
            qrCodeImagePath = qrCodeImagePath
        )

        //Tasks.await(campsiteRef.set(campsite))
        // save the object in the firebase
        campsiteRef
            .set(campsite)
            .addOnSuccessListener {
                result(campsiteRef.id)
            }
            .addOnFailureListener {
                result(null)
            }



    }

    /**
     * retrieves Campsite object into the firebase database .
     *
     * @param campsiteId
     * @param result lamba function that provide if:
     *      - successfully then it returns the Campsite object
     *      - not successfully so it returns null
     * @return nullable Campsite object
     */
    fun getCampsiteById(
        campsiteId: String,
        result: (Campsite?) -> Unit
    ){
        campsitesRef
            .document(campsiteId)
            .get()
            .addOnSuccessListener { document ->

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val campsite = document.toObject(Campsite::class.java)

                Log.d("CampsiteTest", "Raw data: ${document.data}")

                Log.d("CampsiteTest", "Mapped object: $campsite")


                // to avoid :type use lamba
                result(campsite)

            }
            .addOnFailureListener { exception ->
                result(null)
            }
    }

    /**
     * Updates behinds a Campsite object into the firebase database .
     *
     * @param campsite Campsite object
     * @param result lamba function that provide if:
     *      - successfully then it returns a boolean as true
     *      - not successfully so it returns a boolean as false
     * @return nullable string
     */
    fun updateCampsite(
        campsite: Campsite,
        result: (Boolean) -> Unit
    ) {
        campsitesRef.document(campsite.campsiteId)
            .set(campsite)
            .addOnSuccessListener {
                result(true)
            }
            .addOnFailureListener {
                result(false)
            }
    }

    /*
    fun deleteCampsite(campsiteId: String) {
        campsitesRef.document(campsiteId)
            .delete() // need to check if this also affects lotteries, reservations, availability, images??? frick

    }*/

    /*
    fun getCampsitesByOwner() {

    }

    fun getCampsitesByTags() {

    }

    fun getCampsitesByAmenities() {

    }

    fun getCampsitesByLocation() {

    }

    fun getCampsitesByPrice() {

    }*/


    init {
        campsitesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allCampsites.clear()

            snapshot?.documents?.forEach { document ->
                val campsite = document.toObject(Campsite::class.java) // pass from firestore to kotlin object

                if (campsite != null) {
                    _allCampsites.add(campsite)
                }
            }
        }
    }


}