package com.example.burparking.domain.model

import com.example.burparking.data.ParkingRepository
import javax.inject.Inject

class GetClosestParkingsUseCase @Inject constructor(private val repository: ParkingRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): List<Parking> {
        return repository.getClosestParkingFromApi(lat, lon)
    }
}