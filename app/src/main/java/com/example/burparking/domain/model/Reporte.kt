package com.example.burparking.domain.model

import java.util.*

data class Reporte (
    val idParking: Long,
    val capacidad: Int,
    val fechaReporte: Date
    ) {
    override fun toString(): String {
        return "ID: $idParking - Capacidad: $capacidad - FechaReporte: $fechaReporte"
    }
}

