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
    private val entryRepository: LotteryEntryRepository =
        LotteryEntryRepository()
) {

    fun getWaitlist(
        lotteryId: String,
        result: (List<LotteryEntry>?) -> Unit
    ) {
        entryRepository.getLotteryEntriesByLotteryId(lotteryId) { entries ->

            if (entries == null) {
                result(null)
                return@getLotteryEntriesByLotteryId
            }

            // Before the lottery draw, entrants have
            // the status "registered".
            val waitlist = entries.filter {
                it.status == "registered"
            }

            result(waitlist)
        }
    }
}