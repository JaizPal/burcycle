package com.example.burparking.domain.model

import java.util.*

/*
 * Modelo de Incidencia usado en la app
 */
data class Incidencia (
    val idParking: Long?,
    val fecha: String,
    val tipo: String,
    val descripcion: String
)