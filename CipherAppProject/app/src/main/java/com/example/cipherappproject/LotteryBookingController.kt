package com.example.cipherappproject

import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.ReservationStatus
import com.example.cipherappproject.database.exceptions.OperationException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.LocalDate

/**
 * Handles a winning entrant's campsite and date selection for US 01.04.01,
 * 01.04.02 and 01.04.03.
 *
 * Reads availability from the shared repositories' local lists (no Firestore
 * queries here) and writes through the repositories' public methods.
 *
 * Date convention (same as CampsiteAvailabilityRepository.getAvailableBookingDates):
 * start and end dates are both inclusive, so a stay is "first selected date" to
 * "last selected date", and the dates must be consecutive.
 */

class LotteryBookingController {
    // Shared instances (see Database_Documentation.md): never create new repositories here
    private val availabilityRepository get() = RepositoryDatabase.campsiteAvailabilityRepository
    private val reservationRepository get() = RepositoryDatabase.reservationRepository
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository

    private var selectedCampsiteId: String? = null
    private val selectedDates = sortedSetOf<String>()

    /** Only an entrant whose entry is a winner can book. */
    fun canBook(entry: LotteryEntry?): Boolean {
        return entry?.status == LotteryEntryStatus.WINNER.name
    }

    /** Returns campsite choices belonging to the lottery. */
    fun getCampsiteChoices(lottery: Lottery): List<String> {
        return lottery.campsiteIds
    }

    /** Selects a valid campsite and clears the previous date selection. */
    fun selectCampsite(lottery: Lottery, campsiteId: String) {
        if (campsiteId !in lottery.campsiteIds) return
        if (campsiteId != selectedCampsiteId) {
            selectedCampsiteId = campsiteId
            clearSelectedDates()
        }
    }

    /** True once the availability and reservation lists have both finished loading. */
    fun isAvailabilityLoaded(): Boolean {
        return availabilityRepository.isCampsiteAvailabilitiesLoaded.value &&
                reservationRepository.isReservationsLoaded.value
    }

    /**
     * Dates ("yyyy-MM-dd", earliest first) the campsite can still be booked on, or null
     * while the data is loading. Past dates are dropped.
     */
    fun getAvailableDates(campsiteId: String, today: LocalDate = LocalDate.now()): List<String>? {
        if (!isAvailabilityLoaded()) return null
        return availabilityRepository.getAvailableBookingDates(campsiteId)
            .filter { !it.isBefore(today) }
            .map { it.toString() }
    }

    /*
        Check that chosen campsite belongs to the lottery and that selected dates are
        nonempty, available, valid and consecutive.

        Returns an error message, or null when the selection is valid.
        Does not reserve anything.
     */
    fun validateSelection(lottery: Lottery): String? {
        val campsiteId = selectedCampsiteId
            ?: return "Choose a campsite."
        return validateSelection(lottery, getAvailableDates(campsiteId))
    }

    /** [validateSelection] against an already known list of available dates (null = loading). */
    fun validateSelection(lottery: Lottery, available: List<String>?): String? {
        val campsiteId = selectedCampsiteId
            ?: return "Choose a campsite."

        if (campsiteId !in lottery.campsiteIds) {
            return "Choose a campsite from this lottery."
        }

        if (selectedDates.isEmpty()) {
            return "Select at least one date."
        }

        if (available == null) return "Wait for available dates to load."

        if (!available.containsAll(selectedDates)) {
            return "Some selected dates are unavailable."
        }

        val dates = selectedDates.map { date ->
            DateUtils.parse(date)
                ?: return "A selected date is invalid."
        }.sorted()

        if (!dates.zipWithNext().all { (first, second) ->
                second == first.plusDays(1)
            }
        ) {
            return "Choose consecutive dates."
        }

        return null
    }

    /**
     * Adds an available date, only if it extends the current consecutive selection.
     * Returns true if the date was added.
     */
    fun selectDate(date: String): Boolean {
        val campsiteId = selectedCampsiteId ?: return false
        return selectDate(date, getAvailableDates(campsiteId) ?: return false)
    }

    /** [selectDate] against an already known list of available dates. */
    fun selectDate(date: String, available: List<String>): Boolean {
        if (selectedCampsiteId == null) return false
        if (date !in available || date in selectedDates) return false

        if (selectedDates.isEmpty() || isNextToSelection(date)) {
            selectedDates.add(date)
            return true
        }
        return false
    }

    /**
     * Removes a selected date (only from either end, so the selection stays consecutive).
     * Returns true if the date was removed.
     */
    fun deselectDate(date: String): Boolean {
        if (selectedDates.isEmpty()) return false
        if (date == selectedDates.first() || date == selectedDates.last()) {
            return selectedDates.remove(date)
        }
        return false
    }

