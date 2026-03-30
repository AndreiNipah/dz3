package com.example.dz3.fake

import com.example.dz3.data.local.FavouriteCountryDao
import com.example.dz3.data.local.FavouriteCountryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavouriteDao : FavouriteCountryDao {

    private val list = mutableListOf<FavouriteCountryEntity>()
    private val flow = MutableStateFlow<List<FavouriteCountryEntity>>(emptyList())

    override fun observeAll(): Flow<List<FavouriteCountryEntity>> = flow

    override suspend fun getAll(): List<FavouriteCountryEntity> = list.toList()

    override suspend fun insert(country: FavouriteCountryEntity) {
        list.removeAll { it.code == country.code }
        list.add(country)
        flow.value = list.toList()
    }

    override suspend fun deleteByCode(code: String) {
        list.removeAll { it.code == code }
        flow.value = list.toList()
    }
}