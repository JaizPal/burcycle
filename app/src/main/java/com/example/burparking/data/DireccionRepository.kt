package com.example.burparking.data

import com.example.burparking.data.database.dao.DireccionDao
import com.example.burparking.data.database.entities.DireccionEntity
import com.example.burparking.data.network.DireccionService
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.toDomain
import javax.inject.Inject

/*
 * Repositorio de las direcciones
 * usando funciones suspendidas
 */
class DireccionRepository @Inject constructor(
    private val api: DireccionService,
    private val direccionDao: DireccionDao
) {

    /*
     * Recupera todas las direcciones de la API y las devuelve en una lista
     */
    suspend fun getAllDireccionesFromApi(): List<Direccion> {
        val response = api.getDirecciones().direcciones
        return response.map { it.toDomain() }
    }
    /*
     * Recupera todas las direcciones de la BBDD y las devuelve en una lista
     */
    suspend fun getAllDireccionesFromDatabase(): List<Direccion> {
        val response = direccionDao.getAllDirecciones()
        return  response.map { it.toDomain() }
    }

    /*
     * Inserta en la BBDD las direcciones recibidas por parámetros
     */
    suspend fun insertDirecciones(direcciones: List<DireccionEntity>) {
        direccionDao.insertAll(direcciones)
    }

    /*
     * Elimina todas las direcciones de la BBDD
     */
    suspend fun clearDirecciones() {
        direccionDao.deleteAllDirecciones()
    }
}