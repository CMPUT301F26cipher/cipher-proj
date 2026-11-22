package com.example.cipherappproject

import com.example.cipherappproject.reservations.CampsiteFormInput
import com.example.cipherappproject.reservations.CampsiteRegistrationController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

/** US 02.01.01: the register-campsite form accepts complete input and rejects bad input. */
class CampsiteRegistrationTest {
    private val controller = CampsiteRegistrationController()
    private val today = LocalDate.of(2026, 11, 20)

    private fun valid() = CampsiteFormInput(
        name = "Lakeside", locationName = "Banff", latitude = "51.17", longitude = "-115.57",
        description = "Quiet lake site", maxGuests = "4", pricePerNight = "35.50",
        bookingStart = "2026-12-01", bookingEnd = "2026-12-31"
    )

    @Test fun completeFormIsValid() = assertNull(controller.validate(valid(), today))

    @Test fun requiredFieldsAreChecked() {
        assertNotNull(controller.validate(valid().copy(name = " "), today))
        assertNotNull(controller.validate(valid().copy(locationName = ""), today))
        assertNotNull(controller.validate(valid().copy(description = ""), today))
    }

    @Test fun coordinatesMustBeInRange() {
        assertNotNull(controller.validate(valid().copy(latitude = "91"), today))
        assertNotNull(controller.validate(valid().copy(longitude = "-181"), today))
        assertNotNull(controller.validate(valid().copy(latitude = "abc"), today))
    }

    @Test fun guestsAndPriceMustBeNumbers() {
        assertNotNull(controller.validate(valid().copy(maxGuests = "0"), today))
        assertNotNull(controller.validate(valid().copy(maxGuests = "2.5"), today))
        assertNotNull(controller.validate(valid().copy(pricePerNight = "-1"), today))
        assertNotNull(controller.validate(valid().copy(pricePerNight = "free"), today))
        assertNull(controller.validate(valid().copy(pricePerNight = "0"), today))
    }

    @Test fun commaListsAreTrimmed() {
        assertEquals(listOf("a", "b", "c"), controller.splitList(" a, b,, c ,"))
        assertEquals(emptyList<String>(), controller.splitList(""))
    }

    @Test fun bookingPeriodMustBeValidDates() {
        assertNotNull(controller.validate(valid().copy(bookingStart = ""), today))
        assertNotNull(controller.validate(valid().copy(bookingEnd = "12/31/2026"), today))
        assertNotNull(controller.validate(valid().copy(bookingStart = "2026-12-31", bookingEnd = "2026-12-01"), today))
    }

    @Test fun bookingPeriodCannotBeInThePast() {
        assertNotNull(controller.validate(valid().copy(bookingStart = "2026-10-01", bookingEnd = "2026-11-01"), today))
        // already started but still running is fine; a single day is fine
        assertNull(controller.validate(valid().copy(bookingStart = "2026-11-01", bookingEnd = "2026-12-01"), today))
        assertNull(controller.validate(valid().copy(bookingStart = "2026-12-05", bookingEnd = "2026-12-05"), today))
    }
}
