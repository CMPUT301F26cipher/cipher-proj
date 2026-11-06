package com.example.cipherappproject.reservations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cipherappproject.reservations.ReservationList.Status

/*
--------------------------------------------------------------------------------------
Program Name: ReservationController.kt
Date Written: November 5, 2026
Author: Shira Li
Purpose: Reservation display screen for both past and active reservations, and for
both Entrants and Owners
US: 02.07.01, 02.07.02 (owners), 01.06.01, 01.06.02 (entrants)
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-06  Shira      1.01     Moved button Status related code from ReservationList.kt
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

class ReservationController {

    /** Holds the current button status of which reservation list to be displayed */
    var currentStatus: Status by mutableStateOf(Status.ACTIVE)


}