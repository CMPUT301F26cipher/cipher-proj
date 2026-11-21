package com.example.cipherappproject
/*
--------------------------------------------------------------------------------------
Program Name: SearchException.kt
Date Written: Nov 4 2026
Author: Bailey Parrish
Purpose: The error thrown when search input is invalid. Its message is shown to the entrant.
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-04  Bailey      1.01     Built
2026-11-18  Madison     1.02     changed to Exception(message)
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

class SearchException(message: String) : Exception(message) {
}