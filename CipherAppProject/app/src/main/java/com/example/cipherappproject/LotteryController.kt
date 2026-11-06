package com.example.cipherappproject

import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.repositories.LotteryRepository
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Handles owner lottery creation and validation for US 02.04.01, 02.04.05,
 * and 02.04.07.
 * Outstanding issues: implement logic and connect shared ownership/persistence
 * interfaces. Date semantics and capacity rules require team agreement.
 */

class LotteryController {
    private val repository by lazy { LotteryRepository() }
    /** Validates settings and constructs a lottery; persistence is separate. */
    fun createLottery(
        ownerId: String,
        campsiteIds: List<String>,
        startDate: String,
        endDate: String,
        numberOfWinners: Int,
        capacity: Int? = null
    ): Lottery {
        require(ownerId.isNotBlank()) { "Missing owner" }
        require(campsiteIds.isNotEmpty()) { "Select at least one campsite" }
        require(isValidRegistrationPeriod(startDate, endDate)) { "Invalid registration period" }
        require(isValidWinnerCount(numberOfWinners)) { "Invalid number of winners" }
        require(isValidCapacity(capacity, numberOfWinners)) { "Invalid capacity" }

        return Lottery(
            ownerId = ownerId,
            campsiteIds = campsiteIds.toMutableList(),
            numWinners = numberOfWinners,
            startDate = startDate.trim(),
            endDate = endDate.trim(),
            maxParticipants = capacity ?: LotteryConstants.UNLIMITED_PARTICIPANTS,
            status = LotteryConstants.LOTTERY_OPEN,
            createdAt = LocalDateTime.now().toString()
        )
    }

