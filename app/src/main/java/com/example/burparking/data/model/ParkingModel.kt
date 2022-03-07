package com.example.burparking.data.model

data class ParkingModel(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: ParkingTagsModel
)