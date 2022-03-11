package com.example.burparking.domain.model

import com.example.burparking.data.database.entities.ParkingEntity
import com.example.burparking.data.model.parking.ParkingModel

data class Parking(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val capacidad: Int,
    var numero: String?,
    var calle: String?,
    var distancia: Float?,
    var direccion: Direccion?
) {
    override fun toString(): String {
        return super.toString()
    }
}



fun ParkingModel.toDomain() = Parking(id, lat, lon, tags.capacidad, null, null, null, null)
fun ParkingEntity.toDomain() = Parking(id, lat, lon, capacidad, null, null, null, null)