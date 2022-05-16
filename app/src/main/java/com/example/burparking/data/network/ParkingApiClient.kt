package com.example.burparking.data.network

import com.example.burparking.data.model.parking.ParkingElementModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * Interface que recupera la información de los parkings
 */
interface ParkingApiClient {
    @GET("interpreter?data=[out:json];node[amenity=bicycle_parking](42.32,-3.78,42.39,-3.61);out;")
    suspend fun getAllParkings(): Response<ParkingElementModel>

    @GET("interpreter")
    suspend fun getClosestParkings(
        @Query("data") data: String
    ): Response<ParkingElementModel>
}