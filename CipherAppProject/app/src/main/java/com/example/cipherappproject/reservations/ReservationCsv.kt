package com.example.cipherappproject.reservations

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cipherappproject.database.models.Reservation
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import java.util.Locale


/**
 * Build reservation and cancellation confirmations as CSV text that entrants can download.
 * Satisfy US 01.05.02, 01.07.02
 */
object ReservationCsv {
    val HEADER = listOf(
        "Reservation ID", "Campsite", "Start date", "End date",
        "Status", "Price per night", "Total price"
    )

    // turn reservations into CSV text: a header row, and then one row per reservation
    fun build(reservations: List<Reservation>, campsiteName: (String) -> String): String {
        val rows = reservations.map { r ->
            listOf(
                r.reservationId,
                campsiteName(r.campsiteId),
                r.bookStartDate,
                r.bookEndDate,
                r.status,
                String.format(Locale.US, "%.2f", r.pricePerNight),
                String.format(Locale.US, "%.2f", r.totalPrice)
            )
        }
        return (listOf(HEADER) + rows).joinToString("\n") { row ->
            row.joinToString(",") { escape(it) }
    }
}

    // Wrap a value in quotes if it contains comma, quote or newline. Ensure csv stays valid.
    fun escape(value: String): String {
        return if (value.any { it == ',' || it == '"' || it == '\n' }) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else value
    }

    /**
     * CSV button that opens the system "Save as" picker and writes the file there
     *
     * @param filename, it is the suggested filename
     * @param buildCsv, creates the CSV text when the user picks a location.
     */
    @Composable
    fun DownloadCsvButton(fileName: String, buildCsv: () -> String, modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("text/csv")
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult // user didnt want csv
            try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(buildCsv().toByteArray()) }
                Toast.makeText(context, "Saved $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not save file", Toast.LENGTH_SHORT).show()
            }
        }
        OutlinedButton(
            onClick = {launcher.launch(fileName)},
            modifier = modifier.testTag("downloadCsv")
        ) { Text("CSV")}
    }
}