package com.example.burparking.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.burparking.data.database.entities.ParkingEntity

@Dao
interface ParkingDao {

    @Query("SELECT * FROM parking_table order by id LIMIT 15000")
    suspend fun getAllParkings(): List<ParkingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parkings: List<ParkingEntity>)

    @Query("DELETE FROM parking_table")
    suspend fun deleteAllParking()
}