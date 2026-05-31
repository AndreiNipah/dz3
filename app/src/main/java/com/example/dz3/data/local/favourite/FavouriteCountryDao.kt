package com.example.dz3.data.local.favourite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteCountryDao {

    @Query("SELECT * FROM favourite_countries ORDER BY name ASC")
    fun observeAll(): Flow<List<FavouriteCountryEntity>>

    @Query("SELECT * FROM favourite_countries ORDER BY name ASC")
    suspend fun getAll(): List<FavouriteCountryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: FavouriteCountryEntity)

    @Query("DELETE FROM favourite_countries WHERE code = :code")
    suspend fun deleteByCode(code: String)
}