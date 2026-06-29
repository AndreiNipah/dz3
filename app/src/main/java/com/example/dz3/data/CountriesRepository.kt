package com.example.dz3.data

import com.example.dz3.data.local.FavouriteCountryDao
import com.example.dz3.data.local.HistoryCountryDao
import com.example.dz3.data.remote.RestCountriesApi
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException
import com.example.dz3.model.HistoryCountry

@Singleton
class CountriesRepository @Inject constructor(
    private val api: RestCountriesApi,
    private val favouriteDao: FavouriteCountryDao,
    private val historyDao: HistoryCountryDao
) {
    fun observeFavourites(): Flow<List<Country>> {
        return favouriteDao.observeAll().map { list ->
            list.map { it.toCountry() }
        }
    }

    fun observeHistory(): Flow<List<HistoryCountry>> {
        return historyDao.observeAll().map { list ->
            list.map { it.toHistoryCountry() }
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

    suspend fun getAll(): List<Country> = withContext(Dispatchers.IO) {
        api.getAllCountries()
            .data
            ?.objects
            .orEmpty()
            .mapNotNull { it.toCountryOrNull() }
            .sortedBy { it.name }
    }

    suspend fun searchByName(query: String): List<Country> = withContext(Dispatchers.IO) {
        try {
            api.searchByName(name = query)
                .data
                ?.objects
                .orEmpty()
                .mapNotNull { it.toCountryOrNull() }
                .sortedBy { it.name }
        } catch (ex: HttpException) {
            if (ex.code() == 404) {
                emptyList()
            } else {
                throw ex
            }
        }
    }

    suspend fun getDetails(code: String): CountryDetails = withContext(Dispatchers.IO) {
        val dto = api.getByCode(code = code)
            .data
            ?.objects
            .orEmpty()
            .firstOrNull()
            ?: throw IllegalStateException("Country not found: $code")

        dto.toCountryDetailsOrNull()
            ?: throw IllegalStateException("Bad data for: $code")
    }
}