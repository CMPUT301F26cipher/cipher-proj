package com.example.cipherappproject.database.models

/*
--------------------------------------------------------------------------------------
Program Name: UploadedImage.kt
Date Written: November 26, 2026
Author: Sofia Arturo Gomez
Purpose: data class for ImageRepository
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-26  Sofia      1.01     added skeleton
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */

/**
 * Represents the information returned after successfully uploading an image
 * to Cloudinary.
 *
 * @property publicId Unique identifier assigned to the uploaded image by Cloudinary.
 *                    Used to identify and manage the image in Cloudinary.
 * @property imageUrl Secure URL used to retrieve and display the uploaded image.
 */

/*Taken and learn from OpenAI, ChatGPT on 2026-11-26
on "if it returns both publicId and imageUrl should we return both or just use getImageUrl and why? " */
data class UploadedImage(
    val publicId: String,
    val imageUrl: String
)