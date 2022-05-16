package com.example.burparking.data.model.reversedireccion

import com.google.gson.annotations.SerializedName

/*
 * Modelo del campo features de la reverseDireccion
 */
data class ReverseDireccionFeaturesModel(
    @SerializedName("features")
    val features: List<ReverseDireccionPropertiesModel>
)