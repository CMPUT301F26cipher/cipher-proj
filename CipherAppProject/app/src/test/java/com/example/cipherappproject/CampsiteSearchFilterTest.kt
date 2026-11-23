package com.example.cipherappproject

import com.example.cipherappproject.database.models.Campsite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Tests the entrant's search filters (US 01.01.01, 01.01.02, 01.01.03):
 * checking what the entrant typed (SearchFilter) and picking the matching campsites (CampsiteListFromFilters).
 */
class CampsiteSearchFilterTest {

    private fun campsite(id: String, name: String, location: String, guests: Int) = Campsite(
        campsiteId = id,
        campsiteName = name,
        mapLocation = listOf("1.0", "2.0", location),
        maxGuests = guests,
    )

    private val pine = campsite("1", "Pine Ridge", "Jasper, AB", 8)
    private val lake = campsite("2", "Lakeview", "Banff, AB", 4)
    private val aspen = campsite("3", "Aspen Hollow", "Jasper, AB", 6)
    private val all = listOf(pine, lake, aspen)

    private fun search(filter: SearchFilter, free: Map<String, List<LocalDate>> = emptyMap()): List<String> =
        CampsiteListFromFilters(all, filter) { free[it].orEmpty() }.getList().map { it.campsiteId }

    private fun message(block: () -> Unit): String =
        assertThrows(SearchException::class.java) { block() }.message ?: ""

    // ---------- SearchFilter.fromInput ----------

    @Test
    fun fromInput_blankFields_meansNoFilters() {
        assertEquals(SearchFilter(), SearchFilter.fromInput("", "", "", "", ""))
    }

    @Test
    fun fromInput_validFields_areParsedAndTrimmed() {
        val filter = SearchFilter.fromInput(" pine ", " jasper ", " 4 ", "2026-12-02", "2026-12-05")
        assertEquals("pine", filter.name)
        assertEquals("jasper", filter.location)
        assertEquals(4, filter.guests)
        assertEquals(LocalDate.of(2026, 12, 2), filter.checkIn)
        assertEquals(LocalDate.of(2026, 12, 5), filter.checkOut)
    }

    @Test
    fun fromInput_badGuests_rejected() {
        listOf("0", "-1", "abc", "2.5").forEach {
            assertTrue(message { SearchFilter.fromInput("", "", it, "", "") }.contains("guests"))
        }
    }

    @Test
    fun fromInput_badDateFormat_rejected() {
        assertTrue(message { SearchFilter.fromInput("", "", "", "12/02/2026", "2026-12-05") }.contains("Check-in"))
        assertTrue(message { SearchFilter.fromInput("", "", "", "2026-12-02", "Dec 5") }.contains("Check-out"))
        assertTrue(message { SearchFilter.fromInput("", "", "", "2026-13-40", "2026-12-05") }.contains("Check-in"))
    }

    @Test
    fun fromInput_onlyOneDate_rejected() {
        assertTrue(message { SearchFilter.fromInput("", "", "", "2026-12-02", "") }.contains("both"))
        assertTrue(message { SearchFilter.fromInput("", "", "", "", "2026-12-05") }.contains("both"))
    }

    @Test
    fun fromInput_checkOutNotAfterCheckIn_rejected() {
        assertTrue(message { SearchFilter.fromInput("", "", "", "2026-12-05", "2026-12-02") }.contains("after"))
        assertTrue(message { SearchFilter.fromInput("", "", "", "2026-12-05", "2026-12-05") }.contains("after"))
    }

    @Test
    fun fromInput_noGuestsGivesNull() {
        assertNull(SearchFilter.fromInput("", "", "", "", "").guests)
    }

    // ---------- CampsiteListFromFilters ----------

    @Test
    fun emptyFilter_returnsEveryCampsite() {
        assertEquals(listOf("1", "2", "3"), search(SearchFilter()))
    }

    @Test
    fun name_isCaseInsensitiveContains() {
        assertEquals(listOf("1"), search(SearchFilter(name = "PINE")))
        assertEquals(listOf("2"), search(SearchFilter(name = "view")))
        assertTrue(search(SearchFilter(name = "zzz")).isEmpty())
    }

    @Test
    fun location_matchesLocationNameOnly() {
        assertEquals(listOf("1", "3"), search(SearchFilter(location = "jasper")))
        // the latitude and longitude are not searched
        assertTrue(search(SearchFilter(location = "1.0")).isEmpty())
    }

    @Test
    fun guests_needsAtLeastThatManySpots() {
        assertEquals(listOf("1", "3"), search(SearchFilter(guests = 6)))
        assertEquals(listOf("1"), search(SearchFilter(guests = 7)))
        assertTrue(search(SearchFilter(guests = 9)).isEmpty())
    }

    @Test
    fun filters_combine() {
        assertEquals(listOf("3"), search(SearchFilter(name = "a", location = "jasper", guests = 6)))
        assertTrue(search(SearchFilter(name = "lake", location = "jasper")).isEmpty())
    }

    @Test
    fun dates_everyNightMustBeFree() {
        val free = mapOf("3" to (1..10).map { LocalDate.of(2026, 12, it) })
        val stay = { inDay: Int, outDay: Int ->
            SearchFilter(checkIn = LocalDate.of(2026, 12, inDay), checkOut = LocalDate.of(2026, 12, outDay))
        }
        assertEquals(listOf("3"), search(stay(2, 5), free))
        // the check-out day itself is not a night
        assertEquals(listOf("3"), search(stay(7, 11), free))
        // one night past the free dates
        assertTrue(search(stay(8, 13), free).isEmpty())
        // starts before the free dates
        assertTrue(search(stay(30, 3).copy(checkIn = LocalDate.of(2026, 11, 30)), free).isEmpty())
    }

    @Test
    fun dates_campsiteWithNoFreeDatesIsLeftOut() {
        val filter = SearchFilter(checkIn = LocalDate.of(2026, 12, 2), checkOut = LocalDate.of(2026, 12, 5))
        assertTrue(search(filter).isEmpty())
    }

    @Test
    fun dates_combineWithOtherFilters() {
        val days = (1..10).map { LocalDate.of(2026, 12, it) }
        val free = mapOf("1" to days, "3" to days)
        val filter = SearchFilter(guests = 7, checkIn = LocalDate.of(2026, 12, 2), checkOut = LocalDate.of(2026, 12, 5))
        assertEquals(listOf("1"), search(filter, free))
    }
}
