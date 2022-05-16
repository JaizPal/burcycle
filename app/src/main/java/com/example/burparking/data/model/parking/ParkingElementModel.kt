package com.example.burparking.data.model.parking

import com.google.gson.annotations.SerializedName

/*
 * Modelo del campo elements del parking
 */
data class ParkingElementModel(
    @SerializedName("elements")
    val parkings: List<ParkingModel>
)