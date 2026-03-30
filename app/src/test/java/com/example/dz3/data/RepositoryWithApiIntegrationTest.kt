package com.example.dz3.data

import com.example.dz3.data.remote.CountryDto
import com.example.dz3.data.remote.FlagsDto
import com.example.dz3.data.remote.NameDto
import com.example.dz3.fake.FakeCountriesApi
import com.example.dz3.fake.FakeFavouriteDao
import com.example.dz3.fake.FakeHistoryDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryWithApiIntegrationTest {

    private val api = FakeCountriesApi()
    private val favouriteDao = FakeFavouriteDao()
    private val historyDao = FakeHistoryDao()

    private val repository = CountriesRepository(
        api = api,
        favouriteDao = favouriteDao,
        historyDao = historyDao
    )

    @Test
    fun getAll_returnsMappedCountries() = runTest {
        api.countries = listOf(
            CountryDto(
                cca3 = "USA",
                name = NameDto(common = "United States"),
                capital = listOf("Washington"),
                region = "Americas",
                population = 100L,
                flags = FlagsDto(png = "url1")
            ),
            CountryDto(
                cca3 = "FRA",
                name = NameDto(common = "France"),
                capital = listOf("Paris"),
                region = "Europe",
                population = 200L,
                flags = FlagsDto(png = "url2")
            )
        )

        val result = repository.getAll()

        assertEquals(2, result.size)
        assertEquals("France", result[0].name)
        assertEquals("United States", result[1].name)
    }

    @Test
    fun getDetails_returnsMappedDetails() = runTest {
        api.countries = listOf(
            CountryDto(
                cca3 = "USA",
                name = NameDto(
                    common = "United States",
                    official = "United States of America"
                ),
                capital = listOf("Washington"),
                region = "Americas",
                subregion = "North America",
                population = 100L,
                flags = FlagsDto(png = "url1"),
                languages = mapOf("eng" to "English"),
                timezones = listOf("UTC-05:00"),
                borders = listOf("CAN", "MEX")
            )
        )

        val result = repository.getDetails("USA")

        assertEquals("USA", result.code)
        assertEquals("United States", result.name)
        assertEquals("United States of America", result.officialName)
        assertEquals("Washington", result.capital)
        assertEquals("English", result.languages)
        assertTrue(result.borders.contains("CAN"))
    }
}