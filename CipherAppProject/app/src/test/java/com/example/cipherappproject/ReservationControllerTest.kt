package com.example.cipherappproject

import org.junit.Test

// following this convention with backticks for more description
// https://resources.jetbrains.com/storage/products/kotlinconf2018/slides/4_Best%20Practices%20for%20Unit%20Testing%20in%20Kotlin.pdf

class ReservationControllerTest {

    @Test
    fun `all local reservation lists empty when firebase reservation list not empty`() {}

    @Test
    fun `active reservation list empty when firebase reservation contains at least one active`() {}

    @Test
    fun `past reservation list empty when firebase reservation contains at least one past`() {}

    @Test
    fun `cancelled reservation list empty when firebase reservation contains at least one cancelled`() {}

    @Test
    fun `check if reservation list contains only reservations whos booking end date has not yet passed`() {}

    @Test
    fun `all reservations in local lists are under current user when exists at least one user reservation`() {}
    
}