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

    /**
     * Register an entrant on a lottery's waiting list.
     * Fails (false) if blank ids or the user has already joined.
     */
    fun joinWaitlist(
        lotteryId: String,
        userId: String,
        result: (Boolean) -> Unit
    ) {
        if (lotteryId.isBlank() || userId.isBlank()) {
            result(false)
            return
        }

        if (!entryRepository.isLotteriesEntriesLoaded.value) {
            result(false)
            return
        }

        // Already joined? (uses the repository's locally cached entries)
        val existingEntry = entryRepository
            .getLotteryEntryFromListByLotteryIdAndUserId(lotteryId, userId)

        if (existingEntry != null) {
            result(false)
            return
        }

        entryRepository.addLotteryEntry(
            lotteryId = lotteryId,
            userId = userId,
            status = LotteryEntryStatus.REGISTERED.name,
            positionLottery = -1
        ) { addResult ->
            result(addResult.isSuccess)
        }
    }
}
