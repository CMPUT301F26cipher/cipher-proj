package com.example.cipherappproject.database.repositories

import android.util.Log
import com.example.cipherappproject.database.models.Conversation
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.cipherappproject.database.exceptions.DataConversionException
import com.example.cipherappproject.database.exceptions.NotFoundException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.enums.ConversationStatus

/*
--------------------------------------------------------------------------------------
Program Name: ConversationRepository.kt
Date Written: November 12, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for ConversationRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-19  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

class ConversationRepository {
    private val db = Firebase.firestore
    private val conversationsRef = db.collection("conversations")

    private val _allConversations = mutableStateOf<List<Conversation>>(emptyList())

    val conversations: State<List<Conversation>>
        get() = _allConversations

    private val _isConversationsLoaded = mutableStateOf(false)

    val isConversationsLoaded: State<Boolean>
        get() = _isConversationsLoaded


    // ----------------------------------- Queries -----------------------------------
    /**
     * Creates behinds a Message object and add it to the firebase database with a random and unique id.
     *
     * @param campsiteId
     * @param reservationId
     * @param ownerId
     * @param userId
     * @param lastMessage
     * @param status
     * @param result lamba function that provide if:
     *      - Result.success(id) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<String>
     */

    fun addConversation(
        campsiteId: String,
        reservationId: String,
        ownerId: String,
        userId: String,
        lastMessage: String,
        status: String,
        result: (Result<String>) -> Unit

    ) {
        // create a reference and id to the new Conversation
        val conversationRef = conversationsRef.document()

        // create Conversation object
        val campsite = Conversation(
            conversationId = conversationRef.id,
            campsiteId = campsiteId,
            reservationId = reservationId,
            ownerId = ownerId,
            userId = userId,
            lastMessage = lastMessage,
            status = status,
        )

        // save the object in the firebase
        conversationRef
            .set(campsite)
            .addOnSuccessListener {
                result(Result.success(conversationRef.id))

            }
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }

    }


    /**
     * retrieves Result type that has Message object
     *
     * @param conversationId
     * @param result lamba function that has Result and if:
     *      - Result.success(Conversation) if successful
     *      - Result.failure(exception) if not successful
     * @return Result<Campsite>
     */

    fun getConversationById(
        conversationId: String,
        result: (Result<Conversation>) -> Unit
    ) {

        if (conversationId.isBlank()) {
            result(
                Result.failure(
                    ValidationException("Conversation ID cannot be empty.")
                )
            )
            return
        }

        conversationsRef
            .document(conversationId)
            .get()
            .addOnSuccessListener { document ->


                // if the document do not exist --> Conversation do not exist
                if (!document.exists()) {
                    result(
                        Result.failure(
                            NotFoundException("Conversation does not exist.")
                        )
                    )
                    return@addOnSuccessListener // we return the object so that it do not continue the next lines
                }

                val conversation = document.toObject(Conversation::class.java)


                // for testing
                Log.d("ConversationTest", "Raw data: ${document.data}")
                Log.d("ConversationTest", "Mapped object: $conversation")


                // if conversion was successful
                if (conversation == null) {
                    result(
                        Result.failure(
                            DataConversionException("Failed to convert document to Conversation.")
                        )
                    )
                    return@addOnSuccessListener
                }

                // return successfully retrieved Conversation
                result(Result.success(conversation))

            }

            // if any other error
            .addOnFailureListener { exception ->
                result(Result.failure(exception))
            }
    }

    /**
     * Updates behinds a Conversation object into the firebase database
     *
     * @param conversation Conversation object
     * @param result lamba function as callback that provide if:
     *      - Result.success(Unit) if successfully
     *      - Result.failure(exception) if not successful
     * @return Result<Unit>
     */
    fun updateConversation(
        conversation: Conversation,
        result: (Result<Unit>) -> Unit
    ) {

        if (conversation.conversationId.isBlank()) {
            result(
                Result.failure(
                    ValidationException("Conversation ID cannot be empty.")
                )
            )
            return
        }

        // https://firebase.google.com/docs/firestore/manage-data/transactions
        val conversationRef = conversationsRef.document(conversation.conversationId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(conversationRef)

            // if object do not exist
            if (!snapshot.exists()) {
                throw NotFoundException("Conversation does not exist.")
            }

            transaction.set(conversationRef, conversation)

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
     * Get a Conversation object
     *
     * @param conversationId String
     * @return Conversation? --> null if not match found
     */
    fun getConversationFromListById(conversationId: String): Conversation? {
        return _allConversations.value.find {
            it.conversationId == conversationId
        }
    }

    /**
     * Get a Conversation object
     *
     * @param reservationId String
     * @return Conversation? --> null if not match found
     */
    fun getConversationFromListByReservationId(reservationId: String): Conversation? {
        return _allConversations.value.find {
            it.reservationId == reservationId
        }
    }

    /**
     * Get a list of Conversation objects
     *
     * @param campsiteId String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getConversationsFromListByCampsiteId(campsiteId: String): List<Conversation> {
        return _allConversations.value.filter {
            it.campsiteId == campsiteId
        }
    }

    /**
     * Get a list of Conversation objects
     *
     * @param ownerId String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getConversationsFromListByOwnerId(ownerId: String): List<Conversation> {
        return _allConversations.value.filter {
            it.ownerId == ownerId
        }
    }


    /**
     * Get a list of Conversation objects
     *
     * @param userId String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getConversationsFromListByUserId(userId: String): List<Conversation> {
        return _allConversations.value.filter {
            it.userId == userId
        }
    }

    /**
     * Get a list of Conversation objects
     *
     * @param status String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getConversationsFromListByStatus(status: String): List<Conversation> {
        return _allConversations.value.filter {
            it.status == status
        }
    }


    /**
     * Get a list of Conversation objects
     *
     * @param userId String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getOpenConversationsFromListByUserId(userId: String): List<Conversation> {
        return _allConversations.value.filter {
            it.userId == userId && it.status == ConversationStatus.OPEN.name
        }
    }

    /**
     * Get a list of Conversation objects
     *
     * @param ownerId String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getOpenConversationsFromListByOwnerId(ownerId: String): List<Conversation> {
        return _allConversations.value.filter {
            it.ownerId == ownerId && it.status == ConversationStatus.OPEN.name
        }
    }


    /**
     * Get a list of Conversation objects
     *
     * @param userId String
     * @return List<Conversation> --> can be empty list if not match found
     *
     */
    fun getConversationsFromListByUserIdSorted(userId: String): List<Conversation> {
        return _allConversations.value
            .filter { it.userId == userId }
            .sortedByDescending { it.updatedAt }
    }

    init {
        conversationsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot == null) {
                return@addSnapshotListener
            }

            val updateConversations = snapshot.documents.mapNotNull { document ->
                document.toObject(Conversation::class.java)
            }

            _allConversations.value = updateConversations

            _isConversationsLoaded.value = true
        }
    }

}