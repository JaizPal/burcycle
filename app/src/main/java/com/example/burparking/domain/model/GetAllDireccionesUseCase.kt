package com.example.burparking.domain.model

import com.example.burparking.data.DireccionRepository
import com.example.burparking.data.database.entities.toDatabase
import javax.inject.Inject

class GetAllDireccionesUseCase @Inject constructor(private val repository: DireccionRepository) {
    suspend operator fun invoke(): List<Direccion> {
        var direcciones = repository.getAllDireccionesFromDatabase()

        return if(direcciones.isEmpty()) {
            direcciones = repository.getAllDireccionesFromApi()
            repository.insertDirecciones(direcciones.map { it.toDatabase()})
            direcciones
        } else {
            repository.getAllDireccionesFromDatabase()
        }
    }
}