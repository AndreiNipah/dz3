package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.CountriesRepository
import com.example.dz3.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Empty : SearchUiState
    data class Success(val items: List<Country>) : SearchUiState
}

data class SearchScreenState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val search: SearchUiState = SearchUiState.Empty,
    val favourites: List<Country> = emptyList()
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_MS = 450L
        private const val MIN_QUERY_LEN = 2
    }

    private val queryFlow = MutableStateFlow("")
    private val filterFlow = MutableStateFlow(SearchFilter.ALL)
    private val refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val favouritesFlow = repository.observeFavourites()

    private val searchResultFlow = combine(
        queryFlow
            .debounce(DEBOUNCE_MS)
            .map { it.trim() }
            .distinctUntilChanged(),
        refreshFlow.onStart { emit(Unit) }
    ) { query, _ ->
        query
    }.flatMapLatest { query ->
        flow<SearchUiState> {
            if (query.isBlank()) {
                emit(SearchUiState.Empty)
                return@flow
            }

            if (query.length < MIN_QUERY_LEN) {
                emit(SearchUiState.Empty)
                return@flow
            }

            emit(SearchUiState.Loading)

            val items = repository.searchByName(query)

            emit(
                if (items.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Success(items)
                }
            )
        }.catch { ex ->
            emit(SearchUiState.Error(ex.message ?: "Failed to load countries"))
        }
    }

    val uiState: StateFlow<SearchScreenState> = combine(
        queryFlow,
        filterFlow,
        favouritesFlow,
        searchResultFlow
    ) { query, filter, favourites, searchState ->
        val filteredSearchState = when (searchState) {
            is SearchUiState.Success -> {
                val filteredItems = applyFilter(searchState.items, filter)

                if (filteredItems.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Success(filteredItems)
                }
            }

            else -> searchState
        }

        SearchScreenState(
            query = query,
            selectedFilter = filter,
            search = filteredSearchState,
            favourites = favourites
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchScreenState()
    )

    fun updateSearchQuery(query: String) {
        queryFlow.value = query
    }

    fun updateFilter(filter: SearchFilter) {
        filterFlow.value = filter
    }

    fun refresh() {
        refreshFlow.tryEmit(Unit)
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

    private fun applyFilter(
        items: List<Country>,
        filter: SearchFilter
    ): List<Country> {
        return when (filter) {
            SearchFilter.ALL -> items
            SearchFilter.AFRICA -> items.filter { it.region.equals("Africa", ignoreCase = true) }
            SearchFilter.AMERICAS -> items.filter { it.region.equals("Americas", ignoreCase = true) }
            SearchFilter.ASIA -> items.filter { it.region.equals("Asia", ignoreCase = true) }
            SearchFilter.EUROPE -> items.filter { it.region.equals("Europe", ignoreCase = true) }
            SearchFilter.OCEANIA -> items.filter { it.region.equals("Oceania", ignoreCase = true) }
        }
    }
}