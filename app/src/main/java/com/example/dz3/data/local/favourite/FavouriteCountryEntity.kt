package com.example.dz3.data.local.favourite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_countries")
data class FavouriteCountryEntity(
    @PrimaryKey val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val flagUrl: String,
    val population: Long,
)