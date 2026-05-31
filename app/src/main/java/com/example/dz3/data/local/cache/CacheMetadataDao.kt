package com.example.dz3.data.local.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheMetadataDao {

    @Query("SELECT * FROM cache_metadata WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): CacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: CacheMetadataEntity)
}