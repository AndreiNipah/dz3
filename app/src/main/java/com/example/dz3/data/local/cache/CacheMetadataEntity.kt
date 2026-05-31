package com.example.dz3.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_metadata")
data class CacheMetadataEntity(
    @PrimaryKey val key: String,
    val updatedAt: Long
)