    /**
     * Replaces the selection with every date from [start] to [end] (both inclusive,
     * "yyyy-MM-dd"); [end] null means a one-night stay. Used by the calendar picker.
     *
     * Returns an error message and leaves nothing selected if the range is invalid or
     * includes a date that can't be booked, otherwise null.
     */
    fun selectRange(start: String, end: String?): String? {
        val campsiteId = selectedCampsiteId ?: return "Choose a campsite."
        val available = getAvailableDates(campsiteId)
            ?: return "Wait for available dates to load."
        return selectRange(start, end, available)
    }

    /** [selectRange] against an already known list of available dates. */
    fun selectRange(start: String, end: String?, available: List<String>): String? {
        clearSelectedDates()
        if (selectedCampsiteId == null) return "Choose a campsite."

        val first = DateUtils.parse(start) ?: return "A selected date is invalid."
        val last = if (end == null) first else DateUtils.parse(end) ?: return "A selected date is invalid."
        if (last.isBefore(first)) return "The stay must end on or after the day it starts."

        val stay = generateSequence(first) { it.plusDays(1) }
            .takeWhile { !it.isAfter(last) }
            .map { it.toString() }
            .toList()
        if (!available.containsAll(stay)) {
            return "Your stay includes dates that can't be booked. Pick dates without a greyed-out day in between."
        }
        selectedDates.addAll(stay)
        return null
    }

    /** Returns the selected campsite ID, or null if none is selected. */
    fun getSelectedCampsiteId(): String? {
        return selectedCampsiteId
    }

    /** Returns the selected dates. */
    fun getSelectedDates(): Set<String> {
        return selectedDates.toSortedSet()
    }

    /** Clears selected dates. */
    fun clearSelectedDates() {
        selectedDates.clear()
    }

    private fun isNextToSelection(date: String): Boolean {
        val parsed = DateUtils.parse(date) ?: return false
        val first = DateUtils.parse(selectedDates.first()) ?: return false
        val last = DateUtils.parse(selectedDates.last()) ?: return false
        return parsed == first.minusDays(1) || parsed == last.plusDays(1)
    }

    /** The selected stay as (first date, last date), both inclusive, or null if nothing selected. */
    fun getStay(): Pair<String, String>? {
        if (selectedDates.isEmpty()) return null
        return Pair(selectedDates.first(), selectedDates.last())
    }

    /** Local lookups (the repositories' lists), so screens stay in sync with Firestore. */
    fun getLottery(lotteryId: String): Lottery? {
        return RepositoryDatabase.lotteryRepository.getLotteryFromListById(lotteryId)
    }

    fun getEntry(lotteryId: String, entrantId: String): LotteryEntry? {
        return entryRepository.getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, entrantId)
    }

    /** Declines the winning offer (US 01.04.03) by setting the entry to DECLINED. */
    fun declineOffer(entry: LotteryEntry, onResult: (Result<Unit>) -> Unit) {
        if (!canBook(entry)) {
            onResult(Result.failure(OperationException("You do not have an active winning offer.")))
            return
        }
        entryRepository.updateLotteryEntry(
            entry.copy(status = LotteryEntryStatus.DECLINED.name),
            onResult
        )
    }

    /**
     * Reserves the current selection: re-validates it, creates the reservation, then marks
     * the entry ACCEPTED.
     *
     * NOTE: these are two separate writes (no cross-collection transaction exists yet), so a
     * failure in the second one can leave a reservation whose entry is still WINNER.
     */
    fun reserve(lottery: Lottery, entry: LotteryEntry, onResult: (Result<Unit>) -> Unit) {
        if (!canBook(entry)) {
            onResult(Result.failure(OperationException("You do not have an active winning offer.")))
            return
        }

        val error = validateSelection(lottery)
        val campsiteId = selectedCampsiteId
        val stay = getStay()
        if (error != null || campsiteId == null || stay == null) {
            onResult(Result.failure(ValidationException(error ?: "Select dates first.")))
            return
        }

        val pricePerNight = campsiteRepository.getCampsiteFromListById(campsiteId)?.pricePerNight ?: 0.0

        reservationRepository.createReservation(
            userId = entry.userId,
            campsiteId = campsiteId,
            lotteryId = lottery.lotteryId,
            status = ReservationStatus.ACTIVE.name,
            bookStartDate = stay.first,
            bookEndDate = stay.second,
            pricePerNight = pricePerNight,
            totalPrice = pricePerNight * selectedDates.size
        ) { createResult ->
            createResult
                .onSuccess {
                    entryRepository.updateLotteryEntry(
                        entry.copy(status = LotteryEntryStatus.ACCEPTED.name),
                        onResult
                    )
                }
                .onFailure { exception -> onResult(Result.failure(exception)) }
        }
    }
}
