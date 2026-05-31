package com.example.dz3.di

import android.content.Context
import androidx.room.Room
import com.example.dz3.data.local.cache.CachedCountryDao
import com.example.dz3.data.local.CountriesDatabase
import com.example.dz3.data.local.favourite.FavouriteCountryDao
import com.example.dz3.data.local.history.HistoryCountryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.dz3.data.local.quiz.QuizDao
import com.example.dz3.data.local.cache.CacheMetadataDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCountriesDatabase(
        @ApplicationContext context: Context
    ): CountriesDatabase {
        return Room.databaseBuilder(
            context,
            CountriesDatabase::class.java,
            "countries_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFavouriteCountryDao(
        db: CountriesDatabase
    ): FavouriteCountryDao {
        return db.favouriteCountryDao()
    }

    @Provides
    @Singleton
    fun provideHistoryCountryDao(
        db: CountriesDatabase
    ): HistoryCountryDao {
        return db.historyCountryDao()
    }

    @Provides
    @Singleton
    fun provideCachedCountryDao(
        db: CountriesDatabase
    ): CachedCountryDao {
        return db.cachedCountryDao()
    }

    @Provides
    @Singleton
    fun provideQuizDao(
        db: CountriesDatabase
    ): QuizDao {
        return db.quizDao()
    }

    @Provides
    @Singleton
    fun provideCacheMetadataDao(
        db: CountriesDatabase
    ): CacheMetadataDao {
        return db.cacheMetadataDao()
    }

}