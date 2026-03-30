package com.example.dz3.fake

import com.example.dz3.data.remote.CountryDto
import com.example.dz3.data.remote.RestCountriesApi

class FakeCountriesApi : RestCountriesApi {

    var countries: List<CountryDto> = emptyList()
    var shouldThrow = false

    override suspend fun getAllCountries(fields: String): List<CountryDto> {
        if (shouldThrow) throw RuntimeException("API error")
        return countries
    }

    override suspend fun searchByName(name: String, fields: String): List<CountryDto> {
        if (shouldThrow) throw RuntimeException("API error")
        return countries.filter {
            it.name?.common?.contains(name, ignoreCase = true) == true
        }
    }

    override suspend fun getByCodes(codes: String, fields: String): List<CountryDto> {
        if (shouldThrow) throw RuntimeException("API error")
        return countries.filter { it.cca3 == codes }
    }
}