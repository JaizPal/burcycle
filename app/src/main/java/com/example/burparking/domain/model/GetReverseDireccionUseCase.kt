package com.example.burparking.domain.model

import com.example.burparking.data.ReverseDireccionRepository
import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import javax.inject.Inject

/*
 * Devuelve una ReverseDireccionModel recuperada de la API
 * según la longitud y latitud mandada por parámetros
 */
class GetReverseDireccionUseCase @Inject constructor(private val repository: ReverseDireccionRepository){
    suspend operator fun invoke(lon: Double, lat: Double): ReverseDireccionModel {
        return repository.getReverseDireccion(lon, lat)
    }
}