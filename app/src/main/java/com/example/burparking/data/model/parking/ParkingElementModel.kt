package com.example.burparking.data.model.parking

import com.google.gson.annotations.SerializedName

data class ParkingElementModel(
    @SerializedName("elements")
    val parkings: List<ParkingModel>
)