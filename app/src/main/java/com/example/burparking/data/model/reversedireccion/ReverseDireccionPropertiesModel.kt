package com.example.burparking.data.model.reversedireccion

import com.google.gson.annotations.SerializedName

/*
 * Modelo del campo properties de la reverseDireccion
 */
data class ReverseDireccionPropertiesModel (
    @SerializedName("properties")
    val reverseDireccionModel: ReverseDireccionModel
    )