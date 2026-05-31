package com.example.dz3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dz3.data.local.cache.CacheMetadataDao
import com.example.dz3.data.local.cache.CacheMetadataEntity
import com.example.dz3.data.local.cache.CachedCountryDao
import com.example.dz3.data.local.cache.CachedCountryDetailsEntity
import com.example.dz3.data.local.cache.CachedCountryEntity
import com.example.dz3.data.local.favourite.FavouriteCountryDao
import com.example.dz3.data.local.favourite.FavouriteCountryEntity
import com.example.dz3.data.local.history.HistoryCountryDao
import com.example.dz3.data.local.history.HistoryCountryEntity
import com.example.dz3.data.local.quiz.CountryLearningProgressEntity
import com.example.dz3.data.local.quiz.QuizAnswerEntity
import com.example.dz3.data.local.quiz.QuizDao
import com.example.dz3.data.local.quiz.QuizSessionEntity

@Database(
    entities = [
        FavouriteCountryEntity::class,
        HistoryCountryEntity::class,
        CachedCountryEntity::class,
        CachedCountryDetailsEntity::class,
        QuizSessionEntity::class,
        QuizAnswerEntity::class,
        CountryLearningProgressEntity::class,
        CacheMetadataEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class CountriesDatabase : RoomDatabase() {
    abstract fun favouriteCountryDao(): FavouriteCountryDao
    abstract fun historyCountryDao(): HistoryCountryDao
    abstract fun cachedCountryDao(): CachedCountryDao
    abstract fun quizDao(): QuizDao
    abstract fun cacheMetadataDao(): CacheMetadataDao
}