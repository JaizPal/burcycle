package com.example.burparking.data.model.parking

/*
 * Modelo del parking
 */
data class ParkingModel(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: ParkingTagsModel
)