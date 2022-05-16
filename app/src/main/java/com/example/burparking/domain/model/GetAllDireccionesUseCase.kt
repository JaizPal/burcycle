package com.example.burparking.domain.model

import com.example.burparking.data.DireccionRepository
import com.example.burparking.data.database.entities.toDatabase
import javax.inject.Inject

/*
 * Caso de uso para recuperar todas las direcciones
 * en el caso en que la BBDD Room esté vacía recupera
 * las direcciones de la API y las guarda en la BBDD
 */
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