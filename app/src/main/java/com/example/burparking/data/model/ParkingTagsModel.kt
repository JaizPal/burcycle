package com.example.burparking.data.model

import com.google.gson.annotations.SerializedName

data class ParkingTagsModel(
    @SerializedName("capacity")
    val capacidad: Int
)