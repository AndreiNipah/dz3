package com.example.dz3.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestCountriesApi {

    @GET("all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = Fields.LIST
    ): List<CountryDto>

    @GET("name/{name}")
    suspend fun searchByName(
        @Path("name") name: String,
        @Query("fields") fields: String = Fields.LIST
    ): List<CountryDto>

    @GET("alpha")
    suspend fun getByCodes(
        @Query("codes") codes: String,
        @Query("fields") fields: String = Fields.DETAIL
    ): List<CountryDto>
}

object Fields {
    const val LIST = "name,cca3,capital,region,flags,population"
    const val DETAIL = "name,cca3,capital,region,subregion,flags,population,languages,currencies,timezones,borders,maps"
}