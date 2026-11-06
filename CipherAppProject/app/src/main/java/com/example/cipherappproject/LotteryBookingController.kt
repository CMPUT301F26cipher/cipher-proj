package com.example.cipherappproject

import com.example.cipherappproject.database.models.CampsiteAvailability
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.repositories.CampsiteAvailabilityRepository
import com.example.cipherappproject.database.repositories.LotteryEntryRepository
import com.example.cipherappproject.database.repositories.LotteryRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate

/**
 * Handles winning entrant campsite and night selection for US 01.04.01
 * and 01.04.02.
 * Outstanding issues: implement logic and connect shared eligibility and
 * availability interfaces; agree on date format and consecutive-night rules.
 */
class LotteryBookingController {
    /** Checks the entrant's active winning offer through the shared component. */

    private val lotteryRepository by lazy { LotteryRepository() }
    private val entryRepository by lazy { LotteryEntryRepository() }
    private val availabilityRepository by lazy { CampsiteAvailabilityRepository() }

    private var selectedCampsiteId: String? = null
    private var availableNights: Set<String> = emptySet()
    private val selectedNights = sortedSetOf<String>()

//    fun canBook(lotteryId: String, entrantId: String): Boolean {
//        // TODO: Check winner eligibility
//    }

    fun canBook(entry: LotteryEntry?): Boolean {
        return entry?.getStatus() == LotteryConstants.ENTRY_WINNER
    }

    /** Returns campsite choices belonging to the lottery. */
    fun getCampsiteChoices(lottery: Lottery): List<String> {
        return lottery.getLotteryCampsitesId()
    }

    /** Selects a valid campsite and clears the previous night selection. */
    fun selectCampsite(lottery: Lottery, campsiteId: String) {
        if (campsiteId !in lottery.getLotteryCampsitesId()) return
        if (campsiteId != selectedCampsiteId) {
            selectedCampsiteId = campsiteId
            availableNights = emptySet()
            clearSelectedNights()
        }
    }

    /*
        Check that chosen campsite belongs to Lottery and that selected nights are nonempty,
        available, valid and consecutive. 

        Return an error message, or null when the selection is valid. 
        Checks loaded availablility, and doesn't reserve the rights. 
     */
    fun validateSelection(lottery: Lottery): String? { 
         val campsiteId = selectedCampsiteId
        ?: return "Choose a campsite."

        if (campsiteId !in lottery.getLotteryCampsitesId()) {
            return "Choose a campsite from this lottery."
        }

        if (selectedNights.isEmpty()) {
            return "Select at least one night."
        }

        if (!availableNights.containsAll(selectedNights)) {
            return "Some selected nights are unavailable."
        }

        val dates = selectedNights.map { night ->
            DateUtils.parse(night)
                ?: return "A selected date is invalid."
        }.sorted()

        if (!dates.zipWithNext().all { (first, second) ->
                second == first.plusDays(1)
            }
        ) {
            return "Choose consecutive nights."
        }

        return null
    }

    /** Requests available nights from the team's availability component. */
    fun getAvailableNights(lotteryId: String, campsiteId: String): Set<String> {
        TODO("Load available nights")
    }

    /** Adds an available night according to the agreed stay rules. */
    fun selectNight(night: String) {
        if (night !in availableNights || night in selectedNights) return

        if (selectedNights.isEmpty() || isNextToSelection(night)) {
            selectedNights.add(night)
        }
    }

    /** Removes a selected night. */
    fun deselectNight(night: String) {
        if (selectedNights.isEmpty()) return
        if (night == selectedNights.first() || night == selectedNights.last()) {
            selectedNights.remove(night)
        }
    }

    /** Returns the selected campsite ID, or null if none is selected. */
    fun getSelectedCampsiteId(): String? {
        return selectedCampsiteId
    }

    /** Returns the selected nights. */
    fun getSelectedNights(): Set<String> {
        return selectedNights.toSortedSet()
    }

