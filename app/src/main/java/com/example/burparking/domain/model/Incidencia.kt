package com.example.burparking.domain.model

import java.util.*

data class Incidencia (
    val idParking: Long?,
    val fecha: String,
    val tipo: String,
    val descripcion: String
)