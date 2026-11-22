package com.example.cipherappproject

import com.example.cipherappproject.database.enums.LotteryEntryStatus
import com.example.cipherappproject.database.models.LotteryEntry
import com.example.cipherappproject.database.models.Notification
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Test the NotificationController
class NotificationControllerTest {
    private val controller = NotificationController()

    private fun entry(status: LotteryEntryStatus) =
        LotteryEntry(lotteryEntryId = "e", lotteryId = "l", userId = "u", status = status.name)

    private fun sent(eventId: String, title: String, message: String, seconds: Long) =
        Notification(
            notificationId = "n$seconds", eventId = eventId, title = title,
            message = message, fromUserId = "owner", createdAt = Timestamp(seconds, 0)
        )

    /** A winner gets the win title and the campsite name. */
    @Test
    fun winnerGetsWinMessage() {
        val msg = controller.resultMessage(entry(LotteryEntryStatus.WINNER), "Pine Camp")
        assertEquals("You won the lottery!", msg!!.first)
        assertTrue(msg.second.contains("Pine Camp"))
    }

    /** A loser gets the results title and the campsite name. */
    @Test
    fun loserGetsLoseMessage() {
        val msg = controller.resultMessage(entry(LotteryEntryStatus.NOT_SELECTED), "Pine Camp")
        assertEquals("Lottery results", msg!!.first)
        assertTrue(msg.second.contains("Pine Camp"))

    }

    /** Entries that weren't part of the draw get no notification. */
    @Test
    fun otherStatusesGetNoNotification() {
        for (status in listOf(
            LotteryEntryStatus.REGISTERED, LotteryEntryStatus.WITHDRAWN,
            LotteryEntryStatus.DECLINED, LotteryEntryStatus.ACCEPTED
        )) {
            assertNull("$status should not notify", controller.resultMessage(entry(status), "x"))
        }
    }

    @Test
    fun blankTitleIsRejected() {
        assertEquals("Title cannot be empty.", controller.validateMessage("", "Flood"))
    }

    @Test
    fun whitespaceOnlyMessageIsRejected() {
        assertEquals("Message cannot be empty.", controller.validateMessage("Closed", "   "))
    }

    @Test
    fun filledInMessageIsAccepted() {
        assertNull(controller.validateMessage("Closed", "Campsite flooded"))
    }

    @Test
    fun oneBroadcastIsOneRow() {
        val groups =
            controller.groupSent(List(3) { sent("lottery1", "Closed", "Flood", 100L + it) })
        assertEquals(1, groups.size)
        assertEquals(3, groups[0].recipientCount)
    }

    @Test
    fun differentMessagesAreSeparateRows() {
        val groups = controller.groupSent(
            listOf(
                sent("lottery1", "Closed", "Flood", 100),
                sent("lottery1", "Reopened", "Water's gone", 200)
            )
        )
        assertEquals(2, groups.size)
    }

    /** The sent list is newest first. */
    @Test
    fun sentListIsNewestFirst() {
        val groups = controller.groupSent(
            listOf(
                sent("l", "Old", "a", 100),
                sent("l", "New", "b", 300),
                sent("l", "Middle", "c", 200)
            )
        )
        assertEquals(listOf("New", "Middle", "Old"), groups.map { it.title })
    }

    @Test
    fun noSentNotificationsGivesEmptyList() {
        assertTrue(controller.groupSent(emptyList()).isEmpty())
    }
}
