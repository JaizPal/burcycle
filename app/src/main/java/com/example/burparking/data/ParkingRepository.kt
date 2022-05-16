package com.example.burparking.data

import com.example.burparking.data.database.dao.ParkingDao
import com.example.burparking.data.database.entities.ParkingEntity
import com.example.burparking.data.network.ParkingService
import com.example.burparking.domain.model.Parking
import com.example.burparking.domain.model.toDomain
import javax.inject.Inject

/*
 * Repositorio de los parkings
 * usando funciones suspendidas
 */
class ParkingRepository @Inject constructor(
    private val api: ParkingService,
    private val parkingDao: ParkingDao
) {

    /*
     * Recupera todos los parkings de la API y los devuelve en una lista
     */
    suspend fun getAllParkingsFromApi(): List<Parking> {
        val response = api.getparkings().parkings
        return response.map { it.toDomain() }
    }

    /*
     * Recupera todos los parkiongs de la BBDD y los devuelve en una lista
     */
    suspend fun getAllParkingsFromDatabase(): List<Parking> {
        val response = parkingDao.getAllParkings()
        return response.map { it.toDomain() }
    }

    suspend fun getClosestParkingFromApi(lat: Double, lon: Double): List<Parking> {
        val response = api.getClosestParkings(lat, lon).parkings
        return response.map {it.toDomain()}
    }

    /*
     * Inserta en la BBDD los parkings recibidas por parámetros
     */
    suspend fun insertParkings(parkings: List<ParkingEntity>) {
        parkingDao.insertAll(parkings)
    }

    /*
     * Elimina todas los parkings de la BBDD
     */
    suspend fun clearParkings() {
        parkingDao.deleteAllParking()
    }
}