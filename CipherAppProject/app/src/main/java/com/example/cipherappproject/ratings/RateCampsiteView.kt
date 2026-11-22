package com.example.cipherappproject.ratings

/*
--------------------------------------------------------------------------------------
Program Name: RateCampsiteView.kt
Date Written: November 21, 2026
Author: Nethmini Mackenzy
Purpose: Screen where an entrant gives a star rating and optional review for a past stay
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.example.cipherappproject.ui.lottery.CenteredMessage
import com.example.cipherappproject.ui.lottery.LoadingIndicator

/**
 * Rate-and-review screen for one of the entrant's reservations. Pre-fills an existing rating
 * so the entrant can edit it.
 *
 * @param reservationId the reservation being rated
 * @param userId the current entrant
 * @param onDone called after the rating is saved
 */
@Composable
fun RateCampsiteView(reservationId: String, userId: String, onDone: () -> Unit) {
    val reservationRepository = RepositoryDatabase.reservationRepository
    val ratingRepository = RepositoryDatabase.ratingRepository

    if (!reservationRepository.isReservationsLoaded.value || !ratingRepository.isRatingsLoaded.value) {
        LoadingIndicator()
        return
    }

    val reservation = reservationRepository.getReservationFromListById(reservationId)
    if (reservation == null || !RatingRules.canRate(reservation, userId)) {
        CenteredMessage("You can only rate a campsite after your stay.")
        return
    }

    val controller = remember { RatingController() }
    val existing = remember(reservationId) { controller.existingRating(reservationId, userId) }
    if (RatingRules.isLocked(existing)) {
        CenteredMessage("Your rating was removed by an admin and can't be changed.")
        return
    }

    val campsiteName = RepositoryDatabase.campsiteRepository
        .getCampsiteFromListById(reservation.campsiteId)?.campsiteName ?: "this campsite"

    var stars by remember { mutableIntStateOf(existing?.rating?.toInt() ?: 0) }
    var review by remember { mutableStateOf(existing?.review ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("rateCampsiteView"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (existing == null) "Rate $campsiteName" else "Edit your rating for $campsiteName",
            style = MaterialTheme.typography.headlineSmall
        )

        StarPicker(selected = stars, onSelect = { stars = it; error = null })

        OutlinedTextField(
            value = review,
            onValueChange = { review = it; error = null },
            label = { Text("Review (optional)") },
            supportingText = { Text("${review.trim().length} / ${RatingRules.MAX_REVIEW_LENGTH}") },
            isError = review.trim().length > RatingRules.MAX_REVIEW_LENGTH,
            minLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reviewField")
        )

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("ratingError"))
        }

        Button(
            enabled = !saving,
            onClick = {
                saving = true
                controller.submit(reservation, userId, stars, review) { result ->
                    saving = false
                    result
                        .onSuccess { onDone() }
                        .onFailure { error = it.message ?: "Could not save your rating." }
                }
            },
            modifier = Modifier.testTag("submitRatingButton")
        ) {
            Text(if (existing == null) "Submit" else "Update")
        }
    }
}

/** Row of 5 tappable stars; [selected] stars are filled. */
@Composable
fun StarPicker(selected: Int, onSelect: (Int) -> Unit) {
    Row {
        for (i in RatingRules.MIN_STARS..RatingRules.MAX_STARS) {
            TextButton(
                onClick = { onSelect(i) },
                modifier = Modifier
                    .testTag("star$i")
                    .semantics { contentDescription = "$i star" + if (i > 1) "s" else "" }
            ) {
                Text(
                    if (i <= selected) "★" else "☆",
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
