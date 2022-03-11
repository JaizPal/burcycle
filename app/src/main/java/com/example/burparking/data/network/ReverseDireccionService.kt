package com.example.burparking.data.network

import com.example.burparking.data.model.reversedireccion.ReverseDireccionFeaturesModel
import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import com.example.burparking.data.model.reversedireccion.ReverseDireccionPropertiesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReverseDireccionService @Inject constructor(private val api: ReverseDireccionApiClient) {

    suspend fun getReverseDirecciones(lon: Double, lat: Double): ReverseDireccionFeaturesModel {
        return withContext(Dispatchers.IO) {
            val response = api.getReverseDireccion(lon, lat)
            response.body() ?: ReverseDireccionFeaturesModel(emptyList())
        }
    }
}