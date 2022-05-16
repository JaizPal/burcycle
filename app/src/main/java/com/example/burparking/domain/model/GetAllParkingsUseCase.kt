package com.example.burparking.domain.model

import com.example.burparking.data.ParkingRepository
import com.example.burparking.data.database.entities.toDatabase
import javax.inject.Inject

/*
 * Caso de uso para recuperar todos los Parkings
 * en el caso en que la BBDD Room esté vacía recupera
 * los Parkings de la API y las guarda en la BBDD
 */
class GetAllParkingsUseCase @Inject constructor(private val repository: ParkingRepository) {
    suspend operator fun invoke(): List<Parking> {
        var parkings = repository.getAllParkingsFromDatabase()

        return if (parkings.isEmpty()) {
            parkings = repository.getAllParkingsFromApi()
            repository.insertParkings(parkings.map { it.toDatabase() })
            parkings
        } else {
            repository.getAllParkingsFromDatabase()
        }
    }
}
