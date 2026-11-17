package com.example.cipherappproject.database.models

import com.google.firebase.Timestamp

/*
--------------------------------------------------------------------------------------
Program Name: Message.kt
Date Written: November 16, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Message
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-16  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents a Message main attributes and status in relation with the Firebase database.
 *
 * A Message is meant to provide from a specific reservation, the conversation between owner
 * and user.
 *
 * @property conversationId use to uniquely identify each message within the database
 * @property campsiteId use to identify from which campsite
 * @property reservationId use to identify from which reservation
 * @property ownerId the campsite owner id
 * @property userId from which use was sent
 * @property createdAt when message was made IT IS AUTOMATIC DO NOT ADD IT!
 * @property status the status of the message removed, sent, etc --> check enum class
 *
 *
 */

data class Conversation(
    val conversationId: String = "",
    val campsiteId: String = "",
    val reservationId: String = "",
    val ownerId: String = "",
    val userId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val lastMessage: String = "",
    val status: String = ""
)