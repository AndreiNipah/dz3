package com.example.dz3.di

import android.content.Context
import androidx.room.Room
import com.example.dz3.data.local.CountriesDatabase
import com.example.dz3.data.local.FavouriteCountryDao
import com.example.dz3.data.local.HistoryCountryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}