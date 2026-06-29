package com.example.dz3.viewmodel

import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.ui.viewmodel.CountryDetailsViewModel
import com.example.dz3.ui.viewmodel.DetailsUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CountriesRepository

    private val favouritesFlow = MutableStateFlow<List<Country>>(emptyList())

    private val usaDetails = CountryDetails(
        code = "USA",
        name = "United States",
        officialName = "United States of America",
        capital = "Washington",
        region = "Americas",
        subregion = "North America",
        flagUrl = "",
        population = 100L,
        languages = "English",
        currencies = "United States dollar ($, USD)",
        timezones = "UTC-05:00",
        borders = "CAN, MEX",
        googleMapsUrl = "https://maps.google.com"
    )

    @Before
    fun setup() {
        repository = mockk()

        every { repository.observeFavourites() } returns favouritesFlow

        coEvery { repository.addToHistory(any()) } just Runs
    }

    @Test
    fun load_details_success() = runTest {
        coEvery { repository.getDetails("USA") } returns usaDetails

        val vm = CountryDetailsViewModel(repository)

        vm.loadDetails("USA")
        advanceUntilIdle()

        val state = vm.uiState.details

        assertTrue(state is DetailsUiState.Success)

        val success = state as DetailsUiState.Success
        assertEquals("USA", success.details.code)
        assertEquals("United States", success.details.name)
        assertEquals("United States of America", success.details.officialName)
        assertEquals("Washington", success.details.capital)
        assertEquals("Americas", success.details.region)
        assertEquals("North America", success.details.subregion)
        assertEquals("English", success.details.languages)

        coVerify(exactly = 1) {
            repository.getDetails("USA")
        }

        coVerify(exactly = 1) {
            repository.addToHistory(
                match {
                    it.code == "USA" &&
                            it.name == "United States" &&
                            it.capital == "Washington" &&
                            it.region == "Americas"
                }
            )
        }
    }
}