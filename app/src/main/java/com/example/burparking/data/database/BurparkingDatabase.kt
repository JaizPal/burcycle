package com.example.burparking.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.burparking.data.database.dao.ParkingDao
import com.example.burparking.data.database.entities.ParkingEntity

/*
TODO
Añadir después la entity de las calles
 */
@Database(entities = [ParkingEntity::class], version = 1)
abstract class BurparkingDatabase: RoomDatabase() {

    abstract fun getParkingDao(): ParkingDao

}