    /** Clears selected nights. */
    fun clearSelectedNights() {
        selectedNights.clear()
    }

    fun setAvailableNights(nights: Set<String>) {
        availableNights = nights
    }

    private fun isNextToSelection(night: String): Boolean {
        val date = DateUtils.parse(night) ?: return false
        val first = DateUtils.parse(selectedNights.first()) ?: return false
        val last = DateUtils.parse(selectedNights.last()) ?: return false
        return date == first.minusDays(1) || date == last.plusDays(1)
    }

    fun calculateAvailableNights(
        availabilities: List<CampsiteAvailability>,
        reservations: List<Reservation>,
        today: LocalDate = LocalDate.now()
    ): Set<String> {
        // 1. Every night the owner opened
        val openNights = mutableSetOf<LocalDate>()
        for (availability in availabilities) {
            if (availability.getCampsiteAvailabilityStatus() != LotteryConstants.AVAILABILITY_OPEN) continue
            val start = DateUtils.parse(availability.getCampsiteAvailabilityStartDate()) ?: continue
            val end = DateUtils.parse(availability.getCampsiteAvailabilityEndDate()) ?: continue
            openNights.addAll(DateUtils.nightsBetween(start, end))
        }

        // 2. Remove nights someone has already booked
        for (reservation in reservations) {
            if (reservation.getStatus().lowercase() in LotteryConstants.FREED_RESERVATION_STATUSES) continue
            val start = DateUtils.parse(reservation.getBookStartDate()) ?: continue
            val end = DateUtils.parse(reservation.getBookEndDate()) ?: continue
            openNights.removeAll(DateUtils.nightsBetween(start, end).toSet())
        }

        // 3. Drop nights that have already passed
        return openNights
            .filter { night -> !night.isBefore(today) }
            .map { night -> night.toString() }
            .toSortedSet()
    }

    fun getStay(): Pair<String, String>? {
        if (selectedNights.isEmpty()) return null
        val lastNight = DateUtils.parse(selectedNights.last()) ?: return null
        return Pair(selectedNights.first(), lastNight.plusDays(1).toString())
    }

    fun loadLottery(lotteryId: String, onResult: (Lottery?) -> Unit) {
        lotteryRepository.getLotteryById(lotteryId, onResult)
    }

    fun loadEntry(lotteryId: String, entrantId: String, onResult: (LotteryEntry?) -> Unit) {
        entryRepository.getEntryForUser(lotteryId, entrantId, onResult)
    }

    fun loadAvailableNights(campsiteId: String, onResult: (Set<String>?) -> Unit) {
        availabilityRepository.getAvailabilitiesByCampsiteId(campsiteId) { availabilities ->
            if (availabilities == null) {
                onResult(null)
                return@getAvailabilitiesByCampsiteId
            }
            loadReservationsForCampsite(campsiteId) { reservations ->
                if (reservations == null) {
                    onResult(null)
                } else {
                    val nights = calculateAvailableNights(availabilities, reservations)
                    // Ignore a late answer if the entrant has switched campsite since
                    if (campsiteId == selectedCampsiteId) {
                        setAvailableNights(nights)
                    }
                    onResult(nights)
                }
            }
        }
    }

    private fun loadReservationsForCampsite(
        campsiteId: String,
        onResult: (List<Reservation>?) -> Unit
    ) {
        Firebase.firestore.collection("reservations")
            .whereEqualTo("campsiteId", campsiteId)
            .get()
            .addOnSuccessListener { documents ->
                onResult(documents.toObjects(Reservation::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun declineOffer(entry: LotteryEntry, onResult: (Boolean) -> Unit) {
        if (!canBook(entry)) {
            onResult(false)
            return
        }
        entryRepository.updateEntryStatus(
            lotteryId = entry.getLotteryId(),
            lotteryEntryId = entry.getLotteryEntryId(),
            newStatus = LotteryConstants.ENTRY_DECLINED,
            result = onResult
        )
    }
}
