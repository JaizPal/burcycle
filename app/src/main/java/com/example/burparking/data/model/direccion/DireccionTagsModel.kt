package com.example.burparking.data.model.direccion

import com.google.gson.annotations.SerializedName

data class DireccionTagsModel(
    @SerializedName("addr:housenumber")
    val numero: String,
    @SerializedName("addr:street")
    val calle: String,
    @SerializedName("addr:postcode")
    val codigoPostal: String
)