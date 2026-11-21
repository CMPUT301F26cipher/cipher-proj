package com.example.cipherappproject

import android.util.Log
import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.enums.NotificationEventType
import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Notification
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.google.firebase.Timestamp
import java.util.Locale

// One owner broadcase is shown as single row
data class SentNotificationGroup(
    val eventId: String,
    val title: String,
    val message: String,
    val sentAt: Timestamp,
    val recipientCount: Int
)

/**
 * This class works to send and read in-app notifications
 * US 01.03.01, 01.03.02, 01.05.02, 01.07.02, 01.10.01, 02.10.01, 02.10.02
 */

class NotificationController {
    private val notificationRepository get() = RepositoryDatabase.notificationRepository
    private val profileRepository get() = RepositoryDatabase.profileRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository


    // Return false if user turned notifications off
    fun canNotify(userId: String): Boolean {
        return profileRepository.getProfileFromListById(userId)?.notificationsEnabled ?: false
    }

    // Save a notification unless the recipent opted out, return true if it was sent.
    private fun send(
        toUserId: String,
        eventId: String,
        eventType: NotificationEventType,
        title: String,
        message: String,
        fromUserId: String = ""
    ): Boolean {
        if (!canNotify(toUserId)) return false
        notificationRepository.addNotification(
            eventId = eventId,
            eventType = eventType.name,
            toUserId = toUserId,
            title = title,
            message = message,
            fromUserId = fromUserId
        ) { result ->
            result.onFailure { Log.e("Notification", "Send to $toUserId failed", it) }
        }
        return true

    }

    // SHow lottery results
    fun campsiteNames(lottery: Lottery): String {
        return lottery.campsiteIds
            .mapNotNull { campsiteRepository.getCampsiteFromListById(it)?.campsiteName }
            .joinToString(", ")
            .ifBlank { "your campsite" }
    }

    fun resultMessage(entry: LotteryEntry, campsiteNames: String): Pair<String, String>? {
        return when (entry.status) {
            LotteryEntryStatus.WINNER.name ->
                "You won the lottery!" to
                        "You were selected for $campsiteNames. Open Lotteries to choose your dates."

            LotteryEntryStatus.NOT_SELECTED.name ->
                "Lottery results" to
                        "Sorry, you weren't selected for $campsiteNames this time."

            else -> null
        }
    }

    fun notifyLotteryResults(lottery: Lottery, drawnEntries: List<LotteryEntry>) {
        val names = campsiteNames(lottery)
        for (entry in drawnEntries) {
            val (title, message) = resultMessage(entry, names) ?: continue
            send(entry.userId, lottery.lotteryId, NotificationEventType.LOTTERY, title, message)
        }
    }

    //
    fun validateMessage(title: String, message: String): String? {
        return when {
            title.isBlank() -> "Title cannot be empty."
            message.isBlank() -> "Message cannot be empty."
            else -> null
        }
    }

    /** Returns how many entrants were notified. */
    fun notifyWaitlist(lottery: Lottery, title: String, message: String, senderId: String): Int {
        return entryRepository.getLotteryEntriesFromListByLotteryId(lottery.lotteryId)
            .filter { it.status != LotteryEntryStatus.WITHDRAWN.name }
            .map { it.userId }
            .distinct()
            .count { userId ->
                send(
                    userId, lottery.lotteryId, NotificationEventType.LOTTERY,
                    title.trim(), message.trim(), fromUserId = senderId)
            }
    }

    // ---------- Owner's sent list (US 02.10.02) ----------
    fun getSentNotifications(ownerId: String): List<SentNotificationGroup> {
        return groupSent(notificationRepository.getNotificationsFromListBySenderId(ownerId))
    }

    /** Pure: groups one broadcast's per-recipient docs into one row. */
    fun groupSent(sent: List<Notification>): List<SentNotificationGroup> {
        return sent
            .groupBy { Triple(it.eventId, it.title, it.message) }
            .map { (key, docs) ->
                SentNotificationGroup(
                    eventId = key.first,
                    title = key.second,
                    message = key.third,
                    sentAt = docs.minOf { it.createdAt },
                    recipientCount = docs.size
                )
            }
            .sortedByDescending { it.sentAt }
    }

    private fun campsiteName(campsiteId: String): String =
        campsiteRepository.getCampsiteFromListById(campsiteId)?.campsiteName ?: "your campsite"

    fun notifyReservationConfirmed(reservation: Reservation) {
        val total = String.format(Locale.US, "%.2f", reservation.totalPrice)
        send(reservation.userId, reservation.reservationId, NotificationEventType.RESERVATION,
            "Reservation confirmed",
            "${campsiteName(reservation.campsiteId)} from ${reservation.bookStartDate} to " +
                    "${reservation.bookEndDate}. Total: $$total. " +
                    "You can download a CSV copy from Reservations.")
    }

    fun notifyReservationCancelled(reservation: Reservation) {
        send(reservation.userId, reservation.reservationId, NotificationEventType.RESERVATION,
            "Reservation cancelled",
            "Your reservation at ${campsiteName(reservation.campsiteId)} " +
                    "(${reservation.bookStartDate} to ${reservation.bookEndDate}) was cancelled. " +
                    "You can download a CSV confirmation from Reservations.")
    }

    // ---------- Entrant inbox ----------
    fun getReceived(userId: String): List<Notification> {
        return notificationRepository.getNotificationsFromListByUserId(userId)
            .sortedByDescending { it.createdAt }
    }

    fun markAsRead(notification: Notification) {
        if (notification.isRead) return
        notificationRepository.updateNotification(notification.copy(isRead = true)) { result ->
            result.onFailure { Log.e("Notification", "Mark read failed", it) }
        }
    }

}
