package com.example.cipherappproject
/*
--------------------------------------------------------------------------------------
Program Name: SearchFilter.kt
Date Written: Nov 2 2026
Author: Bailey Parrish
Purpose: Class that stores input filter specifications
US: 01.01.02
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-02  Bailey      1.01     Built (name only)
2026-11-18  Madison     1.02     Added location, guests, dates and input validation
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */
import java.io.Serializable
import java.time.LocalDate
/**
 * The filters an entrant chose. A blank or null field means don't filter on this
 *
 * @property name text the campsite name must contain
 * @property location text the campsite's location name must contain
 * @property guests the campsite must hold at least this many guests
 * @property checkIn first night of the stay
 * @property checkOut the day the entrant leaves (that night is not included)
 */
data class SearchFilter(
    val name: String = "",
    val location: String = "",
    val guests: Int? = null,
    val checkIn: LocalDate? = null,
    val checkOut: LocalDate? = null,
) : Serializable {

    companion object {
        /**
         * Builds a SearchFilter from the text the entrant typed.
         *
         * @throws SearchException with a message to show the entrant if any input is invalid
         */
        fun fromInput(
            name: String,
            location: String,
            guests: String,
            checkIn: String,
            checkOut: String
        ): SearchFilter {
            val guestCount = if (guests.isBlank()) {
                null
            } else {
                guests.trim().toIntOrNull()?.takeIf { it > 0 }
                    ?: throw SearchException("Number of guests must be a whole number above 0.")
            }

            val checkInDate = if (checkIn.isBlank()) {
                null
            } else {
                DateUtils.parse(checkIn)
                    ?: throw SearchException("Check-in date must be in the format yyyy-MM-dd.")
            }

            val checkOutDate = if (checkOut.isBlank()) {
                null
            } else {
                DateUtils.parse(checkOut)
                    ?: throw SearchException("Check-out date must be in the format yyyy-MM-dd.")
            }

            if ((checkInDate == null) != (checkOutDate == null)) {
                throw SearchException("Enter both a check-in and a check-out date.")
            }
            if (checkInDate != null && checkOutDate != null && !checkOutDate.isAfter(checkInDate)) {
                throw SearchException("Check-out must be after check-in.")
            }

            return SearchFilter(name.trim(), location.trim(), guestCount, checkInDate, checkOutDate)
        }
    }
}