package com.example.dz3.data

import app.cash.turbine.test
import com.example.dz3.fake.FakeCountriesApi
import com.example.dz3.fake.FakeFavouriteDao
import com.example.dz3.fake.FakeHistoryDao
import com.example.dz3.model.Country
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FavouritesFlowTest {

    @Test
    fun observeFavourites_emits_full_sequence() = runTest {
        val repository = CountriesRepository(
            api = FakeCountriesApi(),
            favouriteDao = FakeFavouriteDao(),
            historyDao = FakeHistoryDao()
        )

        val usa = Country(
            code = "USA",
            name = "United States",
            capital = "Washington",
            region = "Americas",
            flagUrl = "url",
            population = 100L
        )

        repository.observeFavourites().test {
            assertEquals(emptyList<Country>(), awaitItem())

            repository.addFavourite(usa)
            val afterInsert = awaitItem()
            assertEquals(1, afterInsert.size)
            assertEquals("USA", afterInsert.first().code)

            repository.removeFavourite("USA")
            val afterDelete = awaitItem()
            assertEquals(0, afterDelete.size)
        }
    }
}