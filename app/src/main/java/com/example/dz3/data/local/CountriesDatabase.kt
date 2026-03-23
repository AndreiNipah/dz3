package com.example.dz3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavouriteCountryEntity::class,
        HistoryCountryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CountriesDatabase : RoomDatabase() {
    abstract fun favouriteCountryDao(): FavouriteCountryDao
    abstract fun historyCountryDao(): HistoryCountryDao
}