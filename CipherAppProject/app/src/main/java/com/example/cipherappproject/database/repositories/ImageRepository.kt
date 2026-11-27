package com.example.cipherappproject.database.repositories

/*
--------------------------------------------------------------------------------------
Program Name: ImageRepository.kt
Date Written: November 24, 2026
Author: Sofia Arturo Gomez
Purpose: Manage database methods for Images
--------------------------------------------------------------------------------------
Modification History:
--------------------------------------------------------------------------------------
Date        Person     CSR#     Description
2026-11-24  Sofia      1.01     added skeleton
2026-11-26  Sofia      1.01     added queries and exceptions
--------------------------------------------------------------------------------------
Future Enhancements
NA
 */


/**
 * Manages Campsite data stored in the Firebase Firestore database.
 *
 * Provides methods to add, retrieve, and update Campsite documents
 * directly from Firestore, as well as methods to search and filter
 * from the local Campsite list. This local list is inside a state.
 *
 * The local list is synchronized with Firestore using a snapshot listener.
 */

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.example.cipherappproject.database.models.UploadedImage
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.ErrorInfo
import com.example.cipherappproject.database.exceptions.DataConversionException
import com.example.cipherappproject.database.exceptions.ImageUploadException

class ImageRepository {

    /*Learn and taken from OpenAI, ChatGPT on 2026-11-26
    on "How can I initiliaze the cloud here and hwo it works and why?"*/
    private var isInitialized = false
    private val uploadPreset = "cipher_images"
    private val cloudName = "oqhskx70"

    /**
     * Initializes the Cloudinary SDK with the application's cloud configuration.
     *
     * Initialization is performed only once to avoid repeatedly configuring
     * the Cloudinary SDK.
     *
     * @param context Android context used to access the application environment.
     */
    fun initialize(context: Context) {
        if (!isInitialized) {
            val config = mapOf( /*is a like a dictionary with key and value*/
                "cloud_name" to cloudName
            )

            MediaManager.init(context.applicationContext, config)

            isInitialized = true
        }
    }

    /**
     * Uploads an image selected from the device to Cloudinary.
     *
     * @param imageUri URI referencing the image selected from the device.
     * @param result Callback that returns:
     *               - Result.success(UploadedImage) if the upload succeeds,
     *                 containing the image's public ID and secure URL.
     *               - Result.failure(exception) if the upload fails.
     */
    fun uploadImage(
        imageUri: Uri, //
        result: (Result<UploadedImage>) -> Unit
    ) {
        MediaManager.get()
            .upload(imageUri)
            .unsigned(uploadPreset)
            .callback(
                /*Learn and taken from OpenAI, ChatGPT on 2026-11-26
                  on "How does the queries work in Cloudinary? can you explain each?
                  like why override, and objectt???"*/
                object : UploadCallback { /*is an interface! that why override --> You're creating an anonymous object that implements the UploadCallback interface. The Cloudinary SDK defines UploadCallback
                 with five required functions. Kotlin therefore expects your object to implement all five.*/

                    override fun onStart(requestId: String?) {
                    }

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) {
                    }

                    override fun onSuccess(
                        requestId: String?, // Identifies the upload.
                        resultData: Map<*, *>? // A map containing information returned by Cloudinary, such as public_id and secure_url.
                        /*Map<*, *>?
                        - Map --> A collection of key-value pairs.
                        - * --> The key's type is not specified. --> The * is called a star projection in Kotlin.
                        - ? --> The entire map can be null.
                        --> EX: Map<String, String>, Map<String, Int>, Map<String, Any>
                        */

                        /*Cloudinary has its own response containing information about the uploaded image, such as:
                            - public_id: the unique identifier of the image.
                            - secure_url: the HTTPS URL used to access the image.
                            - format: the image format.
                            - width: image width.
                            - height: image height.
                            - bytes: image size.
                          It returns this information in a map, and we choose which values we need.*/
                    ) {

                        // Check that the response exists and Extract public_id and secure_url.
                        val publicId = resultData?.get("public_id") as? String
                        val imageUrl = resultData?.get("secure_url") as? String

                        // Check that both values are valid Strings.
                        if (publicId.isNullOrBlank() || imageUrl.isNullOrBlank()) {
                            result(
                                Result.failure(
                                    DataConversionException(
                                        "Cloudinary response is missing the image ID or URL"
                                    )
                                )
                            )
                            // exits the onSuccess function
                            return
                        }

                        // Create UploadedImage.
                        val uploadedImage = UploadedImage(
                            publicId = publicId,
                            imageUrl = imageUrl
                        )

                        // Return UploadedImage
                        result(Result.success(uploadedImage))

                    }

                    override fun onError(
                        requestId: String?, // Identifies the upload.
                        error: ErrorInfo? // Contains information about what went wrong.
                    ) {
                        result(
                            Result.failure(
                                ImageUploadException("Cloudinary upload failed: ${error?.description}")
                            )
                        )
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {
                    }
                }
            )
            .dispatch() // actually submit the upload request.
    }

    /**
     * Generates the delivery URL used to retrieve and display an image
     * stored in Cloudinary.
     *
     * @param publicId Unique identifier of the uploaded image in Cloudinary.
     * @return Secure URL used to access and display the image.
     */
    fun getImageUrl(publicId: String): String {
        // public_id = profiles/user123/profile_picture
        // return https://res.cloudinary.com/CLOUD_NAME/image/upload/PUBLIC_ID
        //return "https://res.cloudinary.com/$cloudName/image/upload/$publicId"

        // SDK version
        return MediaManager.get()
            .url()
            .generate(publicId)
    }

    /**
     * Deletes an uploaded image from Cloudinary using its public ID. --> NOT IMPLEMENT YET AND I DO NOT THINK WE NEED IT EITHER WAY!
     *
     * @param publicId Unique identifier of the image to be deleted in Cloudinary.
     * @param result Callback that returns:
     *               - Result.success(Unit) if the image was successfully deleted.
     *               - Result.failure(exception) if the deletion failed.
     */
    /*Delete an image from Cloudinary*/
    fun deleteImage(
        publicId: String,
        result: (Result<Unit>) -> Unit
    ){

    }

}