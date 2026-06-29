package com.example.dz3.viewmodel

import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.ui.viewmodel.SearchUiState
import com.example.dz3.ui.viewmodel.SearchViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CountriesRepository

    private val favouritesFlow = MutableStateFlow<List<Country>>(emptyList())

    private val usaCountry = Country(
        code = "USA",
        name = "United States",
        capital = "Washington",
        region = "Americas",
        flagUrl = "",
        population = 100L
    )

    @Before
    fun setup() {
        repository = mockk()

        every { repository.observeFavourites() } returns favouritesFlow

        coEvery { repository.getAll() } returns emptyList()
    }

    @Test
    fun search_success() = runTest {
        coEvery { repository.searchByName("USA") } returns listOf(usaCountry)

        val vm = SearchViewModel(repository)
        advanceUntilIdle()

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = vm.uiState.search

        assertTrue(state is SearchUiState.Success)

        val success = state as SearchUiState.Success
        assertEquals(1, success.items.size)
        assertEquals("USA", success.items.first().code)
        assertEquals("United States", success.items.first().name)
        assertEquals("Washington", success.items.first().capital)
        assertEquals("Americas", success.items.first().region)

        coVerify(exactly = 1) {
            repository.searchByName("USA")
        }
    }

    @Test
    fun search_error() = runTest {
        coEvery { repository.searchByName("USA") } throws RuntimeException("error")

        val vm = SearchViewModel(repository)
        advanceUntilIdle()

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = vm.uiState.search

        assertTrue(state is SearchUiState.Error)

        val error = state as SearchUiState.Error
        assertEquals("error", error.message)

        coVerify(exactly = 1) {
            repository.searchByName("USA")
        }
    }

    @Test
    fun search_empty() = runTest {
        coEvery { repository.searchByName("USA") } returns emptyList()

        val vm = SearchViewModel(repository)
        advanceUntilIdle()

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = vm.uiState.search

        assertTrue(state is SearchUiState.Empty)

        coVerify(exactly = 1) {
            repository.searchByName("USA")
        }
    }

    @Test
    fun retry_after_error() = runTest {
        coEvery { repository.searchByName("USA") } throws RuntimeException("error")

        val vm = SearchViewModel(repository)
        advanceUntilIdle()

        vm.updateSearchQuery("USA")
        advanceTimeBy(500)
        advanceUntilIdle()

        val errorState = vm.uiState.search
        assertTrue(errorState is SearchUiState.Error)
        assertEquals("error", (errorState as SearchUiState.Error).message)

        coEvery { repository.searchByName("USA") } returns listOf(usaCountry)

        vm.refresh()
        advanceUntilIdle()

        val successState = vm.uiState.search

        assertTrue(successState is SearchUiState.Success)

        val success = successState as SearchUiState.Success
        assertEquals(1, success.items.size)
        assertEquals("USA", success.items.first().code)
        assertEquals("United States", success.items.first().name)

        coVerify(exactly = 2) {
            repository.searchByName("USA")
        }
    }
}