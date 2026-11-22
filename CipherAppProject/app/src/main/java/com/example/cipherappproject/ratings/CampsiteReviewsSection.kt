package com.example.cipherappproject.ratings

/*
--------------------------------------------------------------------------------------
Program Name: CampsiteReviewsSection.kt
Date Written: November 21, 2026
Author: Nethmini Mackenzy
Purpose: Shows a campsite's average rating and its reviews on the campsite details page
US: 01.13.01, 01.13.02
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-21  Nethmini   1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ratingText
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * "Ratings & Reviews" block: the average (same text as the search card) and each active
 * written review, newest first. Uses a plain Column since the details page already scrolls.
 */
@Composable
fun CampsiteReviewsSection(campsiteId: String) {
    val ratings = RepositoryDatabase.ratingRepository.getRatingsFromListByCampsiteId(campsiteId)
    val reviews = RatingRules.visibleReviews(ratings)
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.CANADA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .testTag("campsiteReviews"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Ratings & Reviews", style = MaterialTheme.typography.titleMedium)
        Text(ratingText(ratings), modifier = Modifier.testTag("campsiteAverageRating"))

        if (reviews.isEmpty()) {
            Text("No reviews yet")
        }
        reviews.forEach { review ->
            val name = RepositoryDatabase.profileRepository
                .getProfileFromListById(review.userId)?.name ?: "Anonymous"
            HorizontalDivider()
            Column(modifier = Modifier.testTag("reviewItem")) {
                Text(RatingRules.stars(review.rating), color = MaterialTheme.colorScheme.primary)
                Text("$name · ${dateFormat.format(review.createdAt.toDate())}",
                    style = MaterialTheme.typography.labelMedium)
                Text(review.review)
            }
        }
    }
}
