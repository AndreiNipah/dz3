package com.example.dz3.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dz3.data.sync.SyncRepository
import com.example.dz3.data.sync.SyncResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CountriesSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (syncRepository.syncCountriesIfNeeded(force = false)) {
            SyncResult.Success -> Result.success()
            SyncResult.SkippedFreshCache -> Result.success()
            SyncResult.SkippedWifiRequired -> Result.success()
            is SyncResult.Error -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "countries_sync_work"
    }
}