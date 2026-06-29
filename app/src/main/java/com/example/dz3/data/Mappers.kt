package com.example.dz3.data

import com.example.dz3.data.local.FavouriteCountryEntity
import com.example.dz3.data.remote.CountryDto
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.data.local.HistoryCountryEntity
import com.example.dz3.model.HistoryCountry

fun Country.toHistoryEntity(viewedAt: Long): HistoryCountryEntity {
    return HistoryCountryEntity(
        code = code,
        name = name,
        capital = capital,
        region = region,
        flagUrl = flagUrl,
        population = population,
        viewedAt = viewedAt
    )
}

fun HistoryCountryEntity.toHistoryCountry(): HistoryCountry {
    return HistoryCountry(
        id = id,
        viewedAt = viewedAt,
        country = Country(
            code = code,
            name = name,
            capital = capital,
            region = region,
            flagUrl = flagUrl,
            population = population
        )
    )
}

fun HistoryCountryEntity.toCountry(): Country {
    return Country(
        code = code,
        name = name,
        capital = capital,
        region = region,
        flagUrl = flagUrl,
        population = population
    )
}
fun Country.toFavouriteEntity(): FavouriteCountryEntity {
    return FavouriteCountryEntity(
        code = code,
        name = name,
        capital = capital,
        region = region,
        flagUrl = flagUrl,
        population = population
    )
}

fun FavouriteCountryEntity.toCountry(): Country {
    return Country(
        code = code,
        name = name,
        capital = capital,
        region = region,
        flagUrl = flagUrl,
        population = population
    )
}

fun CountryDto.toCountryOrNull(): Country? {
    val code = codes?.alpha3?.trim().orEmpty()
    val commonName = names?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    val capitalStr = capitals
        ?.firstOrNull()
        ?.name
        ?.trim()
        .orEmpty()
        .ifBlank { "—" }
    val regionStr = region?.trim().orEmpty().ifBlank { "—" }
    val flagUrl = flag?.urlPng ?: flag?.urlSvg ?: ""

    return Country(
        code = code,
        name = commonName,
        capital = capitalStr,
        region = regionStr,
        flagUrl = flagUrl,
        population = population ?: 0L
    )
}

fun CountryDto.toCountryDetailsOrNull(): CountryDetails? {
    val code = codes?.alpha3?.trim().orEmpty()
    val commonName = names?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    val officialName = names?.official?.trim().orEmpty().ifBlank { commonName }
    val capitalStr = capitals
        ?.firstOrNull()
        ?.name
        ?.trim()
        .orEmpty()
        .ifBlank { "—" }
    val regionStr = region?.trim().orEmpty().ifBlank { "—" }
    val subregionStr = subregion?.trim().orEmpty().ifBlank { "—" }
    val flagUrl = flag?.urlPng ?: flag?.urlSvg ?: ""

    val languagesStr = languages
        ?.mapNotNull { it.name?.trim()?.takeIf { s -> s.isNotBlank() } }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }

    val currenciesStr = currencies
        ?.mapNotNull { c ->
            val code = c.code?.trim().orEmpty()
            val name = c.name?.trim().orEmpty()
            val symbol = c.symbol?.trim().orEmpty()

            when {
                name.isBlank() && symbol.isBlank() && code.isBlank() -> null
                name.isBlank() && symbol.isBlank() -> code
                symbol.isBlank() && code.isBlank() -> name
                symbol.isBlank() -> "$name ($code)"
                code.isBlank() -> "$name ($symbol)"
                else -> "$name ($symbol, $code)"
            }
        }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }

    val timezonesStr = timezones
        ?.mapNotNull { it.trim().takeIf { s -> s.isNotBlank() } }
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }

    val bordersStr = borders
        ?.mapNotNull { it.trim().takeIf { s -> s.isNotBlank() } }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }

    val googleMapsUrl = maps?.googleMaps?.trim().orEmpty()

    return CountryDetails(
        code = code,
        name = commonName,
        officialName = officialName,
        capital = capitalStr,
        region = regionStr,
        subregion = subregionStr,
        flagUrl = flagUrl,
        population = population ?: 0L,
        languages = languagesStr,
        currencies = currenciesStr,
        timezones = timezonesStr,
        borders = bordersStr,
        googleMapsUrl = googleMapsUrl
    )
}