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

/**
 * An owner "broadcast" is shwoan as a single row in the Sent Notificaitons screen
 * Each customer gets their own Notification document, so these are grouped together.
 * @property eventId the lottery the message was about
 * @property sentAt when the broadcase was sent
 * @property recipientCount how many entrants received it
 **/
data class SentNotificationGroup(
    val eventId: String,
    val title: String,
    val message: String,
    val sentAt: Timestamp,
    val recipientCount: Int
)

/**
 * This class works to create and read in-app notifications, stored in firestore
 * Meets US 01.03.01, 01.03.02, 01.05.02, 01.07.02, 01.10.01, 02.10.01, 02.10.02
 *
 * Every notification goes through "send", which skips the users who opted out.
 */

class NotificationController {
    companion object {
        const val RESERVATION_CONFIRMATION_TITLE = "RESERVATION CONFIRMATION"
    }

    private val notificationRepository get() = RepositoryDatabase.notificationRepository
    private val profileRepository get() = RepositoryDatabase.profileRepository
    private val entryRepository get() = RepositoryDatabase.lotteryEntryRepository
    private val campsiteRepository get() = RepositoryDatabase.campsiteRepository


    /* Check the user's notification setting.
        - It should return false if the user turned notifications off, or their profile isnt loaded yet
    */
    fun canNotify(userId: String): Boolean {
        return profileRepository.getProfileFromListById(userId)?.notificationsEnabled ?: false
    }

    /** Save a notification unless the recipent opted out, return true if it was sent.
     *  All  public notify functions should use this.
     *
     * @param fromUserId the owner who sent it, or "" for system notifications
     * @return true, if notification was sent, false if user opted out.
     */
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

    /**
     * Join the names of a lottery's campsites for use in a message. S
     */
    fun campsiteNames(lottery: Lottery): String {
        return lottery.campsiteIds
            .mapNotNull { campsiteRepository.getCampsiteFromListById(it)?.campsiteName }
            .joinToString(", ")
            .ifBlank { "your campsite" }
    }


    /**
     * Build the title + message for one drawn entry.
     *
     * @return (title, message) for winner or not selected, or null for any other status.
     */
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


    /**
     * Tell every enetrant in the drawn lottery, whether they have won/lost
     *
     * Called by LotteryDrawController after the draw is saved
     */
    fun notifyLotteryResults(lottery: Lottery, drawnEntries: List<LotteryEntry>) {
        val names = campsiteNames(lottery)
        for (entry in drawnEntries) {
            val (title, message) = resultMessage(entry, names) ?: continue
            send(entry.userId, lottery.lotteryId, NotificationEventType.LOTTERY, title, message)
        }
    }

    /**
     * Check an owners mesage before sending, validation check.
     *
     * @return an error message/null if the title + message are both filled in
     */
    fun validateMessage(title: String, message: String): String? {
        return when {
            title.isBlank() -> "Title cannot be empty."
            message.isBlank() -> "Message cannot be empty."
            else -> null
        }
    }

    /**
     * Sends an owner's message to every entrant on a lottery's waitlist
     * Skip the withdrawn entrants and anyone who opted out
     *
     * @param senderId, the owner sending it so it appears in their sent list
     * @return, how many entrants were notified.
     */
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

    /**
     * All messages an owner has sent, one row per broadcast, newest first
     */
    fun getSentNotifications(ownerId: String): List<SentNotificationGroup> {
        return groupSent(notificationRepository.getNotificationsFromListBySenderId(ownerId))
    }

    /**
     * Groups the per-recipient documents of each broadcast into one row
     * matching on lottery, title and message. Newest broadcast first.
     */
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

    /**
     * Display the name of the campsite, by defualt it is "your campsite.
     */
    private fun campsiteName(campsiteId: String): String =
        campsiteRepository.getCampsiteFromListById(campsiteId)?.campsiteName ?: "your campsite"

    /**
     * Text of the confirmation notification (US 01.06.01):
     * "[Campsite name] [Date reserved for] [Reservation ID]", under the title below.
     */
    fun reservationConfirmedMessage(campsiteName: String, reservation: Reservation): String {
        val dates = if (reservation.bookStartDate == reservation.bookEndDate) reservation.bookStartDate
        else "${reservation.bookStartDate} to ${reservation.bookEndDate}"
        return "$campsiteName $dates ${reservation.reservationId}"
    }

    /**
     * Tell the entrant their reservation was confirmed. The entrant can open Reservations
     * from the notification to view it and download a CSV.
     */
    fun notifyReservationConfirmed(reservation: Reservation) {
        send(reservation.userId, reservation.reservationId, NotificationEventType.RESERVATION,
            RESERVATION_CONFIRMATION_TITLE,
            reservationConfirmedMessage(campsiteName(reservation.campsiteId), reservation))
    }

    /**
     * Tells the entrant their reservation was cancelled
     * Must be called by the cancel feature, once a resrvation's status is set to CANCELLED
     */
    fun notifyReservationCancelled(reservation: Reservation, refund: Double = 0.0) {
        val refundText = if (refund > 0.0)
            " A refund of $${String.format(Locale.US, "%.2f", refund)} applies."
        else " No refund applies under the campsite's policy."
        send(reservation.userId, reservation.reservationId, NotificationEventType.RESERVATION,
            "Reservation cancelled",
            "Your reservation at ${campsiteName(reservation.campsiteId)} " +
                    "(${reservation.bookStartDate} to ${reservation.bookEndDate}) was cancelled." +
                    refundText)
    }

    /** Tells the campsite's owner that an entrant cancelled, so the dates are open again. */
    fun notifyOwnerReservationCancelled(reservation: Reservation) {
        val campsite = campsiteRepository.getCampsiteFromListById(reservation.campsiteId) ?: return
        send(campsite.ownerId, reservation.reservationId, NotificationEventType.RESERVATION,
            "Reservation cancelled",
            "A reservation at ${campsite.campsiteName} " +
                    "(${reservation.bookStartDate} to ${reservation.bookEndDate}) was cancelled by the entrant. " +
                    "Those dates can be booked again.",
            fromUserId = reservation.userId)
    }

    // notifications sent to this user, newest first
    fun getReceived(userId: String): List<Notification> {
        return notificationRepository.getNotificationsFromListByUserId(userId)
            .sortedByDescending { it.createdAt }
    }

    // Mark a notification as read in firestore, and does nothing if it already is.
    fun markAsRead(notification: Notification) {
        if (notification.isRead) return
        notificationRepository.updateNotification(notification.copy(isRead = true)) { result ->
            result.onFailure { Log.e("Notification", "Mark read failed", it) }
        }
    }

}
