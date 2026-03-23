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
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Empty : SearchUiState
    data class Success(val items: List<Country>) : SearchUiState
}

sealed interface DetailsUiState {
    data object Idle : DetailsUiState
    data object Loading : DetailsUiState
    data class Error(val message: String) : DetailsUiState
    data class Success(val details: CountryDetails) : DetailsUiState
}

data class CountriesUiState(
    val query: String = "",
    val search: SearchUiState = SearchUiState.Loading,
    val details: DetailsUiState = DetailsUiState.Idle,
    val favourites: List<Country> = emptyList(),
    val history: List<Country> = emptyList()
)

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_MS = 450L
        private const val MIN_QUERY_LEN = 2
    }

    var uiState by mutableStateOf(CountriesUiState())
        private set

    private var allCache: List<Country> = emptyList()

    private var debounceJob: Job? = null
    private var requestJob: Job? = null
    private var detailsJob: Job? = null

    init {
        observeFavourites()
        observeHistory()
        loadAll(forceNetwork = true)
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.observeFavourites().collect { favourites ->
                uiState = uiState.copy(favourites = favourites)
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.observeHistory().collect { history ->
                uiState = uiState.copy(history = history)
            }
        }
    }



    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(query = query)

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            searchInternal()
        }
    }

    fun refresh() {
        val q = uiState.query.trim()
        if (q.isBlank()) loadAll(forceNetwork = true) else searchInternal(forceQuery = q)
    }

    private fun searchInternal(forceQuery: String? = null) {
        val q = (forceQuery ?: uiState.query).trim()

        if (q.isBlank()) {
            showAllFromCacheOrLoad()
            return
        }

        if (q.length < MIN_QUERY_LEN) {
            uiState = uiState.copy(search = SearchUiState.Empty)
            return
        }

        uiState = uiState.copy(search = SearchUiState.Loading)

        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            try {
                val results = repository.searchByName(q)
                uiState = uiState.copy(
                    search = if (results.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(results)
                    }
                )
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    search = SearchUiState.Error(ex.message ?: "Error of search")
                )
            }
        }
    }

    private fun showAllFromCacheOrLoad() {
        if (allCache.isNotEmpty()) {
            uiState = uiState.copy(search = SearchUiState.Success(allCache))
        } else {
            loadAll(forceNetwork = true)
        }
    }

    private fun loadAll(forceNetwork: Boolean) {
        if (!forceNetwork && allCache.isNotEmpty()) {
            uiState = uiState.copy(search = SearchUiState.Success(allCache))
            return
        }

        uiState = uiState.copy(search = SearchUiState.Loading)

        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            try {
                val all = repository.getAll()
                allCache = all
                uiState = uiState.copy(
                    search = if (all.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(all)
                    }
                )
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    search = SearchUiState.Error(ex.message ?: "Failed to load countries")
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

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
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

    fun clearSelection() {
        detailsJob?.cancel()
        uiState = uiState.copy(details = DetailsUiState.Idle)
    }

    override fun onCleared() {
        super.onCleared()
        debounceJob?.cancel()
        requestJob?.cancel()
        detailsJob?.cancel()
    }
}
