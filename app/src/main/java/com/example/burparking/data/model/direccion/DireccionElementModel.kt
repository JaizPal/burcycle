package com.example.burparking.data.model.direccion

import com.google.gson.annotations.SerializedName

data class DireccionElementModel(
    @SerializedName("elements")
    val direcciones: List<DireccionModel>
)