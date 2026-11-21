package com.example.cipherappproject

import com.example.cipherappproject.database.models.Lottery
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Notification
import com.example.cipherappproject.database.models.Reservation
import com.example.cipherappproject.database.provider.RepositoryDatabase
import com.google.firebase.Timestamp


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
 */

class NotificationController {
    private val notificationRepository get() = RepositoryDatabase.notificationRepository
    private val profileRepository get() = RepositoryDatabase.profileRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository


    // ---------- Opt-out (US 01.10.01) ----------
    fun canNotify(userId: String): Boolean {
        // TODO: read profile.notificationsEnabled
        return true
    }

    // ---------- Lottery results (US 01.03.01 / 01.03.02) ----------
    /** Pure: title + message for one drawn entry, or null if no notification is needed. */
    fun resultMessage(entry: LotteryEntry, campsiteNames: String): Pair<String, String>? {
        // TODO: WINNER -> "You won", NOT_SELECTED -> "Not selected"
        return null
    }

    fun notifyLotteryResults(lottery: Lottery, drawnEntries: List<LotteryEntry>) {
        // TODO: send resultMessage(...) to each entry.userId
    }

    // ---------- Owner -> waitlist (US 02.10.01) ----------
    /** Pure: returns an error message, or null if valid. */
    fun validateMessage(title: String, message: String): String? {
        // TODO: blank checks
        return null
    }

    /** Returns how many entrants were notified. */
    fun notifyWaitlist(lottery: Lottery, title: String, message: String): Int {
        // TODO: every non-WITHDRAWN entry of this lottery
        return 0

    }

    // ---------- Owner's sent list (US 02.10.02) ----------
    fun getSentNotifications(ownerId: String): List<SentNotificationGroup> {
        return groupSent(notificationRepository.getNotificationsFromListBySenderId(ownerId))
    }

    /** Pure: groups one broadcast's per-recipient docs into one row. */
    fun groupSent(sent: List<Notification>): List<SentNotificationGroup> {
        // TODO: groupBy (eventId, title, message)
        return emptyList()
    }

    // ---------- Reservations (US 01.05.02 / 01.07.02) ----------
    fun notifyReservationConfirmed(reservation: Reservation) {
        // TODO
    }

    fun notifyReservationCancelled(reservation: Reservation) {
        // TODO
    }

    // ---------- Entrant inbox ----------
    fun getReceived(userId: String): List<Notification> {
        return notificationRepository.getNotificationsFromListByUserId(userId)
            .sortedByDescending { it.createdAt }
    }

    fun markAsRead(notification: Notification) {
    // TODO: notificationRepository.updateNotification(notification.copy(isRead = true)) {}
    }

}
