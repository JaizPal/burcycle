package com.example.burparking.data

import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import com.example.burparking.data.network.ReverseDireccionService
import javax.inject.Inject

/*
 * Repositorio de las reverseDirecciones
 * usando funciones suspendidas
 */
class ReverseDireccionRepository @Inject constructor(
    private val api: ReverseDireccionService
) {
    /*
     * Recupera todas las reverseDireccion de la API y los devuelve en una lista
     */
    suspend fun getReverseDireccion(lon: Double, lat: Double): ReverseDireccionModel {
        val response = api.getReverseDirecciones(lon, lat)
        return response.features[0].reverseDireccionModel
    }
}