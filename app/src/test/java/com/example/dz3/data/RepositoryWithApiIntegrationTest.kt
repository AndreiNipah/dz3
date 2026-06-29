package com.example.dz3.data

import com.example.dz3.data.remote.CapitalDto
import com.example.dz3.data.remote.CodesDto
import com.example.dz3.data.remote.CountryDto
import com.example.dz3.data.remote.CurrencyDto
import com.example.dz3.data.remote.FlagDto
import com.example.dz3.data.remote.LanguageDto
import com.example.dz3.data.remote.MapsDto
import com.example.dz3.data.remote.NamesDto
import com.example.dz3.fake.FakeCountriesApi
import com.example.dz3.fake.FakeFavouriteDao
import com.example.dz3.fake.FakeHistoryDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryWithApiIntegrationTest {

    @Test
    fun getAll_mapsAndSortsCountriesFromApi() = runTest {
        val api = FakeCountriesApi(
            countries = listOf(
                CountryDto(
                    codes = CodesDto(alpha3 = "UGA"),
                    names = NamesDto(common = "Uganda"),
                    capitals = listOf(CapitalDto(name = "Kampala")),
                    region = "Africa",
                    flag = FlagDto(urlPng = "uganda.png"),
                    population = 200L
                ),
                CountryDto(
                    codes = CodesDto(alpha3 = "USA"),
                    names = NamesDto(common = "United States"),
                    capitals = listOf(CapitalDto(name = "Washington")),
                    region = "Americas",
                    flag = FlagDto(urlPng = "usa.png"),
                    population = 100L
                )
            )
        )

        val repository = CountriesRepository(
            api = api,
            favouriteDao = FakeFavouriteDao(),
            historyDao = FakeHistoryDao()
        )

        val result = repository.getAll()

        assertEquals(2, result.size)

        assertEquals("UGA", result[0].code)
        assertEquals("Uganda", result[0].name)
        assertEquals("Kampala", result[0].capital)
        assertEquals("Africa", result[0].region)

        assertEquals("USA", result[1].code)
        assertEquals("United States", result[1].name)
        assertEquals("Washington", result[1].capital)
        assertEquals("Americas", result[1].region)
    }

    @Test
    fun getDetails_mapsDetailsFromApi() = runTest {
        val api = FakeCountriesApi(
            details = mapOf(
                "USA" to CountryDto(
                    codes = CodesDto(alpha3 = "USA"),
                    names = NamesDto(
                        common = "United States",
                        official = "United States of America"
                    ),
                    capitals = listOf(CapitalDto(name = "Washington")),
                    region = "Americas",
                    subregion = "North America",
                    flag = FlagDto(urlPng = "usa.png"),
                    population = 100L,
                    languages = listOf(
                        LanguageDto(name = "English")
                    ),
                    currencies = listOf(
                        CurrencyDto(
                            code = "USD",
                            name = "United States dollar",
                            symbol = "$"
                        )
                    ),
                    timezones = listOf("UTC-05:00"),
                    borders = listOf("CAN", "MEX"),
                    maps = MapsDto(
                        googleMaps = "https://maps.google.com"
                    )
                )
            )
        )

        val repository = CountriesRepository(
            api = api,
            favouriteDao = FakeFavouriteDao(),
            historyDao = FakeHistoryDao()
        )

        val result = repository.getDetails("USA")

        assertEquals("USA", result.code)
        assertEquals("United States", result.name)
        assertEquals("United States of America", result.officialName)
        assertEquals("Washington", result.capital)
        assertEquals("Americas", result.region)
        assertEquals("North America", result.subregion)
        assertEquals("English", result.languages)
        assertEquals("United States dollar ($, USD)", result.currencies)
        assertEquals("UTC-05:00", result.timezones)
        assertEquals("CAN, MEX", result.borders)
        assertEquals("https://maps.google.com", result.googleMapsUrl)
    }
}