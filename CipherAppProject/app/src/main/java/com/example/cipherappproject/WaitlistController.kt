package com.example.cipherappproject
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.repositories.LotteryRepository
import com.example.cipherappproject.database.repositories.LotteryEntryRepository
import com.example.cipherappproject.database.models.LotteryEntry
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


class WaitlistController(
    private val lotteryRepository: LotteryRepository = LotteryRepository(),
    private val entryRepository: LotteryEntryRepository = LotteryEntryRepository()
) {

    /**
     * Find a lottery for the campsite and requested dates.
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

        lotteryRepository.getLotteryByCampsiteId(campsiteId) { lotteries ->

            if (lotteries == null) {
                result(null)
                return@getLotteryByCampsiteId
            }

            val matchingLottery = lotteries.firstOrNull { lottery ->

                val bookingStart = runCatching {
                    LocalDate.parse(lottery.bookingStartDate)
                }.getOrNull()

                val bookingEnd = runCatching {
                    LocalDate.parse(lottery.bookingEndDate)
                }.getOrNull()

                lottery.status == "open" &&
                        bookingStart != null &&
                        bookingEnd != null &&
                        !startDate.isBefore(bookingStart) &&
                        !endDate.isAfter(bookingEnd)
            }

            result(matchingLottery)
        }
    }

    /**
     * Register an entrant for a lottery.
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

        // Check if user has already joined
        entryRepository.getEntryForUser(
            lotteryId = lotteryId,
            userId = userId
        ) { existingEntry ->

            if (existingEntry != null) {
                result(false)
                return@getEntryForUser
            }

            entryRepository.createWaitlistEntry(
                lotteryId = lotteryId,
                userId = userId,
                result = result
            )
        }
    }
}
