package com.example.cipherappproject

import com.example.cipherappproject.ui.campsite.CampsiteInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests the campsite form's input checking (US 02.01.01, 02.01.02, 02.01.03).
 */
class CampsiteInputTest {

    private fun build(
        name: String = "Owl Creek Camp",
        location: String = "Jasper, AB",
        lat: String = "52.87",
        lng: String = "-118.08",
        description: String = "Quiet lakeside site",
        amenities: String = "Fire pit, Toilets",
        guests: String = "6",
        price: String = "45.50",
        image: String = "img/owl.jpg",
        tags: String = "lake, quiet",
    ) = CampsiteInput.fromText(name, location, lat, lng, description, amenities, guests, price, image, tags)

    private fun message(block: () -> Unit): String =
        assertThrows(IllegalArgumentException::class.java) { block() }.message ?: ""

    @Test
    fun validInput_buildsInput() {
        val input = build()
        assertEquals("Owl Creek Camp", input.campsiteName)
        assertEquals(listOf("52.87", "-118.08", "Jasper, AB"), input.mapLocation)
        assertEquals("Quiet lakeside site", input.description)
        assertEquals(listOf("Fire pit", "Toilets"), input.amenities)
        assertEquals(6, input.maxGuests)
        assertEquals(45.5, input.pricePerNight, 0.0)
        assertEquals("img/owl.jpg", input.imagePath)
        assertEquals(listOf("lake", "quiet"), input.tags)
    }

    @Test
    fun imageIsOptional() {
        assertEquals("", build(image = "").imagePath)
    }

    @Test
    fun amenitiesAndTagsAreOptionalAndTrimmed() {
        val input = build(amenities = "", tags = " a ,, b ,")
        assertTrue(input.amenities.isEmpty())
        assertEquals(listOf("a", "b"), input.tags)
    }

    @Test
    fun textIsTrimmed() {
        val input = build(name = "  Owl  ", location = "  Jasper ", lat = " 10 ", guests = " 3 ")
        assertEquals("Owl", input.campsiteName)
        assertEquals("Jasper", input.mapLocation[2])
        assertEquals(3, input.maxGuests)
    }

    @Test
    fun blankName_rejected() {
        assertTrue(message { build(name = "   ") }.contains("name", ignoreCase = true))
    }

    @Test
    fun blankLocation_rejected() {
        assertTrue(message { build(location = "") }.contains("Location"))
    }

    @Test
    fun badLatitude_rejected() {
        assertTrue(message { build(lat = "abc") }.contains("Latitude"))
        assertTrue(message { build(lat = "") }.contains("Latitude"))
        assertTrue(message { build(lat = "90.1") }.contains("Latitude"))
        assertTrue(message { build(lat = "-90.1") }.contains("Latitude"))
    }

    @Test
    fun badLongitude_rejected() {
        assertTrue(message { build(lng = "x") }.contains("Longitude"))
        assertTrue(message { build(lng = "180.1") }.contains("Longitude"))
        assertTrue(message { build(lng = "-180.1") }.contains("Longitude"))
    }

    @Test
    fun coordinateBoundaries_accepted() {
        assertEquals("90.0", build(lat = "90").mapLocation[0])
        assertEquals("-180.0", build(lng = "-180").mapLocation[1])
    }

    @Test
    fun badMaxGuests_rejected() {
        assertTrue(message { build(guests = "0") }.contains("Maximum guests"))
        assertTrue(message { build(guests = "-2") }.contains("Maximum guests"))
        assertTrue(message { build(guests = "2.5") }.contains("Maximum guests"))
        assertTrue(message { build(guests = "") }.contains("Maximum guests"))
    }

    @Test
    fun badPrice_rejected() {
        assertTrue(message { build(price = "-1") }.contains("Price"))
        assertTrue(message { build(price = "free") }.contains("Price"))
        assertTrue(message { build(price = "") }.contains("Price"))
        assertTrue(message { build(price = "NaN") }.contains("Price"))
    }

    @Test
    fun zeroPrice_accepted() {
        assertEquals(0.0, build(price = "0").pricePerNight, 0.0)
    }

    // ---------- rules and policies (US 02.11.01) ----------

    @Test
    fun rulesAndPolicies_onePerLine_keepCommas() {
        val input = CampsiteInput.fromText(
            "Camp", "Jasper", "52.8", "-118.0", "", "", "4", "40", "", "",
            rules = "No smoking\n\n  Quiet after 10pm, please  \n",
            policies = "No refunds after check-in"
        )
        assertEquals(listOf("No smoking", "Quiet after 10pm, please"), input.rules)
        assertEquals(listOf("No refunds after check-in"), input.policies)
    }

    @Test
    fun rulesAndPolicies_areOptional() {
        val input = CampsiteInput.fromText("Camp", "Jasper", "52.8", "-118.0", "", "", "4", "40", "", "")
        assertEquals(emptyList<String>(), input.rules)
        assertEquals(emptyList<String>(), input.policies)
    }
}
