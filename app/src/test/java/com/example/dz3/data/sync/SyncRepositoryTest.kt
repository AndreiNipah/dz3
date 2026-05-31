package com.example.dz3.data.sync

import com.example.dz3.data.CountriesRepository
import com.example.dz3.data.local.cache.CacheMetadataDao
import com.example.dz3.data.local.cache.CacheMetadataEntity
import com.example.dz3.data.settings.SettingsRepository
import com.example.dz3.model.AppSettings
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncRepositoryTest {

    @Test
    fun syncCountriesIfNeeded_skips_whenCacheIsFresh() = runTest {
        val countriesRepository = mockk<CountriesRepository>(relaxed = true)
        val settingsRepository = mockk<SettingsRepository>()
        val networkStatusChecker = mockk<NetworkStatusChecker>()

        every { settingsRepository.settings } returns flowOf(
            AppSettings(cacheTtlHours = 24)
        )

        val now = System.currentTimeMillis()
        val cacheDao = FakeCacheMetadataDao(
            initial = CacheMetadataEntity(
                key = SyncRepository.KEY_COUNTRIES,
                updatedAt = now
            )
        )

        val repository = SyncRepository(
            countriesRepository = countriesRepository,
            cacheMetadataDao = cacheDao,
            settingsRepository = settingsRepository,
            networkStatusChecker = networkStatusChecker
        )

        val result = repository.syncCountriesIfNeeded(force = false)

        assertTrue(result is SyncResult.SkippedFreshCache)
        coVerify(exactly = 0) {
            countriesRepository.refreshCountries()
        }
    }

    @Test
    fun syncCountriesIfNeeded_refreshes_whenCacheIsExpired() = runTest {
        val countriesRepository = mockk<CountriesRepository>(relaxed = true)
        val settingsRepository = mockk<SettingsRepository>()
        val networkStatusChecker = mockk<NetworkStatusChecker>()

        every { settingsRepository.settings } returns flowOf(
            AppSettings(cacheTtlHours = 1)
        )

        coEvery { countriesRepository.refreshCountries() } returns Unit

        val oldTimestamp = System.currentTimeMillis() - 2 * 60L * 60L * 1000L

        val cacheDao = FakeCacheMetadataDao(
            initial = CacheMetadataEntity(
                key = SyncRepository.KEY_COUNTRIES,
                updatedAt = oldTimestamp
            )
        )

        val repository = SyncRepository(
            countriesRepository = countriesRepository,
            cacheMetadataDao = cacheDao,
            settingsRepository = settingsRepository,
            networkStatusChecker = networkStatusChecker
        )

        val result = repository.syncCountriesIfNeeded(force = false)

        assertTrue(result is SyncResult.Success)
        coVerify(exactly = 1) {
            countriesRepository.refreshCountries()
        }
    }

    @Test
    fun syncCountriesIfNeeded_skips_whenWifiRequiredButNotConnected() = runTest {
        val countriesRepository = mockk<CountriesRepository>(relaxed = true)
        val settingsRepository = mockk<SettingsRepository>()
        val networkStatusChecker = mockk<NetworkStatusChecker>()

        every { settingsRepository.settings } returns flowOf(
            AppSettings(
                cacheTtlHours = 1,
                updateOnlyWifi = true
            )
        )

        every { networkStatusChecker.isWifiConnected() } returns false

        val cacheDao = FakeCacheMetadataDao(initial = null)

        val repository = SyncRepository(
            countriesRepository = countriesRepository,
            cacheMetadataDao = cacheDao,
            settingsRepository = settingsRepository,
            networkStatusChecker = networkStatusChecker
        )

        val result = repository.syncCountriesIfNeeded(force = false)

        assertTrue(result is SyncResult.SkippedWifiRequired)
        coVerify(exactly = 0) {
            countriesRepository.refreshCountries()
        }
    }
}

private class FakeCacheMetadataDao(
    initial: CacheMetadataEntity?
) : CacheMetadataDao {

    private var metadata: CacheMetadataEntity? = initial

    override suspend fun getByKey(key: String): CacheMetadataEntity? {
        return metadata?.takeIf { it.key == key }
    }

    override suspend fun upsert(metadata: CacheMetadataEntity) {
        this.metadata = metadata
    }
}