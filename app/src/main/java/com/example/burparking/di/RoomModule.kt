package com.example.burparking.di

import android.content.Context
import androidx.room.Room
import com.example.burparking.data.database.BurparkingDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val PARKING_DATABASE_NAME = "parking_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, BurparkingDatabase::class.java, PARKING_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providesParkingDao(db:BurparkingDatabase) = db.getParkingDao()
}