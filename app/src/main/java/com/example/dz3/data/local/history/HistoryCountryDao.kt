package com.example.dz3.data.local.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryCountryDao {

    @Query("SELECT * FROM history_countries ORDER BY viewedAt DESC")
    fun observeAll(): Flow<List<HistoryCountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: HistoryCountryEntity)

    @Query("DELETE FROM history_countries")
    suspend fun clearAll()
}