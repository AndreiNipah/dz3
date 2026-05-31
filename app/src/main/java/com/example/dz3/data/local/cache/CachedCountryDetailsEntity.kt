package com.example.dz3.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_country_details")
data class CachedCountryDetailsEntity(
    @PrimaryKey val code: String,
    val name: String,
    val officialName: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val flagUrl: String,
    val population: Long,
    val languages: String,
    val currencies: String,
    val timezones: String,
    val borders: String,
    val googleMapsUrl: String,
    val quizAreas: String,
    val updatedAt: Long
)