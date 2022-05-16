package com.example.burparking.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.burparking.domain.model.Direccion

    /*
     * Definición de la tabla direccion
     */
    @Entity(tableName = "direccion_table")
    data class DireccionEntity(
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        val id: Long,
        @ColumnInfo(name = "lat")
        val lat: Double,
        @ColumnInfo(name = "lon")
        val lon: Double,
        @ColumnInfo(name = "numero")
        val numero: String?,
        @ColumnInfo(name = "calle")
        val calle: String?,
        @ColumnInfo(name = "codigoPostal")
        val codigoPostal: String?
    )

    fun Direccion.toDatabase() = DireccionEntity(id, lat, lon, numero, calle, codigoPostal)