package com.example.burparking.data.network

import com.example.burparking.data.model.direccion.DireccionElementModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
 * Devuelve un servicio de la API de dirección
 */
class DireccionService @Inject constructor(private val api: DireccionApiClient) {

    suspend fun getDirecciones(): DireccionElementModel {
        return withContext(Dispatchers.IO) {
            val response = api.getAllDirecciones()
            response.body() ?: DireccionElementModel(listOf())
        }
    }
}