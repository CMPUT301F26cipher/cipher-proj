package com.example.cipherappproject.database.exceptions

/*
--------------------------------------------------------------------------------------
Program Name: DatabaseExceptions.kt
Date Written: November 16, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database exceptions made in contrast to the normal exceptions errors
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-16  Sofia       1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


/* Learn from and taken from OpenAI, ChatGPT on 2026-11-16 on
"How can I differentiate a normal error or exception from the ones I put explicitely?
 using tags or something?"*/

/**
 * An ID, date, price, or other input is invalid.
 */
class ValidationException(message: String) : Exception(message)

/**
 * A requested document doesn't exist.
 */
class NotFoundException(message: String) : Exception(message)

/**
 * Firestore data cannot be converted into a kotlin model.
 */
class DataConversionException(message: String) : Exception(message)

/**
 * A record already exists when duplicates aren't allowed.
 */
class DuplicateException(message: String) : Exception(message)

/**
 * An operation conflicts with existing data, such as overlapping reservations.
 */
class ConflictException(message: String) : Exception(message)

/**
 * The user isn't authorized to perform an operation.
 */
class PermissionException(message: String) : Exception(message)

/**
 * An operation cannot be completed because of its current state.
 *
 * --> For example: someone tries to join a lottery that has already been completed.
 *                  The input itself might be valid, and the lottery exists,
 *                  but the operation isn't allowed in its current state.
 *
 */
class OperationException(message: String) : Exception(message)


// maybe but a bit complicated but can be useful
class DatabaseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)