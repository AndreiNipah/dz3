package com.example.dz3.viewmodel

import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.ui.viewmodel.SearchUiState
import com.example.dz3.ui.viewmodel.SearchViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelCancellationTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = mockk<CountriesRepository>(relaxed = true)

    private val usa = Country(
        code = "USA",
        name = "United States",
        capital = "Washington",
        region = "Americas",
        flagUrl = "url_usa",
        population = 100L
    )

    private val uganda = Country(
        code = "UGA",
        name = "Uganda",
        capital = "Kampala",
        region = "Africa",
        flagUrl = "url_uga",
        population = 200L
    )

    @Test
    fun outdatedRequest_doesNotOverrideNewResult() = runTest {
        every { repository.observeFavourites() } returns flowOf(emptyList())
        coEvery { repository.getAll() } returns emptyList()

        coEvery { repository.searchByName("Uni") } coAnswers {
            delay(1_000)
            listOf(usa)
        }

        coEvery { repository.searchByName("Ug") } coAnswers {
            delay(100)
            listOf(uganda)
        }

        val vm = SearchViewModel(repository)
        advanceUntilIdle()

        vm.updateSearchQuery("Uni")
        advanceTimeBy(500)

        vm.updateSearchQuery("Ug")
        advanceTimeBy(500)
        advanceUntilIdle()

        val state = vm.uiState.search
        assertTrue(state is SearchUiState.Success)

        val items = (state as SearchUiState.Success).items
        assertEquals(1, items.size)
        assertEquals("UGA", items.first().code)
    }
}