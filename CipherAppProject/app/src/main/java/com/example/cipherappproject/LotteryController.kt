package com.example.cipherappproject

import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.exceptions.PermissionException
import com.example.cipherappproject.database.exceptions.ValidationException
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.LocalDate

/**
 * Handles owner lottery creation and validation for US 02.04.01, 02.04.05, 02.04.07 and 02.04.08
 * Outstanding issues: implement logic and connect shared ownership/persistence
 * interfaces. Date semantics and capacity rules require team agreement.
 */

class LotteryController {
    // Shared instances (see Database_Documentation.md): never create new repositories here
    private val repository get() = RepositoryDatabase.lotteryRepository
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository

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
            status = LotteryStatus.OPEN.name
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

        saveNewLottery(lottery) { saveResult ->
            saveResult
                .onSuccess { lotteryId -> onSuccess(lotteryId) }
                .onFailure { exception ->
                    onFailure(exception.message ?: "Could not save lottery. Please try again.")
                }
        }
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

    fun canEdit(lottery: Lottery): Boolean = lottery.status == LotteryStatus.OPEN.name

    fun toFormInput(lottery: Lottery): LotteryFormInput {
        val capacityText =
            if (lottery.maxParticipants == LotteryConstants.UNLIMITED_PARTICIPANTS) ""
            else lottery.maxParticipants?.toString() ?: ""

        return LotteryFormInput(
            campsiteIds = lottery.campsiteIds.toSet(),
            registrationStart = lottery.startDate,
            registrationEnd = lottery.endDate,
            numWinners = lottery.numWinners.toString(),
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

    /** Loads one lottery from Firestore. */
    fun loadLottery(lotteryId: String, onResult: (Result<Lottery>) -> Unit) {
        repository.getLotteryById(lotteryId, onResult)
    }

    /**
     * Checks that every campsite exists and belongs to [ownerId].
     *
     * This replaces the check that used to live in LotteryRepository.saveNewLottery
     * (now commented out): it uses the campsite repository's local list, so
     * the campsites must have finished loading.
     */

    // TODO (US 02.04.08): also accept campsites owned by lottery.coOwnerIds
    fun checkCampsiteOwnership(ownerId: String, campsiteIds: Collection<String>): Result<Unit> {
        if (!campsiteRepository.isCampsitesLoaded.value) {
            return Result.failure(ValidationException("Campsites are still loading. Please try again."))
        }
        val allOwned = campsiteIds.all { id ->
            campsiteRepository.getCampsiteFromListById(id)?.ownerId == ownerId
        }
        return if (allOwned) Result.success(Unit)
        else Result.failure(PermissionException("You can only list your own campsites."))
    }

    /** Saves a new lottery after checking campsite ownership. Result holds the new lottery id. */
    fun saveNewLottery(lottery: Lottery, onResult: (Result<String>) -> Unit = {}) {
        val ownership = checkCampsiteOwnership(lottery.ownerId, lottery.campsiteIds)
        if (ownership.isFailure) {
            onResult(Result.failure(ownership.exceptionOrNull()!!))
            return
        }

        repository.createLottery(
            ownerId = lottery.ownerId,
            campsiteIds = lottery.campsiteIds.toMutableList(),
            numWinners = lottery.numWinners,
            startDate = lottery.startDate,
            endDate = lottery.endDate,
            maxParticipants = lottery.maxParticipants ?: LotteryConstants.UNLIMITED_PARTICIPANTS,
            status = lottery.status,
            result = onResult
        )
    }

    /** Returns a validation message, or null if the form was valid and the save was requested. */
    fun createFromForm(
        ownerId: String,
        input: LotteryFormInput,
        onResult: (Result<String>) -> Unit = {}
    ): String? {
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
        saveNewLottery(lottery, onResult)
        return null
    }

    /** Returns a validation message, or null if the form was valid and the update was requested. */
    fun updateLottery(
        original: Lottery,
        input: LotteryFormInput,
        onResult: (Result<Unit>) -> Unit = {}
    ): String? {
        if (!canEdit(original)) {
            return "This lottery has already been drawn and can no longer be edited"
        }
        val error = validate(input)
        if (error != null) return error

        val ownership = checkCampsiteOwnership(original.ownerId, input.campsiteIds)
        if (ownership.isFailure) return ownership.exceptionOrNull()?.message

        val capacityError = checkCapacityFitsWaitlist(original.lotteryId, parseCapacity(input.capacity))
        if (capacityError != null) return capacityError

        repository.updateLottery(applyChanges(original, input), onResult)
        return null
    }

    // ------------------------- Waitlist capacity (US 02.04.07) -------------------------

    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository

    /**
     * Number of entrants currently on the lottery's waitlist (everyone who joined and has not
     * left), or null while the entries are still loading.
     */
    fun getWaitlistSize(lotteryId: String): Int? {
        if (!entryRepository.isLotteriesEntriesLoaded.value) return null
        return entryRepository.getLotteryEntriesFromListByLotteryId(lotteryId)
            .count { it.status != LotteryEntryStatus.WITHDRAWN.name }
    }

    /** True if [capacity] is a set limit that [waitlistSize] entrants have already reached. */
    fun isFull(capacity: Int?, waitlistSize: Int): Boolean {
        if (capacity == null || capacity == LotteryConstants.UNLIMITED_PARTICIPANTS) return false
        return waitlistSize >= capacity
    }

    /**
     * True if the lottery's waitlist has reached its capacity, so no one else can join.
     * Treated as full while the entries are still loading, so the limit can't be skipped.
     * For the join-waitlist flow to call before adding an entry.
     */
    fun isWaitlistFull(lottery: Lottery): Boolean {
        val size = getWaitlistSize(lottery.lotteryId) ?: return true
        return isFull(lottery.maxParticipants, size)
    }

    /** Error message if [capacity] is lower than the number already on the waitlist, else null. */
    fun checkCapacityFitsWaitlist(lotteryId: String, capacity: Int?): String? {
        if (capacity == null) return null
        val size = getWaitlistSize(lotteryId)
            ?: return "The waitlist is still loading. Please try again."
        return capacityBelowWaitlistError(capacity, size)
    }

    /** Error message if [capacity] is below [waitlistSize], else null. */
    fun capacityBelowWaitlistError(capacity: Int, waitlistSize: Int): String? {
        return if (capacity < waitlistSize) {
            "Waitlist capacity can't be lower than the $waitlistSize entrants already on it"
        } else null
    }

    // ------------------------- Viewing / filtering (owner list) -------------------------

    /** The owner's lotteries with [status] (a LotteryStatus name), or all of them when null. */
    fun filterByStatus(lotteries: List<Lottery>, status: String?): List<Lottery> {
        return if (status == null) lotteries else lotteries.filter { it.status == status }
    }

    /** Readable text for a lottery status. */
    fun statusText(status: String): String {
        return when (status) {
            LotteryStatus.OPEN.name -> "Open"
            LotteryStatus.PROCESSING.name -> "Drawing"
            LotteryStatus.COMPLETED.name -> "Drawn"
            LotteryStatus.CANCELLED.name -> "Cancelled"
            else -> status
        }
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