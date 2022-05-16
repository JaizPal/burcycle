package com.example.burparking.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.burparking.data.database.entities.DireccionEntity

/*
 * Interface DAO de la dirección
 */
@Dao
interface DireccionDao {

    /*
     * Selecciona todas las direcciones y las devuelve en una lista
     */
    @Query("SELECT * FROM direccion_table order by id")
    suspend fun getAllDirecciones(): List<DireccionEntity>

    /*
     * Inserta todas las direcciones recibidas mediante una lista
     * en caso de conflicto lo remplaza
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(direcciones: List<DireccionEntity>)

    /*
     * Elimina todas las direcciones de la base de datos
     */
    @Query("DELETE FROM direccion_table")
    suspend fun deleteAllDirecciones()
}