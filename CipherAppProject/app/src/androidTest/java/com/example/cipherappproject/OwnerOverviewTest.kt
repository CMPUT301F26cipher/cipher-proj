package com.example.cipherappproject

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class OwnerOverviewTest {
    @Test
    fun campsiteRepositoryReceivesFirestoreData() {

        val db = Firebase.firestore
        val repository = CampsiteRepository()

        val campsiteId = "test-${UUID.randomUUID()}"
        val ownerId = "test-owner"

        val testCampsite = Campsite(
            campsiteId = campsiteId,
            ownerId = ownerId,
            campsiteName = "Database Integration Test",
            description = "Temporary test campsite",
            maxGuests = 4
        )

        val documentRef = db.collection("campsites")
            .document(campsiteId)

        try {
            // 1. Write a test campsite to Firestore
            Tasks.await(
                documentRef.set(testCampsite),
                15,
                TimeUnit.SECONDS
            )

            // 2. Wait for the repository's live list
            // to receive the new campsite
            val received = CountDownLatch(1)

            val deadline =
                System.currentTimeMillis() + 15000

            while (System.currentTimeMillis() < deadline) {

                val found = repository.campsites.any {
                    it.campsiteId == campsiteId
                }

                if (found) {
                    received.countDown()
                    break
                }

                Thread.sleep(100)
            }

            // 3. Verify the campsite appeared
            assertTrue(
                "Repository did not receive Firestore data",
                received.count == 0L
            )

            val retrieved = repository.campsites.first {
                it.campsiteId == campsiteId
            }

            assertEquals(
                "Database Integration Test",
                retrieved.campsiteName
            )

            assertEquals(ownerId, retrieved.ownerId)

        } finally {

            // 4. Delete the temporary campsite
            Tasks.await(
                documentRef.delete(),
                15,
                TimeUnit.SECONDS
            )
        }
    }
}