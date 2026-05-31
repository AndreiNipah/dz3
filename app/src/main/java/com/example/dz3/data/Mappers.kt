package com.example.dz3.data

import com.example.dz3.data.local.cache.CachedCountryDetailsEntity
import com.example.dz3.data.local.cache.CachedCountryEntity
import com.example.dz3.data.local.favourite.FavouriteCountryEntity
import com.example.dz3.data.local.history.HistoryCountryEntity
import com.example.dz3.data.remote.CountryDto
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.model.QuizArea

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

    return Country(
        code = code,
        name = commonName,
        capital = capitalString(),
        region = regionString(),
        flagUrl = flagUrl(),
        population = population ?: 0L
    )
}

fun CountryDto.toCountryDetailsOrNull(): CountryDetails? {
    val code = cca3?.trim().orEmpty()
    val commonName = name?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    return CountryDetails(
        code = code,
        name = commonName,
        officialName = name?.official?.trim().orEmpty().ifBlank { commonName },
        capital = capitalString(),
        region = regionString(),
        subregion = subregionString(),
        flagUrl = flagUrl(),
        population = population ?: 0L,
        languages = languagesString(),
        currencies = currenciesString(),
        timezones = timezonesString(),
        borders = bordersString(),
        googleMapsUrl = maps?.googleMaps?.trim().orEmpty()
    )
}

fun CountryDto.toCachedCountryEntity(updatedAt: Long): CachedCountryEntity? {
    val code = cca3?.trim().orEmpty()
    val commonName = name?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    val region = regionString()
    val subregion = subregionString()

    return CachedCountryEntity(
        code = code,
        name = commonName,
        capital = capitalString(),
        region = region,
        subregion = subregion,
        flagUrl = flagUrl(),
        population = population ?: 0L,
        quizAreas = quizAreasString(region = region, subregion = subregion),
        updatedAt = updatedAt
    )
}

fun CountryDto.toCachedCountryDetailsEntity(updatedAt: Long): CachedCountryDetailsEntity? {
    val code = cca3?.trim().orEmpty()
    val commonName = name?.common?.trim().orEmpty()
    if (code.isBlank() || commonName.isBlank()) return null

    val region = regionString()
    val subregion = subregionString()

    return CachedCountryDetailsEntity(
        code = code,
        name = commonName,
        officialName = name?.official?.trim().orEmpty().ifBlank { commonName },
        capital = capitalString(),
        region = region,
        subregion = subregion,
        flagUrl = flagUrl(),
        population = population ?: 0L,
        languages = languagesString(),
        currencies = currenciesString(),
        timezones = timezonesString(),
        borders = bordersString(),
        googleMapsUrl = maps?.googleMaps?.trim().orEmpty(),
        quizAreas = quizAreasString(region = region, subregion = subregion),
        updatedAt = updatedAt
    )
}

fun CachedCountryEntity.toCountry(): Country {
    return Country(
        code = code,
        name = name,
        capital = capital,
        region = region,
        flagUrl = flagUrl,
        population = population
    )
}

fun CachedCountryDetailsEntity.toCountryDetails(): CountryDetails {
    return CountryDetails(
        code = code,
        name = name,
        officialName = officialName,
        capital = capital,
        region = region,
        subregion = subregion,
        flagUrl = flagUrl,
        population = population,
        languages = languages,
        currencies = currencies,
        timezones = timezones,
        borders = borders,
        googleMapsUrl = googleMapsUrl
    )
}

fun CachedCountryEntity.toFallbackDetails(): CountryDetails {
    return CountryDetails(
        code = code,
        name = name,
        officialName = name,
        capital = capital,
        region = region,
        subregion = subregion,
        flagUrl = flagUrl,
        population = population,
        languages = "—",
        currencies = "—",
        timezones = "—",
        borders = "—",
        googleMapsUrl = ""
    )
}

private fun CountryDto.capitalString(): String {
    return capital?.firstOrNull()?.trim().orEmpty().ifBlank { "—" }
}

private fun CountryDto.regionString(): String {
    return region?.trim().orEmpty().ifBlank { "—" }
}

private fun CountryDto.subregionString(): String {
    return subregion?.trim().orEmpty().ifBlank { "—" }
}

private fun CountryDto.flagUrl(): String {
    return when (cca3) {
        "AFG" -> "https://flagcdn.com/w320/af.png"
        else -> flags?.png ?: flags?.svg ?: ""
    }
}

private fun CountryDto.languagesString(): String {
    return languages
        ?.values
        ?.mapNotNull { it.trim().takeIf { s -> s.isNotBlank() } }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }
}

private fun CountryDto.currenciesString(): String {
    return currencies
        ?.values
        ?.mapNotNull { c ->
            val name = c.name?.trim().orEmpty()
            val symbol = c.symbol?.trim().orEmpty()

            when {
                name.isBlank() && symbol.isBlank() -> null
                symbol.isBlank() -> name
                name.isBlank() -> symbol
                else -> "$name ($symbol)"
            }
        }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }
}

private fun CountryDto.timezonesString(): String {
    return timezones
        ?.mapNotNull { it.trim().takeIf { s -> s.isNotBlank() } }
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }
}

private fun CountryDto.bordersString(): String {
    return borders
        ?.mapNotNull { it.trim().takeIf { s -> s.isNotBlank() } }
        ?.distinct()
        ?.sorted()
        ?.joinToString(", ")
        .orEmpty()
        .ifBlank { "—" }
}

private fun quizAreasString(
    region: String,
    subregion: String
): String {
    return buildQuizAreas(region = region, subregion = subregion)
        .joinToString(separator = ",") { it.name }
}

fun buildQuizAreas(
    region: String,
    subregion: String
): Set<QuizArea> {
    val result = mutableSetOf(QuizArea.WORLD)

    when (region) {
        "Europe" -> {
            result += QuizArea.EUROPE
            result += QuizArea.EURASIA
        }

        "Asia" -> {
            result += QuizArea.ASIA
            result += QuizArea.EURASIA
        }

        "Africa" -> {
            result += QuizArea.AFRICA
        }

        "Oceania" -> {
            result += QuizArea.OCEANIA
        }

        "Americas" -> {
            result += QuizArea.AMERICAS

            if (subregion == "South America") {
                result += QuizArea.SOUTH_AMERICA
            } else {
                result += QuizArea.NORTH_AMERICA
            }
        }
    }

    return result
}