package com.example.burparking.data.model.direccion

/*
 * Modelo de la dirección
 */
data class DireccionModel(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: DireccionTagsModel
)