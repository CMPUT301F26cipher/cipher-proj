package com.example.cipherappproject.database.models

import com.google.firebase.Timestamp

/*
--------------------------------------------------------------------------------------
Program Name: Message.kt
Date Written: November 10, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Message
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-10  Sofia      1.01     added skeleton
2026-11-11   Sofia     1.02     added if is read, created at as timestamp, and event type
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
 * @property messageId use to uniquely identify each message within the database
 * @property reservationId use to identify from which reservation comes from
 * @property fromUserId use to identify from which user is the message
 * @property toUserId use to identify to which user is the message
 * @property message the text of the message
 * @property createdAt when message was made IT IS AUTOMATIC DO NOT ADD IT!
 * @property status the status of the message removed, sent, etc --> check enum class
 *
 *
 */

data class Message (
    val messageId: String = "",
    val reservationId: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val message: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    var status: String = ""
)