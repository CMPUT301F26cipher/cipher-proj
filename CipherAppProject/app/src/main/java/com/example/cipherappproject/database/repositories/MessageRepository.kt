package com.example.cipherappproject.database.repositories

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.exceptions.DataConversionException
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Message
import com.example.cipherappproject.database.models.Notification
import com.example.cipherappproject.database.models.Profile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


/*
--------------------------------------------------------------------------------------
Program Name: MessageRepository.kt
Date Written: November 12, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for MessageRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-12  Sofia      1.01     added skeleton
2026-11-15  Sofia      1.02     added states to the local list and changes init too
2026-11-15  Sofia      1.03     added custom exceptions
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


/**
 * Manages Message data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Message documents
 * directly from Firestore, as well as methods to search and filter
 * from the local Message list. This local list is inside a state.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

/*
class MessageRepository {
    private val db = Firebase.firestore
    private val messagesRef = db.collection("messages")
    private val _allMessages = mutableStateOf<List<Message>>(emptyList())

    val messages: State<List<Message>>
        get() = _allMessages

    private val _isMessagesLoaded = mutableStateOf(false)

    val isMessagesLoaded: State<Boolean>
        get() = _isMessagesLoaded



    // ----------------------------------- Queries -----------------------------------

    /**
     * Creates behinds a Message object and add it to the firebase database with a random and unique id.
     *
     * @param reservationId
     * @param fromUserId
     * @param toUserId
     * @param message
     * @param status
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */

    fun addMessage(
        reservationId: String,
        fromUserId: String,
        toUserId: String,
        message: String,
        status: String,
        result: (Result<String>) -> Unit

    ){
        // create a reference and id to the new Message
        val messageRef = messagesRef.document()

        // create Message object
        val campsite = Message(
            messageId = messageRef.id,
            reservationId = reservationId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            message = message,
            status = status
        )

        // save the object in the firebase
        messageRef
            .set(campsite)
            .addOnSuccessListener {
                result(Result.success(messageRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }


    /**
     * retrieves Result type that has Message object
     *
     * @param messageId
     * @param result lamba function that has Result and if:
     *      - Result.success(Message) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Campsite>
     */

    fun getMessageById(
        messageId: String,
        result: (Result<Message>) -> Unit
    ){

        if (messageId.isBlank()) {
            result(Result.failure(
                ValidationException("Message ID cannot be empty.")
            ))
            return
        }

        messagesRef
            .document(messageId)
            .get()
            .addOnSuccessListener { document ->


                // if the document do not exist --> Message do not exist
                if (!document.exists()) {
                    result(Result.failure(
                        NotFoundException("Message does not exist.")
                    ))
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                val message = document.toObject(Message::class.java)


                // for testing
                Log.d("MessageTest", "Raw data: ${document.data}")
                Log.d("MessageTest", "Mapped object: $message")


                // if conversion was successful
                if (message == null) {
                    result(Result.failure(
                        DataConversionException("Failed to convert document to Message.")
                    ))
                    return@addOnSuccessListener
                }

                // return successfully retrieved Message
                result(Result.success(message))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }



    /**
     * Updates behinds a Message object into the firebase database
     *
     * @param message Message object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateMessage(
        message: Message,
        result: (Result<Unit>) -> Unit
    ) {

        if (message.messageId.isBlank()) {
            result(Result.failure(
                ValidationException("Message ID cannot be empty.")
            ))
            return
        }

        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val messageRef = messagesRef.document(message.messageId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(messageRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NotFoundException("Message does not exist.")
            }

            transaction.set(messageRef, message)

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
     * Get a Message object
     *
     * @param messageId String
     * @return Message? --> null if not match found
     */
    fun getMessageFromListById(messageId: String): Message? {
        return _allMessages.value.find { it.messageId == messageId }

    }

    /**
     * Get a list of Message objects
     *
     * @param reservationId String
     * @return List<Message> --> can be empty list if not match found
     *
     */

    fun getMessagesFromListByReservationId(reservationId: String): List<Message> {
        return _allMessages.value.filter { it.reservationId == reservationId }

    }

    /**
     * Get a list of Message objects
     *
     * @param status String
     * @return List<Message> --> can be empty list if not match found
     *
     * Example: getMessagesFromListByStatus(MessageStatus.ACTIVE.name)
     */

    fun getMessagesFromListByStatus(status: String): List<Message> {
        return _allMessages.value.filter { it.status == status }

    }


    /**
     * Get a list of Message objects
     *
     * @param userId String
     * @return List<Message> --> can be empty list if not match found
     *
     */
    fun getMessagesFromListBySenderId(userId: String): List<Message> {
        return _allMessages.value.filter { it.fromUserId == userId }
    }

    /**
     * Get a list of Message objects
     *
     * @param userId String
     * @return List<Message> --> can be empty list if not match found
     *
     */
    fun getMessagesFromListByReceiverId(userId: String): List<Message> {
        return _allMessages.value.filter { it.toUserId == userId }
    }

    /**
     * Get a list of Message objects
     *
     * @param userId1 String
     * @param userId2 String
     * @return List<Message> --> can be empty list if not match found
     *
     */
    fun getMessagesFromListBetweenUsers(userId1: String, userId2: String): List<Message> {
        return _allMessages.value.filter {
            (it.fromUserId == userId1 && it.toUserId == userId2) ||
                    (it.fromUserId == userId2 && it.toUserId == userId1)
        }
    }

    init {
        messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val updateMessages = snapshot.documents.mapNotNull { document ->
                document.toObject(Message::class.java)
            }

            _allMessages.value = updateMessages

            _isMessagesLoaded.value = true
        }
    }
}

 */