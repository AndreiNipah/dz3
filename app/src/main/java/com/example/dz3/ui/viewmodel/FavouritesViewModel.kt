package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavouritesScreenState(
    val favourites: List<Country> = emptyList()
)

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    val uiState: StateFlow<FavouritesScreenState> =
        repository.observeFavourites()
            .map { favourites ->
                FavouritesScreenState(favourites = favourites)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FavouritesScreenState()
            )

    fun removeFavourite(country: Country) {
        viewModelScope.launch {
            repository.removeFavourite(country.code)
        }
    }
}