package com.example.cipherappproject

import com.example.cipherappproject.reservations.ReservationCsv
import org.junit.Assert.assertEquals
import org.junit.Test


// appying some unit tests for producing csv
class ReservationCsvTest {
    @Test
    fun emptyListGivesHeaderOnly() {
        val csv = ReservationCsv.build(emptyList()) { it }
        assertEquals(ReservationCsv.HEADER.joinToString(","), csv)
    }
    // TODO: one row, quoting a name with a comma, price formatting
}