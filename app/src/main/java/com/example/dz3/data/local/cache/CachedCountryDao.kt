package com.example.dz3.data.local.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedCountryDao {

    @Query("SELECT * FROM cached_countries ORDER BY name ASC")
    fun observeAllCountries(): Flow<List<CachedCountryEntity>>

    @Query("SELECT * FROM cached_countries ORDER BY name ASC")
    suspend fun getAllCountries(): List<CachedCountryEntity>

    @Query("""
        SELECT * FROM cached_countries
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    suspend fun searchCountriesByName(query: String): List<CachedCountryEntity>

    @Query("SELECT * FROM cached_countries WHERE code = :code LIMIT 1")
    suspend fun getCountryByCode(code: String): CachedCountryEntity?

    @Query("""
    SELECT * FROM cached_countries
    WHERE ',' || quizAreas || ',' LIKE '%,' || :quizArea || ',%'
    ORDER BY name ASC
""")
    suspend fun getCountriesByQuizArea(quizArea: String): List<CachedCountryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCountries(countries: List<CachedCountryEntity>)

    @Query("SELECT * FROM cached_country_details WHERE code = :code LIMIT 1")
    suspend fun getDetailsByCode(code: String): CachedCountryDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDetails(details: CachedCountryDetailsEntity)
}