package com.example.dz3.data.remote

import com.google.gson.annotations.SerializedName

data class CountriesResponse(
    val data: CountriesData? = null
)

data class CountriesData(
    val objects: List<CountryDto>? = null
)

data class CountryDto(
    val names: NamesDto? = null,
    val codes: CodesDto? = null,
    val capitals: List<CapitalDto>? = null,
    val region: String? = null,
    val subregion: String? = null,
    val population: Long? = null,
    val flag: FlagDto? = null,
    val languages: List<LanguageDto>? = null,
    val currencies: List<CurrencyDto>? = null,
    val timezones: List<String>? = null,
    val borders: List<String>? = null,
    val maps: MapsDto? = null,
)

data class CapitalDto(
    val name: String? = null,
)

data class NamesDto(
    val common: String? = null,
    val official: String? = null,
)

data class CodesDto(
    @SerializedName("alpha_3") val alpha3: String? = null,
    @SerializedName("alpha_2") val alpha2: String? = null,
)

data class FlagDto(
    @SerializedName("url_png") val urlPng: String? = null,
    @SerializedName("url_svg") val urlSvg: String? = null,
    val emoji: String? = null,
)

data class LanguageDto(
    val name: String? = null,
    @SerializedName("native") val nativeName: String? = null,
)

data class CurrencyDto(
    val code: String? = null,
    val name: String? = null,
    val symbol: String? = null,
)

data class MapsDto(
    @SerializedName("google_maps") val googleMaps: String? = null,
)