package com.example.burparking.di

import com.example.burparking.data.network.DireccionApiClient
import com.example.burparking.data.network.ParkingApiClient
import com.example.burparking.data.network.ReverseDireccionApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private var client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    @Singleton
    @Provides
    @Named("overpass")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lz4.overpass-api.de/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Named("photon")
    fun provideReverseRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://photon.komoot.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideParkingApiClient(@Named("overpass")retrofit: Retrofit): ParkingApiClient {
        return retrofit.create(ParkingApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideDireccionApiClient(@Named("overpass")retrofit: Retrofit): DireccionApiClient {
        return retrofit.create(DireccionApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideReverseDireccionApiClient(@Named("photon")retrofit: Retrofit): ReverseDireccionApiClient {
        return retrofit.create(ReverseDireccionApiClient::class.java)
    }
}