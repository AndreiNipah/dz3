package com.example.dz3.fake

import com.example.dz3.data.local.FavouriteCountryDao
import com.example.dz3.data.local.FavouriteCountryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavouriteDao : FavouriteCountryDao {

    private val flow = MutableStateFlow<List<FavouriteCountryEntity>>(emptyList())

    override fun observeAll(): Flow<List<FavouriteCountryEntity>> {
        return flow
    }

    override suspend fun insert(country: FavouriteCountryEntity) {
        val current = flow.value
            .filterNot { it.code == country.code }

        flow.value = (current + country).sortedBy { it.name }
    }

    override suspend fun deleteByCode(code: String) {
        flow.value = flow.value
            .filterNot { it.code == code }
            .sortedBy { it.name }
    }
}