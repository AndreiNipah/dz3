package com.example.dz3.viewmodel

import com.example.dz3.MainDispatcherRule
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.ui.viewmodel.SearchUiState
import com.example.dz3.ui.viewmodel.SearchViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var repository: CountriesRepository
    private lateinit var vm: SearchViewModel

    private val usaCountry = Country(
        code = "USA",
        name = "United States",
        capital = "Washington",
        region = "Americas",
        flagUrl = "url",
        population = 100L
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)

        every { repository.observeFavourites() } returns flowOf(emptyList())
        coEvery { repository.getAll() } returns emptyList()

        vm = SearchViewModel(repository)
        advanceInit()
    }

    private fun advanceInit() {
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun search_success() = runTest {
        coEvery { repository.searchByName("USA") } returns listOf(usaCountry)

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertTrue(vm.uiState.search is SearchUiState.Success)
    }

    @Test
    fun search_error() = runTest {
        coEvery { repository.searchByName("USA") } throws RuntimeException("error")

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertTrue(vm.uiState.search is SearchUiState.Error)
    }

    @Test
    fun search_empty() = runTest {
        coEvery { repository.searchByName("ZZZ") } returns emptyList()

        vm.updateSearchQuery("ZZZ")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertTrue(vm.uiState.search is SearchUiState.Empty)
    }

    @Test
    fun retry_after_error() = runTest {
        coEvery { repository.searchByName("USA") } throws RuntimeException("error")

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()
        assertTrue(vm.uiState.search is SearchUiState.Error)

        coEvery { repository.searchByName("USA") } returns listOf(usaCountry)

        vm.refresh()
        advanceUntilIdle()

        assertTrue(vm.uiState.search is SearchUiState.Success)
    }
}