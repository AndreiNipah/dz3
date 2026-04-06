package com.example.dz3.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dz3.model.Country
import com.example.dz3.ui.viewmodel.HistoryScreenState
import com.example.dz3.ui.widget.CountryCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryScreenState,
    onOpenDetails: (Country) -> Unit,
    onToggleFavourite: (Country) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.history.isNotEmpty()) {
                        IconButton(onClick = onClearHistory) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear history")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("History is empty")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.history, key = { it.id }) { item ->
                    val country = Country(
                        code = item.code,
                        name = item.name,
                        capital = item.capital,
                        region = item.region,
                        flagUrl = item.flagUrl,
                        population = item.population
                    )

                    CountryCard(
                        country = country,
                        isFavourite = uiState.favourites.any { it.code == item.code },
                        onClick = { onOpenDetails(country) },
                        onToggleFavourite = { onToggleFavourite(country) }
                    )
                }
            }
        }
    }
}