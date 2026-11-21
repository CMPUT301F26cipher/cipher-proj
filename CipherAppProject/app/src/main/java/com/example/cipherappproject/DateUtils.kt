package com.example.cipherappproject

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Helpers for date related things.
 */
object DateUtils {

    private val displayFormat = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")

    /** Parses "yyyy-MM-dd". Returns null for blank or badly formatted text. */
    fun parse(text: String): LocalDate? {
        return try {
            LocalDate.parse(text.trim())
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /** Formats a date for display, e.g. "Fri, May 7, 2027". */
    fun display(date: LocalDate): String = date.format(displayFormat)

    /** Midnight UTC of [date] in epoch millis, the format Material3 date pickers use. */
    fun toUtcMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    /** The calendar day of a Material3 date picker value (epoch millis, UTC). */
    fun fromUtcMillis(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

    /** Every night from [start] up to, but not including, [end]. */
    fun nightsBetween(start: LocalDate, end: LocalDate): List<LocalDate> {
        val nights = mutableListOf<LocalDate>()
        var current = start
        while (current.isBefore(end)) {
            nights.add(current)
            current = current.plusDays(1)
        }
        return nights
    }
}
