package com.example.dz3.data.remote

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("cca3") val cca3: String? = null,
    val name: NameDto? = null,
    val capital: List<String>? = null,
    val region: String? = null,
    val subregion: String? = null,
    val population: Long? = null,
    val flags: FlagsDto? = null,
    val languages: Map<String, String>? = null,
    val currencies: Map<String, CurrencyDto>? = null,
    val timezones: List<String>? = null,
    val borders: List<String>? = null,
    val maps: MapsDto? = null,
)


data class NameDto(
    val common: String? = null,
    val official: String? = null,
)


data class FlagsDto(
    val png: String? = null,
    val svg: String? = null,
)


data class CurrencyDto(
    val name: String? = null,
    val symbol: String? = null,
)


data class MapsDto(
    @SerializedName("googleMaps") val googleMaps: String? = null,
    @SerializedName("openStreetMaps") val openStreetMaps: String? = null,
)