package com.example.cipherappproject.database.models

import com.google.firebase.Timestamp

/*
--------------------------------------------------------------------------------------
Program Name: Rating.kt
Date Written: November 11, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database for the object as Rating
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-11  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents a Rating main attributes and status in relation with the Firebase database.
 *
 * A Rating is meant to provide the main features of a review/rating such as
 *
 * @property ratingId use to uniquely identify each reservation within the database
 * @property userId use to identify to which user comes from
 * @property campsiteId use to identify to which campsite comes from
 * @property review the user review
 * @property status determine if the reservation is removed, active, hidden, etc
 * @property rating the rating from the usr
 * @property createdAt when it was made changed to timestamp
 *
 *
 */

data class Rating (
    val ratingId: String = "",
    val userId: String = "",
    val campsiteId: String = "",
    val review: String = "",
    val rating: Double = 0.0,
    var status: String = "",
    val createdAt: Timestamp = Timestamp.now()
)