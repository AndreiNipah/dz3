package com.example.dz3.data

import com.example.dz3.fake.FakeCountriesApi
import com.example.dz3.fake.FakeFavouriteDao
import com.example.dz3.fake.FakeHistoryDao
import com.example.dz3.model.Country
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryRepositoryTest {

    private val repository = CountriesRepository(
        api = FakeCountriesApi(),
        favouriteDao = FakeFavouriteDao(),
        historyDao = FakeHistoryDao()
    )

    private val country = Country(
        code = "USA",
        name = "United States",
        capital = "Washington, D.C.",
        region = "Americas",
        flagUrl = "url",
        population = 100L
    )

    @Test
    fun addToHistory_twice_createsTwoEntries() = runTest {
        repository.addToHistory(country)
        repository.addToHistory(country)

        val history = repository.observeHistory().first()

        assertEquals(2, history.size)
        assertEquals("USA", history[0].country.code)
        assertEquals("USA", history[1].country.code)
    }

    @Test
    fun history_isSortedByViewedAtDesc() = runTest {
        repository.addToHistory(country)
        val firstSnapshot = repository.observeHistory().first()
        val firstViewedAt = firstSnapshot.first().viewedAt

        repository.addToHistory(country)
        val secondSnapshot = repository.observeHistory().first()

        assertEquals(2, secondSnapshot.size)
        assertTrue(secondSnapshot.first().viewedAt >= firstViewedAt)
    }
}