package com.example.dz3.data

import com.example.dz3.fake.FakeCountriesApi
import com.example.dz3.fake.FakeFavouriteDao
import com.example.dz3.fake.FakeHistoryDao
import com.example.dz3.model.Country
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.flow.first

class CountriesRepositoryTest {

    private val api = FakeCountriesApi()
    private val favouriteDao = FakeFavouriteDao()
    private val historyDao = FakeHistoryDao()

    private val repository = CountriesRepository(
        api = api,
        favouriteDao = favouriteDao,
        historyDao = historyDao
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
    fun addFavourite_addsCountry() = runTest {
        repository.addFavourite(country)

        val result = favouriteDao.observeAll().first()

        assertEquals(1, result.size)
        assertEquals("USA", result.first().code)
    }

    @Test
    fun addFavourite_twice_doesNotCreateDuplicate() = runTest {
        repository.addFavourite(country)
        repository.addFavourite(country)

        val result = favouriteDao.observeAll().first()

        assertEquals(1, result.size)
    }

    @Test
    fun clearHistory_removesAllItems() = runTest {
        repository.addToHistory(country)
        repository.clearHistory()

        val result = historyDao.observeAll().first()

        assertEquals(0, result.size)
    }
}