    /*
        Validates owner's form, constructs a lottery, and requests its save. 

        Report validation/persistence failures through function onFailure
        Reports the saved lottery ID through onSuccess
     */
    fun createAndSaveLottery( // validate the owner's settings and save the lotter
        ownerId: String,
        input: LotteryFormInput,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (ownerId.isBlank()) {
        onFailure("An owner is required.")
        return
        }

        val error = validate(input)

        if (error != null) {
            onFailure(error)
            return
        }

        val lottery = try {
            createLottery(
                ownerId = ownerId,
                campsiteIds = input.campsiteIds.toList(),
                startDate = input.registrationStart,
                endDate = input.registrationEnd,
                numberOfWinners = input.numWinners.trim().toInt(),
                capacity = parseCapacity(input.capacity)
            )
        } catch (error: IllegalArgumentException) {
            onFailure(error.message ?: "Invalid lottery settings.")
            return
        }

        repository.saveNewLottery(
            lottery = lottery,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /** Checks date format and chronological order. */
    fun isValidRegistrationPeriod(startDate: String, endDate: String): Boolean {
        val start = DateUtils.parse(startDate) ?: return false
        val end = DateUtils.parse(endDate) ?: return false
        return !end.isBefore(start)
    }

    /** Checks that the winner count is positive. */
    fun isValidWinnerCount(numberOfWinners: Int): Boolean {
        return numberOfWinners > 0
    }

    /** Checks unlimited capacity or the team's agreed capacity rules. */
    fun isValidCapacity(capacity: Int?, numberOfWinners: Int): Boolean {
        if (capacity == null) return true
        return capacity >= numberOfWinners
    }

    // Added functions - Neth

    fun validate(input: LotteryFormInput, today: LocalDate = LocalDate.now()): String? {
        val winners = input.numWinners.trim().toIntOrNull()
        val capacityIsNumberOrBlank =
            input.capacity.isBlank() || input.capacity.trim().toIntOrNull() != null
        val registrationEnd = DateUtils.parse(input.registrationEnd)

        return when {
            input.campsiteIds.isEmpty() ->
                "Select at least one campsite"
            !isValidRegistrationPeriod(input.registrationStart, input.registrationEnd) ->
                "Registration dates must be yyyy-MM-dd, and it must end on or after the day it opens"
            registrationEnd != null && registrationEnd.isBefore(today) ->
                "Registration cannot end in the past"
            winners == null || !isValidWinnerCount(winners) ->
                "Number of winners must be a whole number above 0"
            !capacityIsNumberOrBlank ->
                "Waitlist capacity must be a number, or empty for unlimited"
            !isValidCapacity(parseCapacity(input.capacity), winners) ->
                "Waitlist capacity must be at least the number of winners"
            else -> null
        }
    }

    fun parseCapacity(capacityText: String): Int? = capacityText.trim().toIntOrNull()

    fun canEdit(lottery: Lottery): Boolean = lottery.getLotteryStatus() == LotteryConstants.LOTTERY_OPEN

    fun toFormInput(lottery: Lottery): LotteryFormInput {
        val capacityText =
            if (lottery.getLotteryMaxParticipants() == LotteryConstants.UNLIMITED_PARTICIPANTS) ""
            else lottery.getLotteryMaxParticipants()?.toString() ?: ""

        return LotteryFormInput(
            campsiteIds = lottery.getLotteryCampsitesId().toSet(),
            registrationStart = lottery.getLotteryStartDate(),
            registrationEnd = lottery.getLotteryEndDate(),
            numWinners = lottery.getLotteryNumWinners().toString(),
            capacity = capacityText
        )
    }

    fun applyChanges(original: Lottery, input: LotteryFormInput): Lottery {
        return original.copy(
            campsiteIds = input.campsiteIds.toMutableList(),
            startDate = input.registrationStart.trim(),
            endDate = input.registrationEnd.trim(),
            numWinners = input.numWinners.trim().toInt(),
            maxParticipants = parseCapacity(input.capacity) ?: LotteryConstants.UNLIMITED_PARTICIPANTS
        )
    }

    fun loadLottery(lotteryId: String, onResult: (Lottery?) -> Unit) {
        repository.getLotteryById(lotteryId, onResult)
    }

    fun saveNewLottery(lottery: Lottery, onResult: (String?) -> Unit = {}) {
        repository.createLottery(
            ownerId = lottery.getLotteryOwnerId(),
            campsiteIds = lottery.getLotteryCampsitesId().toMutableList(),
            numWinners = lottery.getLotteryNumWinners(),
            startDate = lottery.getLotteryStartDate(),
            endDate = lottery.getLotteryEndDate(),
            maxParticipants = lottery.getLotteryMaxParticipants() ?: LotteryConstants.UNLIMITED_PARTICIPANTS,
            status = lottery.getLotteryStatus(),
            createdAt = lottery.getLotteryCreatedAt(),
            result = onResult
        )
    }

    fun createFromForm(ownerId: String, input: LotteryFormInput): String? {
        val error = validate(input)
        if (error != null) return error

        val lottery = createLottery(
            ownerId = ownerId,
            campsiteIds = input.campsiteIds.toList(),
            startDate = input.registrationStart,
            endDate = input.registrationEnd,
            numberOfWinners = input.numWinners.trim().toInt(),
            capacity = parseCapacity(input.capacity)
        )
        saveNewLottery(lottery)
        return null
    }

    fun updateLottery(original: Lottery, input: LotteryFormInput): String? {
        if (!canEdit(original)) {
            return "This lottery has already been drawn and can no longer be edited"
        }
        val error = validate(input)
        if (error != null) return error

        repository.updateLottery(applyChanges(original, input)) {}
        return null
    }
}

/**
 * What the owner has typed into the lottery form.
 *
 * @property campsiteIds the owner's campsites included in this lottery (US 02.04.02)
 * @property capacity optional waitlist capacity; blank means unlimited (US 02.04.07)
 */
data class LotteryFormInput(
    val campsiteIds: Set<String> = emptySet(),
    val registrationStart: String = "",
    val registrationEnd: String = "",
    val numWinners: String = "",
    val capacity: String = ""
)
