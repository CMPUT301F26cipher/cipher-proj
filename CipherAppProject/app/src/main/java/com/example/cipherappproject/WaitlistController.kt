package com.example.cipherappproject

/*
--------------------------------------------------------------------------------------
Program Name:
Date Written:
Author:
Purpose:
US: 01.02.01
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-05  Shira      1.01     Modifying to fit DB variables
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


// https://www.geeksforgeeks.org/kotlin/kotlin-data-classes/

/*
    Stores the data for each user and campsite in the waitlist

    user: Name of the user in the waitlist

    campsiteName: Name of the campsite that the waitlist is for
    participants: List of users in the waitlist (?)

    winners: Number of people that can win the lottery

    lotteryStatus: Ongoing, drawn, or closed
*/

data class WaitlistModel(
    val waitlistID: String,
    val campsiteID: String,
    val campsiteName: String,
    val participants: List<String>,
    val winners: Int,
    val lotteryStatus: LotteryStatus
) {
    // hi
}

// https://kotlinlang.org/docs/enum-classes.html#declare-enum-classes

/*
ACTIVE: Lottery is still ongoing, users can join and leave anytime

USERS_DRAWN: x amount of users have been drawn from the lottery

CLOSED: One of the drawn users has successfully reserved the
campsite
*/
enum class LotteryStatus {
    ACTIVE,
    USERS_DRAWN,
    CLOSED
}