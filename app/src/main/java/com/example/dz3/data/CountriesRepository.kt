package com.example.dz3.data

import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.NetworkModule
import com.example.dz3.data.remote.RestCountriesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CountriesRepository(
    private val api: RestCountriesApi = NetworkModule.api
) {
    suspend fun getAll(): List<Country> = withContext(Dispatchers.IO) {
        api.getAllCountries()
            .mapNotNull { it.toCountryOrNull() }
            .sortedBy { it.name }
    }

    suspend fun searchByName(query: String): List<Country> = withContext(Dispatchers.IO) {
        api.searchByName(name = query)
            .mapNotNull { it.toCountryOrNull() }
            .sortedBy { it.name }
    }

    suspend fun getDetails(code: String): CountryDetails = withContext(Dispatchers.IO) {
        val dto = api.getByCodes(codes = code).firstOrNull()
            ?: throw IllegalStateException("Country not found: $code")
        dto.toCountryDetailsOrNull()
            ?: throw IllegalStateException("Bad data for: $code")
    }
}