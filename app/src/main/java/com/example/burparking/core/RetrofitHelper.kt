package com.example.burparking.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lz4.overpass-api.de/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}