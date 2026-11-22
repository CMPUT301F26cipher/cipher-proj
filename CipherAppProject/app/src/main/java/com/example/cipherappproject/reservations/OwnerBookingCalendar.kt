package com.example.cipherappproject.reservations

/*
--------------------------------------------------------------------------------------
Program Name: OwnerBookingCalendar.kt
Date Written: 2026-11-21
Purpose: Month calendar showing an owner how full their campsite(s) are on each day,
opened from the owner's active reservations screen.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-21  Hye Sung   1.01     Built
--------------------------------------------------------------------------------------
Future Enhancements
Tap a day to list the reservations booked on it
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

object BookingCalendar {
    /**
     * How many of the given campsites are booked on each day of [month]. A stay covers its
     * start and end dates (both inclusive); only ACTIVE and PENDING reservations hold dates.
     */
    fun bookedCounts(
        reservations: List<Reservation>,
        campsiteIds: Set<String>,
        month: YearMonth
    ): Map<LocalDate, Int> {
        val counts = mutableMapOf<LocalDate, Int>()
        val holding = reservations.filter {
            it.campsiteId in campsiteIds &&
                (it.status == ReservationStatus.ACTIVE.name || it.status == ReservationStatus.PENDING.name)
        }
        // one campsite can't be booked twice on a day, so count each campsite once per day
        val perDay = mutableMapOf<LocalDate, MutableSet<String>>()
        for (r in holding) {
            val start = runCatching { LocalDate.parse(r.bookStartDate) }.getOrNull() ?: continue
            val end = runCatching { LocalDate.parse(r.bookEndDate) }.getOrNull() ?: continue
            var day = maxOf(start, month.atDay(1))
            val last = minOf(end, month.atEndOfMonth())
            while (!day.isAfter(last)) {
                perDay.getOrPut(day) { mutableSetOf() }.add(r.campsiteId)
                day = day.plusDays(1)
            }
        }
        perDay.forEach { (day, ids) -> counts[day] = ids.size }
        return counts
    }
}

/**
 * Month calendar of how full the owner's campsites are each day.
 *
 * @param ownerId the owner's profile id
 * @param onBack called when the owner leaves the calendar
 */
@Composable
fun OwnerBookingCalendar(ownerId: String, onBack: () -> Unit) {
    val campsites = RepositoryDatabase.campsiteRepository.getCampsiteFromListByOwnerId(ownerId)
    // reading .value makes the calendar update live when reservations change
    val reservations = RepositoryDatabase.reservationRepository.reservations.value

    var month by remember { mutableStateOf(YearMonth.now()) }
    var selectedCampsiteId by remember { mutableStateOf<String?>(null) } // null = all campsites

    val shownIds = selectedCampsiteId?.let { setOf(it) } ?: campsites.map { it.campsiteId }.toSet()
    val counts = BookingCalendar.bookedCounts(reservations, shownIds, month)
    val total = shownIds.size

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("ownerBookingCalendar"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack, modifier = Modifier.testTag("calendarBack")) { Text("Back") }
            Text("Booked dates", style = MaterialTheme.typography.headlineSmall)
        }

        // which campsite to show
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedCampsiteId == null, onClick = { selectedCampsiteId = null }, label = { Text("All") })
            campsites.forEach { c ->
                FilterChip(
                    selected = selectedCampsiteId == c.campsiteId,
                    onClick = { selectedCampsiteId = c.campsiteId },
                    label = { Text(c.campsiteName) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { month = month.minusMonths(1) }, modifier = Modifier.testTag("prevMonth")) { Text("<") }
            Text(
                "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("monthLabel")
            )
            TextButton(onClick = { month = month.plusMonths(1) }, modifier = Modifier.testTag("nextMonth")) { Text(">") }
        }

        // weekday header, Sunday first
        Row(Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            }
        }

        val leadingBlanks = month.atDay(1).dayOfWeek.value % 7 // Sunday = 0
        val cells: List<Int?> = List(leadingBlanks) { null } + (1..month.lengthOfMonth()).toList()
        cells.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (i in 0 until 7) {
                    val dayNum = week.getOrNull(i)
                    if (dayNum == null) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val booked = counts[month.atDay(dayNum)] ?: 0
                        DayCell(dayNum, booked, total, Modifier.weight(1f))
                    }
                }
            }
        }

        Text(
            if (total <= 1) "Green: free. Red: booked."
            else "Each day shows booked / total campsites. Darker means fuller.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DayCell(day: Int, booked: Int, total: Int, modifier: Modifier) {
    val fill = if (total == 0) 0f else booked.toFloat() / total
    val color = when {
        booked == 0 -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.25f + 0.6f * fill)
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(color)
            .testTag("day_$day"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(day.toString(), style = MaterialTheme.typography.labelMedium)
            if (total > 1 && booked > 0) Text("$booked/$total", style = MaterialTheme.typography.labelSmall)
        }
    }
}
