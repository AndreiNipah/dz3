package com.example.dz3.fake

import com.example.dz3.data.remote.CountriesData
import com.example.dz3.data.remote.CountriesResponse
import com.example.dz3.data.remote.CountryDto
import com.example.dz3.data.remote.Fields
import com.example.dz3.data.remote.RestCountriesApi

class FakeCountriesApi(
    private val countries: List<CountryDto> = emptyList(),
    private val details: Map<String, CountryDto> = emptyMap()
) : RestCountriesApi {

    override suspend fun getAllCountries(
        responseFields: String,
        limit: Int
    ): CountriesResponse {
        return CountriesResponse(
            data = CountriesData(
                objects = countries.take(limit)
            )
        )
    }

    override suspend fun searchByName(
        name: String,
        responseFields: String,
        limit: Int
    ): CountriesResponse {
        val query = name.trim()

        val result = countries.filter { dto ->
            dto.names?.common
                ?.contains(query, ignoreCase = true) == true
        }

        return CountriesResponse(
            data = CountriesData(
                objects = result.take(limit)
            )
        )
    }

    override suspend fun getByCode(
        code: String,
        responseFields: String
    ): CountriesResponse {
        val dto = details[code]
            ?: countries.firstOrNull { it.codes?.alpha3 == code }

        return CountriesResponse(
            data = CountriesData(
                objects = listOfNotNull(dto)
            )
        )
    }
}