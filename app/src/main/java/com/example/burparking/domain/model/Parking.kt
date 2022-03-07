package com.example.burparking.domain.model

import com.example.burparking.data.database.entities.ParkingEntity
import com.example.burparking.data.model.parking.ParkingModel

data class Parking(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val capacidad: Int
) {
    override fun toString(): String {
        return super.toString()
    }
}



fun ParkingModel.toDomain() = Parking(id, lat, lon, tags.capacidad)
fun ParkingEntity.toDomain() = Parking(id, lat, lon, capacidad)