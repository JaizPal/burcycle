package com.example.burparking.data

import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import com.example.burparking.data.network.ReverseDireccionService
import javax.inject.Inject

class ReverseDireccionRepository @Inject constructor(
    private val api: ReverseDireccionService
) {
    suspend fun getReverseDireccion(lon: Double, lat: Double): ReverseDireccionModel {
        val response = api.getReverseDirecciones(lon, lat)
        return response.features[0].reverseDireccionModel
    }
}