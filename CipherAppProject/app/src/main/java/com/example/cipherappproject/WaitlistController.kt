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
2026-11-21  Nethmini   1.02     Join by open registration period instead of stay dates
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


class WaitlistController {
    // Shared instances (see Database_Documentation.md): never create new repositories here
    private val lotteryRepository get() = RepositoryDatabase.lotteryRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository

    /** True once the live list of lottery entries has loaded. */
    fun isEntriesLoaded(): Boolean = entryRepository.isLotteriesEntriesLoaded.value

    /**
     * Everyone who joined [lotteryId]'s waiting list and is still part of it (US 02.09.01),
     * from the live list, so the owner's screen updates as entrants join or leave.
     * Kept after the draw too, when their status shows the result.
     */
    fun getJoinedEntrants(lotteryId: String): List<LotteryEntry> =
        joinedOf(entryRepository.getLotteryEntriesFromListByLotteryId(lotteryId))

    /** Plain-language status of an entry for the owner's waitlist screen. */
    fun statusLabel(entry: LotteryEntry): String = when (entry.status) {
        LotteryEntryStatus.REGISTERED.name -> "Waiting for the draw"
        LotteryEntryStatus.WINNER.name, LotteryEntryStatus.OFFER_PENDING.name -> "Won, hasn't reserved yet"
        LotteryEntryStatus.ACCEPTED.name -> "Won and reserved"
        LotteryEntryStatus.DECLINED.name -> "Won, declined"
        LotteryEntryStatus.NOT_SELECTED.name -> "Not selected"
        LotteryEntryStatus.EXPIRED.name -> "Offer expired"
        else -> entry.status
    }

    /** Entries still on the list (those who left are dropped). */
    fun joinedOf(entries: List<LotteryEntry>): List<LotteryEntry> =
        entries.filter { it.status != LotteryEntryStatus.WITHDRAWN.name }

    companion object {
        /**
         * True while entrants can join [lottery]: it is OPEN and [today] is inside its
         * registration period (the lottery's startDate to endDate, both inclusive).
         */
        fun isRegistrationOpen(lottery: Lottery, today: LocalDate = LocalDate.now()): Boolean {
            if (lottery.status != LotteryStatus.OPEN.name) return false
            val start = DateUtils.parse(lottery.startDate) ?: return false
            val end = DateUtils.parse(lottery.endDate) ?: return false
            return !today.isBefore(start) && !today.isAfter(end)
        }

        /** The lotteries in [lotteries] for [campsiteId] that can be joined today, closing soonest first. */
        fun openLotteriesFor(
            lotteries: List<Lottery>,
            campsiteId: String,
            today: LocalDate = LocalDate.now()
        ): List<Lottery> =
            lotteries
                .filter { campsiteId in it.campsiteIds && isRegistrationOpen(it, today) }
                .sortedBy { it.endDate } // yyyy-MM-dd sorts correctly as text
    }

    /**
     * Lotteries the campsite is listed in whose registration is open today (US 01.01.06).
     * Reads the repository's live list, so a screen calling this updates as lotteries change.
     */
    fun getOpenLotteries(campsiteId: String, today: LocalDate = LocalDate.now()): List<Lottery> =
        openLotteriesFor(lotteryRepository.lotteries.value, campsiteId, today)

    /** The entrant's entry in [lotteryId] from the live list, or null if they never joined. */
    fun getEntry(lotteryId: String, userId: String): LotteryEntry? =
        entryRepository.getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, userId)

    /** Why a join attempt succeeded or failed (shown to the entrant by the campsite page). */
    enum class JoinResult { JOINED, ALREADY_JOINED, FULL, CLOSED, NOT_READY, FAILED }

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
        if (!isRegistrationOpen(lottery)) {
            result(JoinResult.CLOSED)
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
