package com.example.dz3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailsUiState {
    data object Idle : DetailsUiState
    data object Loading : DetailsUiState
    data class Error(val message: String) : DetailsUiState
    data class Success(val details: CountryDetails) : DetailsUiState
}

data class CountryDetailsScreenState(
    val details: DetailsUiState = DetailsUiState.Idle,
    val favourites: List<Country> = emptyList()
)

@HiltViewModel
class CountryDetailsViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    var uiState by mutableStateOf(CountryDetailsScreenState())
        private set

    private var detailsJob: Job? = null

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

    fun loadDetails(code: String) {
        uiState = uiState.copy(details = DetailsUiState.Loading)

        detailsJob?.cancel()
        detailsJob = viewModelScope.launch {
            try {
                val details = repository.getDetails(code)
                uiState = uiState.copy(details = DetailsUiState.Success(details))

                repository.addToHistory(
                    Country(
                        code = details.code,
                        name = details.name,
                        capital = details.capital,
                        region = details.region,
                        flagUrl = details.flagUrl,
                        population = details.population
                    )
                )
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    details = DetailsUiState.Error(ex.message ?: "Failed to load details")
                )
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

    fun clearSelection() {
        detailsJob?.cancel()
        uiState = uiState.copy(details = DetailsUiState.Idle)
    }

    override fun onCleared() {
        super.onCleared()
        detailsJob?.cancel()
    }
}