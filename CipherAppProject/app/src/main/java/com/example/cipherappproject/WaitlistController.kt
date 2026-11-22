package com.example.cipherappproject
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.LocalDate
/*
--------------------------------------------------------------------------------------
Program Name:
Date Written:
Author:
Purpose:
US: 01.02.01
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Hye Sung   1.01     Adding DB variables, implementing usable join function
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


class WaitlistController {
    // Shared instances (see Database_Documentation.md): never create new repositories here
    private val lotteryRepository get() = RepositoryDatabase.lotteryRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository

    fun getWaitlist(
        lotteryId: String,
        result: (List<LotteryEntry>?) -> Unit
    ) {
        entryRepository.getLotteryEntriesByLotteryId(lotteryId) { entriesResult ->

            val entries = entriesResult.getOrNull()

            if (entries == null) {
                result(null)
                return@getLotteryEntriesByLotteryId
            }

            // Before the lottery draw, entrants have
            // the status REGISTERED.
            val waitlist = entries.filter {
                it.status == LotteryEntryStatus.REGISTERED.name
            }

            result(waitlist)
        }
    }

    /**
     * Find an open lottery for the campsite whose period covers the requested dates.
     * Uses the repository's locally cached list of lotteries (null if it has not loaded yet).
     */
    fun findAvailableLottery(
        campsiteId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        result: (Lottery?) -> Unit
    ) {
        if (!endDate.isAfter(startDate)) {
            result(null)
            return
        }

        if (!lotteryRepository.isLotteriesLoaded.value) {
            result(null)
            return
        }

        val match = lotteryRepository.lotteries.value.firstOrNull { lottery ->
            val lotteryStart = runCatching { LocalDate.parse(lottery.startDate) }.getOrNull()
            val lotteryEnd = runCatching { LocalDate.parse(lottery.endDate) }.getOrNull()

            campsiteId in lottery.campsiteIds &&
                    lottery.status == LotteryStatus.OPEN.name &&
                    lotteryStart != null &&
                    lotteryEnd != null &&
                    !startDate.isBefore(lotteryStart) &&
                    !endDate.isAfter(lotteryEnd)
        }

        result(match)
    }

    /** Why a join attempt succeeded or failed (shown to the entrant by the campsite page). */
    enum class JoinResult { JOINED, ALREADY_JOINED, FULL, NOT_READY, FAILED }

    /**
     * Register an entrant on a lottery's waiting list.
     * Reports ALREADY_JOINED if they are on it, FULL if the owner's capacity is reached,
     * NOT_READY if data hasn't loaded yet or an id is blank.
     */
    fun joinWaitlist(
        lotteryId: String,
        userId: String,
        result: (JoinResult) -> Unit
    ) {
        if (lotteryId.isBlank() || userId.isBlank() ||
            !entryRepository.isLotteriesEntriesLoaded.value
        ) {
            result(JoinResult.NOT_READY)
            return
        }

        // Already joined? (uses the repository's locally cached entries)
        val existingEntry = entryRepository
            .getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, userId)

        // A withdrawn entry doesn't count as joined: the entrant may rejoin (re-registers it)
        if (existingEntry != null && existingEntry.status != LotteryEntryStatus.WITHDRAWN.name) {
            result(JoinResult.ALREADY_JOINED)
            return
        }

        // Respect the owner's optional waitlist capacity (US 02.04.07)
        val lottery = lotteryRepository.getLotteryFromListById(lotteryId)
        if (lottery == null) {
            result(JoinResult.NOT_READY)
            return
        }
        if (LotteryController().isWaitlistFull(lottery)) {
            result(JoinResult.FULL)
            return
        }

        if (existingEntry != null) {
            entryRepository.updateLotteryEntry(
                existingEntry.copy(status = LotteryEntryStatus.REGISTERED.name)
            ) { r -> result(if (r.isSuccess) JoinResult.JOINED else JoinResult.FAILED) }
            return
        }

        entryRepository.addLotteryEntry(
            lotteryId = lotteryId,
            userId = userId,
            status = LotteryEntryStatus.REGISTERED.name,
            positionLottery = -1
        ) { addResult ->
            result(if (addResult.isSuccess) JoinResult.JOINED else JoinResult.FAILED)
        }
    }

    /**
     * Take an entrant off a lottery's waiting list (US 01.01.07).
     * Only allowed while the entry is still REGISTERED, i.e. before the draw. The entry is
     * marked WITHDRAWN, which frees a spot if the owner capped the waitlist.
     */
    fun leaveWaitlist(entry: LotteryEntry, result: (Boolean) -> Unit) {
        if (entry.status != LotteryEntryStatus.REGISTERED.name) {
            result(false)
            return
        }
        entryRepository.updateLotteryEntry(
            entry.copy(status = LotteryEntryStatus.WITHDRAWN.name)
        ) { r -> result(r.isSuccess) }
    }
}
