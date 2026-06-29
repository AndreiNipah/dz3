package com.example.dz3.fake

import com.example.dz3.data.local.HistoryCountryDao
import com.example.dz3.data.local.HistoryCountryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeHistoryDao : HistoryCountryDao {

    private val list = mutableListOf<HistoryCountryEntity>()
    private val flow = MutableStateFlow<List<HistoryCountryEntity>>(emptyList())

    override fun observeAll(): Flow<List<HistoryCountryEntity>> = flow

    override suspend fun insert(country: HistoryCountryEntity) {
        list.add(country)
        list.sortByDescending { it.viewedAt }
        flow.value = list.toList()
    }

    override suspend fun clearAll() {
        list.clear()
        flow.value = emptyList()
    }
}