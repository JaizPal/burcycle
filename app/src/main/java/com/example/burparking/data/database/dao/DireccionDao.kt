package com.example.burparking.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.burparking.data.database.entities.DireccionEntity


@Dao
interface DireccionDao {

    @Query("SELECT * FROM direccion_table order by id")
    suspend fun getAllDirecciones(): List<DireccionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(direcciones: List<DireccionEntity>)

    @Query("DELETE FROM direccion_table")
    suspend fun deleteAllDirecciones()
}