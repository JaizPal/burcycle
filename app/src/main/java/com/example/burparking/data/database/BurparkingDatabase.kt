package com.example.burparking.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.burparking.data.database.dao.DireccionDao
import com.example.burparking.data.database.dao.ParkingDao
import com.example.burparking.data.database.entities.DireccionEntity
import com.example.burparking.data.database.entities.ParkingEntity

/*
 * Definición de la base de datos
 */
@Database(entities = [ParkingEntity::class, DireccionEntity::class], version = 5)
abstract class BurparkingDatabase: RoomDatabase() {

    abstract fun getParkingDao(): ParkingDao
    abstract fun getDireccionDao(): DireccionDao
}