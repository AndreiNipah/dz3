package com.example.dz3.data.local.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_countries")
data class HistoryCountryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val flagUrl: String,
    val population: Long,
    val viewedAt: Long,
)