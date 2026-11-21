package com.example.cipherappproject.reservations

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cipherappproject.database.models.Reservation

object ReservationCsv {
    val HEADER = listOf("Reservation ID", "Campsite", "Start date", "End date",
        "Status", "Price per night", "Total price")

    // builds CSV text. campsiteName maps a campsiteId to a display name. */
    fun build(reservations: List<Reservation>, campsiteName: (String) -> String): String {
        // TODO: header row + one row per reservation, escape commas/quotes
        return HEADER.joinToString(",")
    }

    // Save a CSV through the system "Save as" picker
    @Composable
    fun DownloadCsvButton(fileName: String, buildCsv: () -> String) {
        // TODO: rememberLauncherForActivityResult(CreateDocument("text/csv"))
        OutlinedButton(onClick = { /* TODO */ }) { Text("Download CSV") }
    }
}