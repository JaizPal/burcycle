package com.example.burparking.domain.model

import com.example.burparking.data.ParkingRepository
import com.example.burparking.data.database.entities.toDatabase
import javax.inject.Inject

class GetAllParkingsUseCase @Inject constructor(private val repository: ParkingRepository) {
    suspend operator fun invoke(): List<Parking> {
        var parkings = repository.getAllParkingsFromDatabase()

        return if (parkings.isEmpty()) {
            parkings = repository.getAllParkingsFromApi()
            repository.insertParkings(parkings.map { it.toDatabase() })
            parkings
//            repository.clearParkings()
//            repository.insertParkings(parkings.map{it.toDatabase()})
//            parkings
        } else {
            repository.getAllParkingsFromDatabase()
        }
    }
}
