package com.example.dz3.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dz3.model.Country
import com.example.dz3.ui.viewmodel.CountriesUiState
import com.example.dz3.ui.viewmodel.DetailsUiState
import com.example.dz3.ui.widget.CountryContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailsScreen(
    code: String,
    uiState: CountriesUiState,
    onLoad: () -> Unit,
    onBack: () -> Unit,
    onToggleFavourite: (Country) -> Unit,
) {
    LaunchedEffect(code) { onLoad() }

    val detailsState = uiState.details
    val isFavourite = uiState.favourites.any { it.code == code }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(code) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val d = (detailsState as? DetailsUiState.Success)?.details ?: return@IconButton
                            onToggleFavourite(
                                Country(
                                    code = d.code,
                                    name = d.name,
                                    capital = d.capital,
                                    region = d.region,
                                    flagUrl = d.flagUrl,
                                    population = d.population
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favourite"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (detailsState) {
                DetailsUiState.Idle -> {
                    item { Text("No data") }
                }

                DetailsUiState.Loading -> {
                    item {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading details...")
                    }
                }

                is DetailsUiState.Error -> {
                    item {
                        Text(
                            text = detailsState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is DetailsUiState.Success -> {
                    item {
                        CountryContent(details = detailsState.details)
                    }
                }
            }
        }
    }
}