package com.example.dz3.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dz3.model.Country
import com.example.dz3.ui.viewmodel.SearchScreenState
import com.example.dz3.ui.viewmodel.SearchUiState
import com.example.dz3.ui.widget.CountryCard
import androidx.compose.material.icons.outlined.History

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchScreenState,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenDetails: (Country) -> Unit,
    onOpenFavourites: () -> Unit,
    onOpenHistory: () -> Unit,
    onToggleFavourite: (Country) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Countries Explorer") },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Outlined.History, contentDescription = "History")
                    }
                    IconButton(onClick = onOpenFavourites) {
                        Icon(Icons.Outlined.Favorite, contentDescription = "Favourites")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Row {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = onSearchChange,
                        label = { Text("Search by name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val refreshEnabled = uiState.search !is SearchUiState.Loading
                    Button(onClick = onRefresh, enabled = refreshEnabled) {
                        Text("Refresh")
                    }
                }
            }

            when (val s = uiState.search) {
                is SearchUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is SearchUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = s.message,
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(onClick = onRefresh) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is SearchUiState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val q = uiState.query.trim()
                        val msg = when {
                            q.isBlank() -> "Enter a country name"
                            q.length < 2 -> "Type at least 2 letters"
                            else -> "Nothing found"
                        }
                        Text(msg)
                    }
                }

                is SearchUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.items, key = { it.code }) { country ->
                            CountryCard(
                                country = country,
                                isFavourite = uiState.favourites.any { it.code == country.code },
                                onClick = { onOpenDetails(country) },
                                onToggleFavourite = { onToggleFavourite(country) }
                            )
                        }
                    }
                }
            }
        }
    }
}