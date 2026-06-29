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

data class FavouritesScreenState(
    val favourites: List<Country> = emptyList()
)

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    var uiState by mutableStateOf(FavouritesScreenState())
        private set

    init {
        observeFavourites()
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
            repository.removeFavourite(country.code)
        }
    }
}