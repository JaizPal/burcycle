package com.example.burparking.data.model.reversedireccion

import com.google.gson.annotations.SerializedName

data class ReverseDireccionPropertiesModel (
    @SerializedName("properties")
    val reverseDireccionModel: ReverseDireccionModel
)