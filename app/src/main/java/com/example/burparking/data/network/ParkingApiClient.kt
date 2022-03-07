package com.example.burparking.data.network

import com.example.burparking.data.model.parking.ParkingElementModel
import retrofit2.Response
import retrofit2.http.GET

interface ParkingApiClient {
    @GET("interpreter?data=[out:json];node[amenity=bicycle_parking](42.32,-3.78,42.39,-3.61);out;")
    suspend fun getAllParkings(): Response<ParkingElementModel>
}