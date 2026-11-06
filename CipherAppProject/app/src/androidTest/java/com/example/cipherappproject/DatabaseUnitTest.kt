package com.example.cipherappproject

import android.util.Log
import com.example.cipherappproject.database.models.Campsite
import com.example.cipherappproject.database.repositories.CampsiteRepository
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class DatabaseUnitTest {

/*
    @Test
    fun addCampsiteTest() {

        val repository = CampsiteRepository()

        // Create dummy campsite data
        val campsiteName = "Edmonton Campsite"

        val amenities = mutableListOf("Parking", "Firepit")
        val tags = mutableListOf("Nature", "Lake")

        // Add campsite to Firestore and wait for the result
        val result = awaitCallback<Result<String>> { callback ->
            repository.addCampsite(
                ownerId = "dvIxoQNArr16yanGiCLY",
                campsiteName = campsiteName,
                mapLocation = listOf("67.564", "-22.3", "Test name"),
                description = "Dummy campsite for repository testing",
                amenities = amenities,
                maxGuests = 10,
                pricePerNight = 47.0,
                imagePath = "test/campsite.jpg",
                tags = tags,
                qrCodeImagePath = "test/qrcode.png",
                result = callback
            )
        }

        // Extract campsite ID or throw the Firebase exception
        val id = result.getOrThrow()

        Log.d("CampsiteTest", "Created campsite: $id")
    }*/


    @Test
    fun getCampsiteByIdTest() {
        val repository = CampsiteRepository()

        // Create dummy campsite data
        val campsiteName = "Alberta Campsite"

        val amenities = mutableListOf("Wi-Fi", "Parking", "Firepit")
        val tags = mutableListOf("Nature", "Camping", "Lake")

        val id = "WVJx8bVdgKXoccXkF5sP"

        // Retrieve campsite from Firestore
        val result = awaitCallback<Result<Campsite>> { callback ->
            repository.getCampsiteById(id, callback)
        }

        // Extract retrieved campsite
        val actualCampsite = result.getOrThrow()

        // Check that the retrieved object matches the original data
        val expectedCampsite = Campsite(
            campsiteId = id,
            ownerId = "dvIxoQNArr16yanGiCLY",
            campsiteName = campsiteName,
            mapLocation = listOf("53.5461", "-113.4938", "Test location"),
            description = "Dummy campsite for repository testing",
            amenities = amenities,
            maxGuests = 4,
            pricePerNight = 75.0,
            imagePath = "test/campsite.jpg",
            tags = tags,
            qrCodeImagePath = "test/qrcode.png"
        )

        Assert.assertEquals(expectedCampsite, actualCampsite)

        Log.d("CampsiteTest", "Campsite retrieved, name $campsiteName")
        Log.d("CampsiteTest", "Campsite retrieved successfully")
    }

    /*
    @Test
    fun updateCampsite(){

        val repository = CampsiteRepository()

        // Create dummy campsite data
        val campsiteName = "Alberta Campsite"

        val amenities = mutableListOf("Wi-Fi", "Parking", "Firepit")
        val tags = mutableListOf("Nature", "Camping", "Lake")

        val id = "WVJx8bVdgKXoccXkF5sP"

        // Retrieve campsite from Firestore
        val retrievedCampsite = awaitCallback<Campsite?> { callback ->
            repository.getCampsiteById(id, callback)
        }

        TestCase.assertNotNull("Campsite should exist in Firestore", retrievedCampsite)

        val actualCampsite = requireNotNull(retrievedCampsite)

        // Check that the retrieved object matches the original data
        val expectedCampsite = Campsite(
            campsiteId = id,
            ownerId = "dvIxoQNArr16yanGiCLY",
            campsiteName = campsiteName,
            mapLocation = listOf("53.5461", "-113.4938", "Test name"),
            description = "Dummy campsite for repository testing",
            amenities = amenities,
            maxGuests = 4,
            pricePerNight = 75.0,
            imagePath = "test/campsite.jpg",
            tags = tags,
            qrCodeImagePath = "test/qrcode.png"
        )

        Assert.assertEquals(expectedCampsite, actualCampsite)
        Log.d("CampsiteTest", "Campsite retrieved, name $campsiteName")

        Log.d("CampsiteTest", "Campsite retrieved successfully")

        // 4. Modify the campsite
        val updatedName = "UPDATED $campsiteName"
        actualCampsite.campsiteName = updatedName

        // 5. Save the updated campsite
        val updateSuccessful = awaitCallback<Boolean> { callback ->
            repository.updateCampsite(actualCampsite, callback)
        }

        TestCase.assertTrue("Campsite update should succeed", updateSuccessful)

    }*/


    /*
    *
        // 6. Retrieve the campsite again
        val updatedCampsite = awaitCallback<Campsite?> { callback ->
            repository.getCampsiteById(id, callback)
        }

        TestCase.assertNotNull(updatedCampsite)

        // Verify that the changes persisted
        Assert.assertEquals(updatedName, updatedCampsite?.campsiteName)

        // Verify that other fields were preserved
        Assert.assertEquals(4, updatedCampsite?.maxGuests)

        Log.d("CampsiteTest", "Campsite updated and verified successfully")*/


    // Helper function to wait for Firebase callbacks
    private fun <T> awaitCallback(
        register: ((T) -> Unit) -> Unit
    ): T {

        val latch = CountDownLatch(1)
        val result = AtomicReference<T>()

        register { value ->
            result.set(value)
            latch.countDown()
        }

        check(latch.await(15, TimeUnit.SECONDS)) {
            "Firebase callback timed out"
        }

        return result.get()
    }
}