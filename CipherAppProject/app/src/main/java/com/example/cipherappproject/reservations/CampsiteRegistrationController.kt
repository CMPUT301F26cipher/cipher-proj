package com.example.cipherappproject.reservations

import com.example.cipherappproject.database.enums.CampsiteAvailabilityStatus
import com.example.cipherappproject.database.enums.CampsiteStatus
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.LocalDate

/*
--------------------------------------------------------------------------------------
Program Name: CampsiteRegistrationController
Purpose: Validates the "register campsite" form and saves the new campsite
US: 02.01.01
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-20  Hye Sung   1.01     Initial implementation
--------------------------------------------------------------------------------------
 */

/** Raw text from the register-campsite form. Comma separated lists for amenities/tags/rules. */
data class CampsiteFormInput(
    val name: String = "",
    val locationName: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val description: String = "",
    val maxGuests: String = "",
    val pricePerNight: String = "",
    val amenities: String = "",
    val tags: String = "",
    val rules: String = "",
    /** Booking period, yyyy-MM-dd, both days inclusive (US 02.01.02) */
    val bookingStart: String = "",
    val bookingEnd: String = ""
)

class CampsiteRegistrationController {
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository
    private val availabilityRepository get() = RepositoryDatabase.campsiteAvailabilityRepository

    /** Error message for the first problem in [input], or null if it can be registered. */
    fun validate(input: CampsiteFormInput, today: LocalDate = LocalDate.now()): String? {
        if (input.name.isBlank()) return "Campsite name is required"
        if (input.locationName.isBlank()) return "Location is required"

        val lat = input.latitude.trim().toDoubleOrNull()
        val lon = input.longitude.trim().toDoubleOrNull()
        if (lat == null || lat !in -90.0..90.0) return "Latitude must be a number between -90 and 90"
        if (lon == null || lon !in -180.0..180.0) return "Longitude must be a number between -180 and 180"

        if (input.description.isBlank()) return "Description is required"

        val guests = input.maxGuests.trim().toIntOrNull()
        if (guests == null || guests < 1) return "Maximum guests must be a whole number of at least 1"

        val price = input.pricePerNight.trim().toDoubleOrNull()
        if (price == null || price < 0.0 || price.isNaN() || price.isInfinite()) {
            return "Price per night must be a number, 0 or more"
        }
        return validateBookingPeriod(input.bookingStart, input.bookingEnd, today)
    }

    /**
     * The booking period is the dates entrants can reserve (start and end both inclusive).
     * Error message, or null if valid.
     */
    fun validateBookingPeriod(start: String, end: String, today: LocalDate = LocalDate.now()): String? {
        val startDate = runCatching { LocalDate.parse(start.trim()) }.getOrNull()
            ?: return "Booking period start must be a date like 2026-12-01"
        val endDate = runCatching { LocalDate.parse(end.trim()) }.getOrNull()
            ?: return "Booking period end must be a date like 2026-12-31"
        if (endDate.isBefore(startDate)) return "Booking period can't end before it starts"
        if (endDate.isBefore(today)) return "Booking period can't be entirely in the past"
        return null
    }

    /** "a, b,, c" -> ["a", "b", "c"] */
    fun splitList(text: String): MutableList<String> =
        text.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()

    /**
     * Validate then save. [result] gets null on success (the campsite appears in the owner's
     * list through the repository's live listener) or a message to show the owner.
     */
    fun register(ownerId: String, input: CampsiteFormInput, result: (String?) -> Unit) {
        validate(input)?.let { result(it); return }
        if (ownerId.isBlank()) {
            result("Your profile is still loading. Please try again.")
            return
        }

        campsiteRepository.addCampsite(
            ownerId = ownerId,
            campsiteName = input.name.trim(),
            // same layout the entrant campsite page reads: [latitude, longitude, name]
            mapLocation = listOf(input.latitude.trim(), input.longitude.trim(), input.locationName.trim()),
            description = input.description.trim(),
            amenities = splitList(input.amenities),
            maxGuests = input.maxGuests.trim().toInt(),
            pricePerNight = input.pricePerNight.trim().toDouble(),
            imagePath = "",
            tags = splitList(input.tags),
            qrCodeImagePath = "",
            status = CampsiteStatus.ACTIVE.name,
            rules = splitList(input.rules),
            policies = mutableListOf()
        ) { addResult ->
            val campsiteId = addResult.getOrNull()
            if (campsiteId == null) {
                result(addResult.exceptionOrNull()?.message ?: "Could not register the campsite.")
                return@addCampsite
            }

            // The booking period is stored as an ACTIVE availability for the new campsite;
            // this is what the booking screen offers winners (see LotteryBookingController)
            availabilityRepository.createCampsiteAvailability(
                campsiteId = campsiteId,
                startDate = input.bookingStart.trim(),
                endDate = input.bookingEnd.trim(),
                status = CampsiteAvailabilityStatus.ACTIVE.name
            ) { availabilityResult ->
                result(
                    if (availabilityResult.isSuccess) null
                    else "Campsite saved, but its booking period could not be saved: " +
                            (availabilityResult.exceptionOrNull()?.message ?: "unknown error")
                )
            }
        }
    }
}
