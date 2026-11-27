package com.example.cipherappproject.database.repositories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.exceptions.DataConversionException
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Message
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
2026-11-19  Sofia      1.04     added custom exceptions
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

class MessageRepository {

    private val db = Firebase.firestore
    private val messagesRef = db.collectionGroup("messages")
    private val _allMessages = mutableStateOf<List<Message>>(emptyList())


    val messages: State<List<Message>>
        get() = _allMessages


    private val _isMessagesLoaded = mutableStateOf(false)

    val isMessagesLoaded: State<Boolean>
        get() = _isMessagesLoaded


    /**
     * Creates behinds a Message object and add it to the firebase database with a random and unique id.
     *
     * @param conversationId
     * @param fromUserId
     * @param message
     * @param status
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */

    fun addMessage(
        conversationId: String,
        fromUserId: String,
        message: String,
        status: String,
        result: (Result<String>) -> Unit

    ) {
        // create a reference and id to the new Message
        val conversationRef = db.collection("conversations").document(conversationId)


        db.runTransaction { transaction ->
            val messageSnapshot = transaction.get(conversationRef)

            // Check if the parent Conversation exists
            if (!messageSnapshot.exists()) {
                throw NotFoundException("Conversation does not exist.")
            }

            val messageRef = conversationRef
                .collection("messages")
                .document()

            // create Message object
            val message = Message(
                messageId = messageRef.id,
                conversationId = conversationId,
                fromUserId = fromUserId,
                message = message,
                status = status

            )

            // set entry
            transaction.set(messageRef, message)

            // return id
            messageRef.id

        }
            .addOnSuccessListener { messageId -> // since is success return id
                result(Result.success(messageId))
            }
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
        val messageId = message.messageId
        val conversationId = message.conversationId

        // Check IDs
        if (messageId.isBlank() || conversationId.isBlank()) {
            result(Result.failure(
                ValidationException("Conversation or Message ID cannot be empty.")
            ))
            return
        }

        val conversationRef = db.collection("conversations").document(conversationId)

        val messageRef = conversationRef
            .collection("messages")
            .document(messageId)

        // Check conversation and message before updating
        db.runTransaction { transaction ->

            val conversationSnapshot = transaction.get(conversationRef)

            // Check whether conversation exists
            if (!conversationSnapshot.exists()) {
                throw NotFoundException("Conversation does not exist.")
            }

            val messageSnapshot = transaction.get(messageRef)

            // Check whether Message exists
            if (!messageSnapshot.exists()) {
                throw NotFoundException("Message entry does not exist.")
            }

            // Check whether entry belongs to the specified Conversation
            val storedConversationId = messageSnapshot.getString("conversationId")

            if (storedConversationId != conversationId) {
                throw ValidationException(
                    "The Message does not belong to this Conversation."
                )
            }

            // Update message
            transaction.set(messageRef, message)

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


    /**
     * retrieves Result type that has Message object
     *
     * @param messageId
     * @param result lamba function that has Result and if:
     *      - Result.success(Message) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Message>
     */
    fun getMessageById(
        conversationId: String,
        messageId: String,
        result: (Result<Message>) -> Unit
    ) {
        // Check IDs
        if (conversationId.isBlank() || messageId.isBlank()) {
            result(Result.failure(
                ValidationException(
                    "Message ID and Conversation ID cannot be empty."
                )
            ))
            return
        }

        val messageRef = db
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .document(messageId)

        messageRef
            .get()
            .addOnSuccessListener { document ->

                // Check whether entry exists
                if (!document.exists()) {
                    result(Result.failure(
                        NotFoundException("Message entry does not exist.")
                    ))
                    return@addOnSuccessListener
                }

                // Convert document into message object
                val message = document.toObject(Message::class.java)

                // Check whether conversion was successful
                if (message == null) {
                    result(Result.failure(
                        DataConversionException(
                            "Failed to convert document to Message."
                        )
                    ))
                    return@addOnSuccessListener
                }

                result(Result.success(message))
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
     * @param conversationId String
     * @return Message? --> null if not match found
     */

    fun getMessageFromListById(messageId: String, conversationId: String): Message? {
        return _allMessages.value.find {
            it.messageId == messageId && it.conversationId == conversationId
        }
    }


    /**
     * Get a list of Message objects
     *
     * @param conversationId String
     * @return List<Message> --> can be empty list if not match found
     */
    fun getMessagesFromListByConversationId(conversationId: String): List<Message> {
        return _allMessages.value.filter {
            it.conversationId == conversationId
        }
    }



    /**
     * Get a list of Message objects
     *
     * @param conversationId String
     * @return List<Message> --> can be empty list if not match found
     */
    fun getMessagesFromListByConversationIdSorted(conversationId: String): List<Message> {
        return _allMessages.value
            .filter { it.conversationId == conversationId }
            .sortedBy { it.createdAt }
    }



    /**
     * Get a list of Message objects
     *
     * @param conversationId String
     * @param fromUserId String
     * @return List<Message> --> can be empty list if not match found
     */
    fun getMessagesFromListBySenderId(conversationId: String, fromUserId: String): List<Message> {
        return _allMessages.value.filter {
            it.conversationId == conversationId && it.fromUserId == fromUserId
        }
    }


    /**
     * Get a list of Message objects
     *
     * @param conversationId String
     * @param status String
     * @return List<Message> --> can be empty list if not match found
     */
    fun getMessagesFromListByStatus(conversationId: String, status: String): List<Message> {
        return _allMessages.value.filter {
            it.conversationId == conversationId && it.status == status
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

            val updatedMessages= snapshot.documents.mapNotNull { document ->
                document.toObject(Message::class.java)
            }
            _allMessages.value = updatedMessages

            _isMessagesLoaded.value = true
        }
    }


}