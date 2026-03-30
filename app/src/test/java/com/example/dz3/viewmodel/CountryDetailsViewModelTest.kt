package com.example.dz3.viewmodel

import com.example.dz3.MainDispatcherRule
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.ui.viewmodel.CountryDetailsViewModel
import com.example.dz3.ui.viewmodel.DetailsUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryDetailsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var repository: CountriesRepository
    private lateinit var vm: CountryDetailsViewModel

    private val usaDetails = CountryDetails(
        code = "USA",
        name = "United States",
        officialName = "United States of America",
        capital = "Washington",
        region = "Americas",
        subregion = "North America",
        flagUrl = "url",
        population = 100L,
        languages = "English",
        currencies = "Dollar ($)",
        timezones = "UTC-05:00",
        borders = "CAN, MEX",
        googleMapsUrl = "maps"
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)

        every { repository.observeFavourites() } returns flowOf(emptyList())

        vm = CountryDetailsViewModel(repository)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun load_details_success() = runTest {
        coEvery { repository.getDetails("USA") } returns usaDetails

        vm.loadDetails("USA")
        advanceUntilIdle()

        assertTrue(vm.uiState.details is DetailsUiState.Success)
    }
}