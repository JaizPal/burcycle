package com.example.burparking.data.model.reversedireccion

import com.google.gson.annotations.SerializedName

data class ReverseDireccionFeaturesModel(
    @SerializedName("features")
    val features: List<ReverseDireccionPropertiesModel>
)