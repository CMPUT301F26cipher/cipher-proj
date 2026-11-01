package com.example.cipherappproject

data class CampsiteInfoModel (
    val campsiteID: String,
    val ownerID: String,
    val campsiteName: String,
    val description: String,
    val amenities: List<String>,
    val maxOccupancy: Int,
    val latitude: Double,
    val longitude: Double,
    val image: String? = null,
    val tags: List<String> = emptyList()
)