package com.example.burparking.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.burparking.domain.model.Parking

@Entity(tableName = "parking_table")
data class ParkingEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    @ColumnInfo(name = "capacidad")
    val capacidad: Int
)

fun Parking.toDatabase() = ParkingEntity(id = id, lat = lat, lon = lon, capacidad)