package com.example.burparking.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.burparking.data.database.entities.ParkingEntity

/*
 * Interfaz DAO de los parkings
 */
@Dao
interface ParkingDao {

    /*
     * Selecciona todas los parkings y los devuelve en una lista
     */
    @Query("SELECT * FROM parking_table order by id LIMIT 15000")
    suspend fun getAllParkings(): List<ParkingEntity>

    /*
     * Inserta todas las direcciones recibidas mediante una lista
     * en caso de conflicto lo remplaza
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parkings: List<ParkingEntity>)

    /*
     * Elimina todas las direcciones de la base de datos
     */
    @Query("DELETE FROM parking_table")
    suspend fun deleteAllParking()
}