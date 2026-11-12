package com.example.cipherappproject.database.repositories

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.models.Notification
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/*
--------------------------------------------------------------------------------------
Program Name: NotificationRepository.kt
Date Written: November 12, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for NotificationRepository
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
 * Manages Notification data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Notification documents
 * directly from Firestore, as well as methods to search and filter
 * from the local Notification list.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

class NotificationRepository {

    private val db = Firebase.firestore
    private val notificationsRef = db.collection("notifications")
    private val _allNotifications = mutableStateListOf<Notification>()

    val notifications: List<Notification>
        get() = _allNotifications


    private val _isNotificationsLoaded = mutableStateOf(false)

    val isNotificationsLoaded: State<Boolean>
        get() = _isNotificationsLoaded


    // ----------------------------------- Queries -----------------------------------

    /**
     * Creates behinds a Notification object and add it to the firebase database with a random and unique id.
     *
     * @param eventId
     * @param eventType
     * @param toUserId
     * @param title
     * @param message
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */

    fun addNotification(
        eventId: String,
        eventType: String,
        toUserId: String,
        title: String,
        message: String,
        result: (Result<String>) -> Unit

    ){
        // create a reference and id to the new Message
        val notificationRef = notificationsRef.document()

        // create Message object
        val notification = Notification(
            notificationId = notificationRef.id,
            eventId = eventId,
            eventType = eventType,
            toUserId = toUserId,
            title = title,
            message = message
        )

        // save the object in the firebase
        notificationRef
            .set(notification)
            .addOnSuccessListener {
                result(Result.success(notificationRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }


    /**
     * retrieves Result type that has Notification object
     *
     * @param notificationId
     * @param result lamba function that has Result and if:
     *      - Result.success(Notification) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Notification>
     */


    fun getNotificationById(
        notificationId: String,
        result: (Result<Notification>) -> Unit
    ){

        if (notificationId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Notification ID cannot be empty.")
            ))
            return
        }

        notificationsRef
            .document(notificationId)
            .get()
            .addOnSuccessListener { document ->


                // if the document do not exist --> Notification do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NoSuchElementException("Notification does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                /*learn and taken form https://medium.com/@ender.koca94/effective-data-modeling-in-firebase-cloud-firestore-using-kotlin-0a3224d84bb8*/
                val notification = document.toObject(Notification::class.java)


                // for testing
                Log.d("NotificationTest", "Raw data: ${document.data}")
                Log.d("NotificationTest", "Mapped object: $notification")


                // if conversion was successful
                if (notification == null) {
                    result(Result.failure(
                        IllegalStateException("Failed to convert document to Notification.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved notification
                result(Result.success(notification))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    /**
     * Updates behinds a Notification object into the firebase database
     *
     * @param notification Notification object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateCampsite(
        notification: Notification,
        result: (Result<Unit>) -> Unit
    ) {

        //https://medium.com/@niranjanky14/4-kotlin-advanced-exception-17daf07ea51a
        if (notification.notificationId.isBlank()) {
            result(Result.failure(
                IllegalArgumentException("Notification ID cannot be empty.")
            ))
            return
        }

        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val notificationRef = notificationsRef.document(notification.notificationId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(notificationRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NoSuchElementException("Notification does not exist.")
            }

            transaction.set(notificationRef, notification)
            transaction.set(notificationRef, notification)

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
     * Get a Notification object
     *
     * @param notificationId String
     * @return Notification? --> null if not match found
     */
    // https://kotlinlang.org/docs/collection-elements.html#retrieve-with-selector
    fun getNotificationFromListById(notificationId: String): Notification? {
        return _allNotifications.find { it.notificationId == notificationId }

    }

    /**
     * Get a list of Notification objects
     *
     * @param userId String
     * @return List<Notification> --> can be empty list if not match found
     */
    fun getNotificationsFromListByUserId(userId: String): List<Notification> {
        return _allNotifications.filter { it.toUserId == userId }
    }


    /**
     * Get a list of Notification objects
     *
     * @param isRead Boolean
     * @return List<Notification> --> can be empty list if not match found
     */
    fun getNotificationsFromListByReadStatus(isRead: Boolean): List<Notification> {
        return _allNotifications.filter { it.isRead == isRead }
    }

    /**
     * Get a list of Notification objects
     *
     * @param userId String
     * @return List<Notification> --> can be empty list if not match found
     */
    fun getUnreadNotificationsFromListByUserId(userId: String): List<Notification> {
        return _allNotifications.filter {
            it.toUserId == userId && !it.isRead
        }
    }

    /**
     * Get a list of Notification objects
     *
     * @param eventId String
     * @return List<Notification> --> can be empty list if not match found
     */
    fun getNotificationsFromListByEventId(eventId: String): List<Notification> {
        return _allNotifications.filter { it.eventId == eventId }
    }

    /**
     * Get a list of Notification objects
     *
     * @param eventType String
     * @return List<Notification> --> can be empty list if not match found
     */
    fun getNotificationsFromListByEventType(eventType: String): List<Notification> {
        return _allNotifications.filter { it.eventType == eventType }
    }


    init {
        notificationsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _allNotifications.clear()

            snapshot?.documents?.forEach { document ->
                val notification = document.toObject(Notification::class.java) // pass from firestore to kotlin object

                if (notification != null) {
                    _allNotifications.add(notification)
                }
            }

            _isNotificationsLoaded.value = true
        }
    }
}
