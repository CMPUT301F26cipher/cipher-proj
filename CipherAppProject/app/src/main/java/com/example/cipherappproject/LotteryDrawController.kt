package com.example.cipherappproject

import android.util.Log
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.LotteryStatus
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.provider.RepositoryDatabase
import java.time.LocalDate
import kotlin.random.Random

/**                                                                               
   * Draws lottery winners once a lottery's registration period has ended (US       
  02.05.01).                                                                        
   *                                                                                
   * The draw shuffles every REGISTERED entry, numbers them 1..N in positionLottery,
   * marks the first numWinners as WINNER and the rest as NOT_SELECTED, then marks  
   * the lottery COMPLETED. positionLottery keeps the order so a declined spot can  
   * later be offered to the next person.                                           
   */   

   class LotteryDrawController {

    private val lotteryRepository get() = RepositoryDatabase.lotteryRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository

    // True if the lottery is open and its registration has ended
    // The end date is the last day to register, so the draw should happen day after it

    fun isReadyToDraw(lottery: Lottery, today: LocalDate = LocalDate.now()): Boolean {
        if (lottery.status != LotteryStatus.OPEN.name) return false
        val end = DateUtils.parse(lottery.endDate) ?: return false
        return end.isBefore(today)
    }

    /*
        It should randomely order the registered entries and return updated copies
        positions 1..N, the first [numWinners] are WINNER, the rest NOT_SELECTED.
        Entries wih any other status, i.e. withdrawn are left out
        Pass a seeded random in tests to get the same order every time
     */

     fun pickWinners(
        entries: List<LotteryEntry>
        numWinners: Int,
        random: Random = Random.Default
     ) : List<LotteryEntry> {
        return entries
            .filter {it.status == LotteryEntryStatus.REGISTERED.name}
            .shuffled(random)
            mapIndexed {index, entry ->
                val status = 
                    if (index < numWinners) LotteryEntryStatus.WINNER
                    else LotteryEntryStatus.NOT_SELECTED
                entry.copy(status = status.name, positionLottery = index + 1)
            }
     }

     /*
        Draw one lottery, save the results
        Mark it PROCESSING, so it is no longer OPEN and won't be drawn twice,
        then saves every entry, then marks the lottery COMPLETED
      */

      fun drawLottery(lottery: Lottery, onResult: (Result<Unit>) -> Unit = {}) {
        val processing = lottery.copy(status = LotteryStatus.PROCESSING.name)
        lotteryRepository.updateLottery(processing) { processingResult ->
                if (processingResult.isFailure) {
                    onResult(processingResult)
                    return @updateLottery
                }

                val entries = entryRepository.getLotteryEntriesFromListByLotteryId(lottery.lotteryId)
                val draw = pickWinners(entries, lottery.numWinners)
                saveEntries(drawn) { saveResult ->
                    if (saveResult.isFailure) {
                        onResult(saveResult)
                        return @saveEntries
                    }
                    val completed = lottery.copy(status = LotteryStatus.COMPLETED.name)
                    lotteryRepository.update(completed, onresult)
                }
        }
      }

      // Draw every lottery whose registration has ended, and does nothing until the data has loaded. 

      fun drawExpiredLotteries(today: LocalDate = LocalDate.now()) {
        if (!lotteryRepository.isLotteriesLoaded.value || !entryRepository.isLotteriesEntriesLoaded.value
      ) return

      lotteryRepository.lotteries.value
        .filter {isReadyToDraw(it, today) }
        .forEach { lottery ->
            drawLottery(lottery) { result ->
                result.onFailure {
                    Log.e("LotteryDraw", "Draw failed for ${lottery.lotteryID}", error)
                }

            }
        }
   }

   /*
        Svae each entry, then call onDone once: success if all are saved
        or on the first failure
    */

    private fun saveEntries(entries: List<LotteryEntry>, onDone: (Result<Unit>) -> Unit) {
        if (entries.isEmpty()) {
            onDone(Result.success(Unit))
            return
        }

        var remaining = entries.size

        var failed = false
        for entry in entries {
            entryRepository.updateLotteryEntry(entry) { result ->
                if (failed) return @updateLotteryEntry
                if (result.isFailure) {
                    failed = true
                    onDone(result)
                    return @updateLotteryEntry
                }

                remaining--
                if (remaining == 0) onDone(Result.success(Unit))

            }
        }
    }

   }

   