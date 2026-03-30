package com.example.dz3.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dz3.data.local.CountriesDatabase
import com.example.dz3.fake.FakeCountriesApi
import com.example.dz3.model.Country
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryRoomIntegrationTest {

    private lateinit var db: CountriesDatabase
    private lateinit var repository: CountriesRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CountriesDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = CountriesRepository(
            api = FakeCountriesApi(),
            favouriteDao = db.favouriteCountryDao(),
            historyDao = db.historyCountryDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addFavourite_thenObserveFavourites() = runTest {
        val country = Country(
            code = "USA",
            name = "United States",
            capital = "Washington",
            region = "Americas",
            flagUrl = "url",
            population = 100L
        )

        repository.addFavourite(country)

        val result = repository.observeFavourites().first()

        assertEquals(1, result.size)
        assertEquals("USA", result.first().code)
    }
}