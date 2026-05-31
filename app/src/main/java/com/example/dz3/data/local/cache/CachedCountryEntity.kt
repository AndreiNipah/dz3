package com.example.dz3.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_countries")
data class CachedCountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val flagUrl: String,
    val population: Long,
    val quizAreas: String,
    val updatedAt: Long
)