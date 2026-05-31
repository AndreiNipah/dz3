package com.example.dz3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.dz3.data.local.history.HistoryCountryEntity

data class HistoryScreenState(
    val history: List<HistoryCountryEntity> = emptyList(),
    val favourites: List<Country> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    var uiState by mutableStateOf(HistoryScreenState())
        private set

    init {
        observeHistory()
        observeFavourites()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.observeHistory().collect { history ->
                uiState = uiState.copy(history = history)
            }
        }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.observeFavourites().collect { favourites ->
                uiState = uiState.copy(favourites = favourites)
            }
        }
    }

    fun toggleFavourite(country: Country) {
        viewModelScope.launch {
            val exists = uiState.favourites.any { it.code == country.code }
            if (exists) {
                repository.removeFavourite(country.code)
            } else {
                repository.addFavourite(country)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}