package com.example.dz3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Empty : SearchUiState
    data class Success(val items: List<Country>) : SearchUiState
}

data class SearchScreenState(
    val query: String = "",
    val search: SearchUiState = SearchUiState.Loading,
    val favourites: List<Country> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_MS = 450L
        private const val MIN_QUERY_LEN = 2
    }

    var uiState by mutableStateOf(SearchScreenState())
        private set

    private var allCache: List<Country> = emptyList()
    private var debounceJob: Job? = null
    private var requestJob: Job? = null

    init {
        observeFavourites()
        loadAll(forceNetwork = true)
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.observeFavourites().collect { favourites ->
                uiState = uiState.copy(favourites = favourites)
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
        if (q.isBlank()) {
            loadAll(forceNetwork = true)
        } else {
            searchInternal(forceQuery = q)
        }
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

    override fun onCleared() {
        super.onCleared()
        debounceJob?.cancel()
        requestJob?.cancel()
    }
}