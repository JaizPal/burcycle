package com.example.burparking.domain.model

import com.example.burparking.data.ReverseDireccionRepository
import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import javax.inject.Inject

class GetReverseDireccionUseCase @Inject constructor(private val repository: ReverseDireccionRepository){
    suspend operator fun invoke(lon: Double, lat: Double): ReverseDireccionModel {
        return repository.getReverseDireccion(lon, lat)
    }
}