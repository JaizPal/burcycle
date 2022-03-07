package com.example.burparking.data.model

import com.google.gson.annotations.SerializedName

data class ParkingElementModel(
    @SerializedName("elements")
    val parkings: List<ParkingModel>
)