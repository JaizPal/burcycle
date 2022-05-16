package com.example.burparking.data.model.direccion

import com.google.gson.annotations.SerializedName

/*
 * Modelo del campo elements de la dirección
 */
data class DireccionElementModel(
    @SerializedName("elements")
    val direcciones: List<DireccionModel>
)