package com.example.dz3.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestCountriesApi {

    @GET("v5")
    suspend fun getAllCountries(
        @Query("response_fields") responseFields: String = Fields.LIST,
        @Query("limit") limit: Int = 100
    ): CountriesResponse

    @GET("v5/name")
    suspend fun searchByName(
        @Query("q") name: String,
        @Query("response_fields") responseFields: String = Fields.LIST,
        @Query("limit") limit: Int = 100
    ): CountriesResponse

    @GET("v5/codes.alpha_3/{code}")
    suspend fun getByCode(
        @Path("code") code: String,
        @Query("response_fields") responseFields: String = Fields.DETAIL
    ): CountriesResponse
}

object Fields {
    const val LIST = "names.common,codes.alpha_3,capitals,region,flag.url_png,flag.url_svg,population"
    const val DETAIL = "names.common,names.official,codes.alpha_3,capitals,region,subregion,flag.url_png,flag.url_svg,population,languages,currencies,timezones,borders,maps.google_maps"
}