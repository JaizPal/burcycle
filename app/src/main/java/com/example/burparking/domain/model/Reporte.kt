package com.example.burparking.domain.model

import java.util.*

/*
 * Modelo de Reporte usado en la app
 */
data class Reporte (
    val idParking: Long,
    val capacidad: Int,
    val fechaReporte: Date
    ) {
    override fun toString(): String {
        return "ID: $idParking - Capacidad: $capacidad - FechaReporte: $fechaReporte"
    }
}

