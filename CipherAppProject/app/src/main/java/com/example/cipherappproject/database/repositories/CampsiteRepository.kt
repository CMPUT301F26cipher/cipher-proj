package com.example.cipherappproject.database.repositories

import androidx.compose.runtime.mutableStateListOf
import com.example.cipherappproject.database.models.Campsite
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CampsiteRepository {
    private val db = Firebase.firestore
    private val campsitesRef = db.collection("campsites")
    private val _campsites = mutableStateListOf<Campsite>()


    val campsites: List<Campsite>
        get() = _campsites

    /**
     * create a campsite object and save it in firestore database
     *
     * OTHER SPECIFICATIONS WILL BE ADDED LATER
     *
     * @param ownerId use to identify to which owner is related to
     * @param campsiteName is the campsite name
     * @param mapLocation the path to the image with the location of the campsite
     * @param description the campsite description
     * @param amenities the campsite further features
     * @param pricePerNight campsite price
     * @param imagePath the path locally to the campsite image
     * @param tags the keywords need for the filter feature adn search bar
     * @param maxGuests how many guest the campsite can hold
     * @param qrCodeImagePath the campsite qr code path
     * @return NA
     */
    /*Taken and learned from
    - firebase official documentation https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    - reddit question https://www.reddit.com/r/Firebase/comments/woiubf/how_to_store_document_id_in_a_field_of_a_document/
    */

    fun addCampsite( // pass the field and only then create the campsite object in this function --> since id is only known and created here
        ownerId: String,
        campsiteName: String,
        mapLocation: String,
        description: String,
        amenities: List<String>,
        maxGuests: Int,
        pricePerNight: Float,
        imagePath: String,
        tags: List<String>,
        qrCodeImagePath: String

    ) {
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

        // save the object in the firebase
        campsiteRef.set(campsite)
    }


    fun updateCampsite(campsite: Campsite) {
        campsitesRef.document(campsite.campsiteId).set(campsite)
    }


    fun deleteCampsite(campsiteId: String) {
        campsitesRef.document(campsiteId)
            .delete() // need to check if this also affects lotteries, reservations, availability, images??? frick

    }

    fun getCampsitesByOwner() {

    }

    fun getCampsitesByTags() {

    }

    fun getCampsitesByAmenities() {

    }

    fun getCampsitesByLocation() {

    }

    fun getCampsitesByPrice() {

    }


    init {
        campsitesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _campsites.clear()

            snapshot?.documents?.forEach { document ->
                val campsite = document.toObject(Campsite::class.java) // pass from firestore to kotlin object

                if (campsite != null) {
                    _campsites.add(campsite)
                }
            }
        }
    }


}