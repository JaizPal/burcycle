package com.example.burparking.data

import com.example.burparking.data.database.dao.ParkingDao
import com.example.burparking.data.database.entities.ParkingEntity
import com.example.burparking.data.network.ParkingService
import com.example.burparking.domain.model.Parking
import com.example.burparking.domain.model.toDomain
import javax.inject.Inject

class ParkingRepository @Inject constructor(private val api: ParkingService, private val parkingDao: ParkingDao){

    suspend fun getAllParkingsFromApi(): List<Parking> {
        val response = api.getparkings().parkings
        return response.map{it.toDomain()}
    }

    suspend fun getAllParkingsFromDatabase(): List<Parking> {
        val response = parkingDao.getAllParkings()
        return response.map {it.toDomain()}
    }

    suspend fun insertParkings(parkings: List<ParkingEntity>) {
        parkingDao.insertAll(parkings)
    }

    suspend fun clearParkings() {
        parkingDao.deleteAllParking()
    }
}