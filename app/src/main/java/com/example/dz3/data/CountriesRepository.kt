package com.example.dz3.data

import com.example.dz3.data.local.cache.CachedCountryDao
import com.example.dz3.data.local.favourite.FavouriteCountryDao
import com.example.dz3.data.local.history.HistoryCountryDao
import com.example.dz3.data.local.history.HistoryCountryEntity
import com.example.dz3.data.remote.RestCountriesApi
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountriesRepository @Inject constructor(
    private val api: RestCountriesApi,
    private val favouriteDao: FavouriteCountryDao,
    private val historyDao: HistoryCountryDao,
    private val cachedCountryDao: CachedCountryDao
) {

    fun observeFavourites(): Flow<List<Country>> {
        return favouriteDao.observeAll().map { list ->
            list.map { it.toCountry() }
        }
    }

    fun observeHistory(): Flow<List<HistoryCountryEntity>> {
        return historyDao.observeAll()
    }

    fun observeCachedCountries(): Flow<List<Country>> {
        return cachedCountryDao.observeAllCountries().map { list ->
            list.map { it.toCountry() }
        }
    }

    suspend fun addFavourite(country: Country) = withContext(Dispatchers.IO) {
        favouriteDao.insert(country.toFavouriteEntity())
    }

    suspend fun removeFavourite(code: String) = withContext(Dispatchers.IO) {
        favouriteDao.deleteByCode(code)
    }

    suspend fun addToHistory(country: Country) = withContext(Dispatchers.IO) {
        historyDao.insert(country.toHistoryEntity(System.currentTimeMillis()))
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAll()
    }

    suspend fun refreshCountries() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        val cached = api.getAllCountries()
            .mapNotNull { it.toCachedCountryEntity(updatedAt = now) }
            .sortedBy { it.name }

        cachedCountryDao.upsertCountries(cached)
    }

    suspend fun getAll(): List<Country> = withContext(Dispatchers.IO) {
        val cachedBeforeRequest = cachedCountryDao.getAllCountries()

        try {
            refreshCountries()

            cachedCountryDao.getAllCountries()
                .map { it.toCountry() }
                .sortedBy { it.name }
        } catch (ex: Exception) {
            cachedBeforeRequest
                .map { it.toCountry() }
                .sortedBy { it.name }
        }
    }

    suspend fun searchByName(query: String): List<Country> = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()

            val remote = api.searchByName(name = query)
                .mapNotNull { it.toCachedCountryEntity(updatedAt = now) }
                .sortedBy { it.name }

            cachedCountryDao.upsertCountries(remote)

            remote.map { it.toCountry() }
        } catch (ex: HttpException) {
            if (ex.code() == 404) {
                emptyList()
            } else {
                searchCachedByName(query)
            }
        } catch (ex: Exception) {
            searchCachedByName(query)
        }
    }

    suspend fun getDetails(code: String): CountryDetails = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()

            val dto = api.getByCodes(codes = code).firstOrNull()
                ?: throw IllegalStateException("Country not found: $code")

            val detailsEntity = dto.toCachedCountryDetailsEntity(updatedAt = now)
                ?: throw IllegalStateException("Bad data for: $code")

            cachedCountryDao.upsertDetails(detailsEntity)

            dto.toCachedCountryEntity(updatedAt = now)?.let { countryEntity ->
                cachedCountryDao.upsertCountries(listOf(countryEntity))
            }

            detailsEntity.toCountryDetails()
        } catch (ex: Exception) {
            val cachedDetails = cachedCountryDao.getDetailsByCode(code)
            if (cachedDetails != null) {
                return@withContext cachedDetails.toCountryDetails()
            }

            val cachedCountry = cachedCountryDao.getCountryByCode(code)
            if (cachedCountry != null) {
                return@withContext cachedCountry.toFallbackDetails()
            }

            throw ex
        }
    }

    private suspend fun searchCachedByName(query: String): List<Country> {
        return cachedCountryDao.searchCountriesByName(query)
            .map { it.toCountry() }
            .sortedBy { it.name }
    }
}