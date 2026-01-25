package com.example.dz3.model

data class Country(
    val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val flagUrl: String,
    val population: Long,
)

data class CountryDetails(
    val code: String,
    val name: String,
    val officialName: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val flagUrl: String,
    val population: Long,
    val languages: String,
    val currencies: String,
    val timezones: String,
    val borders: String,
    val googleMapsUrl: String,
)