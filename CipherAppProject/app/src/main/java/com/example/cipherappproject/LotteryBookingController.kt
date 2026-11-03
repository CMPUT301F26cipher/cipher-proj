package com.example.cipherappproject

/**
 * Handles winning entrant campsite and night selection for US 01.04.01
 * and 01.04.02.
 * Outstanding issues: implement logic and connect shared eligibility and
 * availability interfaces; agree on date format and consecutive-night rules.
 */
class LotteryBookingController {
    /** Checks the entrant's active winning offer through the shared component. */


    private var selectedCampsiteId: String? = null

    private val availableNights: MutableSet<String> = mutableSetOf() // represents Nights available for the currently selected campsite.

    private val selectedNights: MutableSet<String> = mutableSetOf() // represents Nights currently chosen by the entrant.
    fun canBook(lotteryId: String, entrantId: String): Boolean {
        // TODO: Check winner eligibility
    }

    /** Returns campsite choices belonging to the lottery. */
    fun getCampsiteChoices(lottery: Lottery): List<String> {
        // TODO: Return lottery campsite IDs
    }

    /** Selects a valid campsite and clears the previous night selection. */
    fun selectCampsite(lottery: Lottery, campsiteId: String) {
        // TODO: Validate membership and change selected campsite
    }

    /** Requests available nights from the team's availability component. */
    fun getAvailableNights(lotteryId: String, campsiteId: String): Set<String> {
        // TODO: Load available nights
    }

    /** Adds an available night according to the agreed stay rules. */
    fun selectNight(night: String) {
        // TODO: Validate and select night
    }

    /** Removes a selected night. */
    fun deselectNight(night: String) {
        // TODO: Remove selected night
    }

    /** Returns the selected campsite ID, or null if none is selected. */
    fun getSelectedCampsiteId(): String? {
        // TODO: Return selected campsite ID
    }

    /** Returns the selected nights. */
    fun getSelectedNights(): Set<String> {
        // TODO: Return selected nights
    }

    /** Clears selected nights. */
    fun clearSelectedNights() {
        // TODO: Clear selected nights
    }
}
