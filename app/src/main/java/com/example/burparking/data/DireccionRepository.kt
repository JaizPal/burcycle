package com.example.burparking.data

import com.example.burparking.data.database.dao.DireccionDao
import com.example.burparking.data.database.entities.DireccionEntity
import com.example.burparking.data.network.DireccionService
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.toDomain
import javax.inject.Inject

class DireccionRepository @Inject constructor(
    private val api: DireccionService,
    private val direccionDao: DireccionDao
) {

    suspend fun getAllDireccionesFromApi(): List<Direccion> {
        val response = api.getDirecciones().direcciones
        return response.map { it.toDomain() }
    }

    suspend fun getAllDireccionesFromDatabase(): List<Direccion> {
        val response = direccionDao.getAllDirecciones()
        return  response.map { it.toDomain() }
    }

    suspend fun insertDirecciones(direcciones: List<DireccionEntity>) {
        direccionDao.insertAll(direcciones)
    }

    suspend fun clearDirecciones() {
        direccionDao.deleteAllDirecciones()
    }
}