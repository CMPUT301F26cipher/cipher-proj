package com.example.cipherappproject

import com.example.cipherappproject.reservations.ReservationList
import org.junit.Test

class ReservationListTest {

    /* Validate if active list contains only active reservations */
    @Test
    fun validActiveReservationList() {
        //TODO
    }

    /* Validate if past list contains only past reservations */
    @Test
    fun validPastReservationList() {
        //TODO
    }

    /* Empty reservation list */
    @Test
    fun nullReservationList() {
        val reservationList = ReservationList("")
    }

    /* if after refresh time, an expired reservation is moved form active to past */
    @Test

    //TODO: Test can it update active and past reservations list after booking end date passes

    ////////////////////////////////////// PART 4 STUFF DO LATER //////////////////////////////////////////

    //TODO: Validate if cancelled list contains only cancelled reservations
}