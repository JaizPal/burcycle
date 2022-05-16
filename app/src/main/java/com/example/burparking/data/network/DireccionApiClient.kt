package com.example.burparking.data.network

import com.example.burparking.data.model.direccion.DireccionElementModel
import retrofit2.Response
import retrofit2.http.GET

/*
 * Interface que recupera la información de las direcciones
 */
interface DireccionApiClient {
    @GET("interpreter?data=[out:json];node[\"addr:housenumber\"](42.32,-3.78,42.39,-3.61);out;")
    suspend fun getAllDirecciones(): Response<DireccionElementModel>
}