package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CountryDetailsViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    private val codeFlow = MutableStateFlow("")
    private val retryFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val detailsFlow = combine(
        codeFlow,
        retryFlow.onStart { emit(Unit) }
    ) { code, _ ->
        code
    }.flatMapLatest { code ->
        flow<DetailsUiState> {
            if (code.isBlank()) {
                emit(DetailsUiState.Idle)
                return@flow
            }

            emit(DetailsUiState.Loading)

            val details = repository.getDetails(code)

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

            emit(DetailsUiState.Success(details))
        }.catch { ex ->
            emit(DetailsUiState.Error(ex.message ?: "Failed to load details"))
        }
    }

    val uiState: StateFlow<CountryDetailsScreenState> =
        combine(
            detailsFlow,
            repository.observeFavourites()
        ) { details, favourites ->
            CountryDetailsScreenState(
                details = details,
                favourites = favourites
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CountryDetailsScreenState()
        )

    fun loadDetails(code: String) {
        codeFlow.value = code
    }

    fun retry() {
        retryFlow.tryEmit(Unit)
    }

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

    fun clearSelection() {
        codeFlow.value = ""
    }
}