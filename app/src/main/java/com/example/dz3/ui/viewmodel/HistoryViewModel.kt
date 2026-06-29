package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.model.HistoryCountry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryScreenState(
    val history: List<HistoryCountry> = emptyList(),
    val favourites: List<Country> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryScreenState> =
        combine(
            repository.observeHistory(),
            repository.observeFavourites()
        ) { history, favourites ->
            HistoryScreenState(
                history = history,
                favourites = favourites
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryScreenState()
        )

    fun toggleFavourite(country: Country) {
        viewModelScope.launch {
            val exists = uiState.value.favourites.any { it.code == country.code }

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