package com.example.cipherappproject.database.models

import com.google.firebase.Timestamp

/*
--------------------------------------------------------------------------------------
Program Name: Notification.kt
Date Written: November 10, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Notification
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
 * Represents a Notification main attributes and status in relation with the Firebase database.
 *
 * A Notification is meant to provide from which event or type comes from such as lottery,
 * reservation, system, etc. It also has a title, message, to whom is sent to and when it was
 * created.
 *
 * @property notificationId use to uniquely identify each notification within the database
 * @property eventId use to identify from which event of types comes from --> lotteryId, reservationId, etc
 * @property toUserId use to identify to which user is the notification
 * @property title the title of the notification
 * @property message the text of the notification
 * @property createdAt when notification was made IT IS AUTOMATIC DO NOT ADD IT!
 *
 *
 */

data class Notification (
    val notificationId: String = "",
    val eventId: String = "",
    val eventType: String = "",
    val toUserId: String = "",
    val fromUserId: String = "", // Added code, Justin. 
    val title: String = "",
    val message: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    var isRead: Boolean = false
)