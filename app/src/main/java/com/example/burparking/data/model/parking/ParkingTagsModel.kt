package com.example.burparking.data.model.parking

import com.google.gson.annotations.SerializedName

/*
 * Modelo de los tags del parking
 */
data class ParkingTagsModel(
    @SerializedName("capacity")
    val capacidad: Int
)