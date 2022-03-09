package com.example.burparking.data.network

import com.example.burparking.data.model.parking.ParkingElementModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ParkingService @Inject constructor(private val api: ParkingApiClient) {

    suspend fun getparkings(): ParkingElementModel {
        return withContext(Dispatchers.IO) {
            val response = api.getAllParkings()
            response.body() ?: ParkingElementModel(listOf())
        }
    }

    suspend fun getClosestParkings(lat: Double, lon: Double): ParkingElementModel {
        return withContext(Dispatchers.IO) {
            val response =
                api.getClosestParkings("[out:json];node[amenity=bicycle_parking](around:700,$lat,$lon);out;")
            response.body() ?: ParkingElementModel(listOf())
        }
    }
}