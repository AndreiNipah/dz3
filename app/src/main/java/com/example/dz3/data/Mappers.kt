package com.example.dz3.data

import com.example.dz3.data.local.FavouriteCountryEntity
import com.example.dz3.data.remote.CountryDto
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.data.local.HistoryCountryEntity


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
    val code = cca3?.trim().orEmpty()
    val commonName = name?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    val capitalStr = capital?.firstOrNull()?.trim().orEmpty().ifBlank { "—" }
    val regionStr = region?.trim().orEmpty().ifBlank { "—" }
    val flagUrl = when (cca3) {
        "AFG" -> "https://flagcdn.com/w320/af.png"
        else -> flags?.png ?: flags?.svg ?: ""
        /*
        К Афганистану пришлось применить "костыль", так как на данный момент
        де-юре международно признанный и де-факто используемый в стране флаги отличаются.
        А используемая в REST Countries ссылка на флаг Афганистан — единственная ссылка
        кривого формата, ведущая на файл формата .svg.png на вики,
        в связи с чем отображение деталей Афганистана ломалось.
        */
    }

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
    val code = cca3?.trim().orEmpty()
    val commonName = name?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    val officialName = name?.official?.trim().orEmpty().ifBlank { commonName }
    val capitalStr = capital?.firstOrNull()?.trim().orEmpty().ifBlank { "—" }
    val regionStr = region?.trim().orEmpty().ifBlank { "—" }
    val subregionStr = subregion?.trim().orEmpty().ifBlank { "—" }
    val flagUrl = when (cca3) {
        "AFG" -> "https://flagcdn.com/w320/af.png"
        else -> flags?.png ?: flags?.svg ?: ""
    }

    val languagesStr = languages
        ?.values
        ?.mapNotNull { it.trim().takeIf { s -> s.isNotBlank() } }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }

    val currenciesStr = currencies
        ?.values
        ?.mapNotNull { c ->
            val n = c.name?.trim().orEmpty()
            val s = c.symbol?.trim().orEmpty()
            when {
                n.isBlank() && s.isBlank() -> null
                s.isBlank() -> n
                n.isBlank() -> s
                else -> "$n ($s)"
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