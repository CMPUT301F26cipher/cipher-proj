package com.example.cipherappproject.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.DateUtils
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.temporal.ChronoUnit
import java.util.Locale

/*
--------------------------------------------------------------------------------------
Program Name: ReservationConfirmedView.kt
Date Written: November 21, 2026
Author: Nethmini
Purpose: Confirmation shown to an entrant once their lottery reservation is made, with the
reservation's details and a CSV copy to download
US: 01.05.01, 01.05.02
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-21  Nethmini   1.01     Initial implementation
--------------------------------------------------------------------------------------
 */

object ReservationConfirmation {
    /**
     * The reservation [userId] made through [lotteryId], newest first if there are several
     * (e.g. one was rejected and the entrant won again), or null if none is found.
     */
    fun find(reservations: List<Reservation>, userId: String, lotteryId: String): Reservation? =
        reservations
            .filter { it.userId == userId && it.lotteryId == lotteryId }
            .maxByOrNull { it.createdAt }

    /** Nights in the stay; both dates are inclusive, so a one-day stay is 1 night. */
    fun nights(reservation: Reservation): Long {
        val start = DateUtils.parse(reservation.bookStartDate) ?: return 0
        val end = DateUtils.parse(reservation.bookEndDate) ?: return 0
        return ChronoUnit.DAYS.between(start, end) + 1
    }
}

/**
 * Shows the entrant's reservation for [lotteryId] as confirmed (US 01.05.01), with a button to
 * save it as a CSV (US 01.05.02). Reads the live reservation list, so a later cancellation or
 * rejection shows here too.
 */
@Composable
fun ReservationConfirmedView(
    lotteryId: String,
    entrantId: String,
    onViewReservations: () -> Unit
) {
    val reservation = ReservationConfirmation.find(
        RepositoryDatabase.reservationRepository.reservations.value, entrantId, lotteryId
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).testTag("reservationConfirmed"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (reservation == null) {
            Text("You've reserved through this lottery.", style = MaterialTheme.typography.titleMedium)
            Text("Your reservation details are still loading.")
        } else {
            val helpers = ReservationListHelpers(reservation, isOwner = false)
            val nights = ReservationConfirmation.nights(reservation)
            Text(
                when {
                    ReservationList.isCancelled(reservation) -> "Reservation ${reservation.status.lowercase()}"
                    else -> "Reservation confirmed!"
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag("confirmationTitle")
            )
            Text("Campsite: ${helpers.formattedCampsiteName()}")
            Text("Dates: ${helpers.formattedBookingPeriod()}")
            Text("Nights: $nights")
            Text("Price per night: $" + String.format(Locale.US, "%.2f", reservation.pricePerNight))
            Text("Total: $" + String.format(Locale.US, "%.2f", reservation.totalPrice))
            Text("Reservation ID: ${reservation.reservationId}")

            ReservationCsv.DownloadCsvButton(
                fileName = "reservation_${reservation.reservationId}.csv",
                buildCsv = {
                    ReservationCsv.build(listOf(reservation)) {
                        RepositoryDatabase.campsiteRepository.getCampsiteFromListById(it)?.campsiteName
                            ?: "Unknown campsite"
                    }
                }
            )
        }

        Button(onClick = onViewReservations, modifier = Modifier.testTag("viewReservations")) {
            Text("View my reservations")
        }
    }
}
