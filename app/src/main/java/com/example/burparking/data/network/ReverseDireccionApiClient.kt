package com.example.burparking.data.network

import com.example.burparking.data.model.reversedireccion.ReverseDireccionFeaturesModel
import com.example.burparking.data.model.reversedireccion.ReverseDireccionModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseDireccionApiClient {
    @GET("reverse")
    suspend fun getReverseDireccion(
        @Query("lon") lon: Double,
        @Query("lat") lat: Double
    ): Response<ReverseDireccionFeaturesModel>
}