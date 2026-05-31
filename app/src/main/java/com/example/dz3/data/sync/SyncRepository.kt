package com.example.dz3.data.sync

import com.example.dz3.data.CountriesRepository
import com.example.dz3.data.local.cache.CacheMetadataDao
import com.example.dz3.data.local.cache.CacheMetadataEntity
import com.example.dz3.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val cacheMetadataDao: CacheMetadataDao,
    private val settingsRepository: SettingsRepository,
    private val networkStatusChecker: NetworkStatusChecker
) {

    suspend fun syncCountriesIfNeeded(force: Boolean = false): SyncResult {
        val settings = settingsRepository.settings.first()

        if (settings.updateOnlyWifi && !networkStatusChecker.isWifiConnected()) {
            return SyncResult.SkippedWifiRequired
        }

        val now = System.currentTimeMillis()

        val lastSyncAt = cacheMetadataDao.getByKey(KEY_COUNTRIES)?.updatedAt ?: 0L
        val ttlMillis = settings.cacheTtlHours * 60L * 60L * 1000L
        val isExpired = now - lastSyncAt > ttlMillis

        if (!force && !isExpired) {
            return SyncResult.SkippedFreshCache
        }

        return try {
            countriesRepository.refreshCountries()

            cacheMetadataDao.upsert(
                CacheMetadataEntity(
                    key = KEY_COUNTRIES,
                    updatedAt = now
                )
            )

            SyncResult.Success
        } catch (ex: Exception) {
            SyncResult.Error(ex.message ?: "Sync failed")
        }
    }

    companion object {
        const val KEY_COUNTRIES = "countries"
    }
}

sealed interface SyncResult {
    data object Success : SyncResult
    data object SkippedFreshCache : SyncResult
    data object SkippedWifiRequired : SyncResult
    data class Error(val message: String) : SyncResult
}