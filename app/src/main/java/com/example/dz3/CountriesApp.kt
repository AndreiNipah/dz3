package com.example.dz3

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dz3.ui.screen.CountryDetailsScreen
import com.example.dz3.ui.screen.FavouritesScreen
import com.example.dz3.ui.screen.SearchScreen
import com.example.dz3.ui.screen.HistoryScreen
import com.example.dz3.ui.viewmodel.SearchViewModel
import com.example.dz3.ui.viewmodel.CountryDetailsViewModel
import com.example.dz3.ui.viewmodel.HistoryViewModel
import com.example.dz3.ui.viewmodel.FavouritesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

sealed class CountriesRoute(val route: String) {
    data object Search : CountriesRoute("search")
    data object Favourites : CountriesRoute("favourites")

    data object History : CountriesRoute("history")
    data object Detail : CountriesRoute("detail/{code}") {
        const val ARG_CODE = "code"
        fun createRoute(code: String) = "detail/$code"
    }
}

@Composable
fun CountriesApp() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = CountriesRoute.Search.route) {
        composable(CountriesRoute.Search.route) {
            val vm: SearchViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            SearchScreen(
                uiState = uiState,
                onSearchChange = vm::updateSearchQuery,
                onRefresh = vm::refresh,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onOpenFavourites = { nav.navigate(CountriesRoute.Favourites.route) },
                onOpenHistory = { nav.navigate(CountriesRoute.History.route) },
                onToggleFavourite = vm::toggleFavourite,
                onFilterChange = vm::updateFilter
            )
        }

        composable(CountriesRoute.Favourites.route) {
            val vm: FavouritesViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            FavouritesScreen(
                uiState = uiState,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onToggleFavourite = vm::removeFavourite,
                onBack = { nav.popBackStack() }
            )
        }

        composable(CountriesRoute.History.route) {
            val vm: HistoryViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            HistoryScreen(
                uiState = uiState,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onToggleFavourite = vm::toggleFavourite,
                onClearHistory = vm::clearHistory,
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            route = CountriesRoute.Detail.route,
            arguments = listOf(navArgument(CountriesRoute.Detail.ARG_CODE) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val vm: CountryDetailsViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            val code = backStackEntry.arguments
                ?.getString(CountriesRoute.Detail.ARG_CODE)
                .orEmpty()

            CountryDetailsScreen(
                code = code,
                uiState = uiState,
                onLoad = { vm.loadDetails(code) },
                onRetry = vm::retry,
                onBack = {
                    vm.clearSelection()
                    nav.popBackStack()
                },
                onToggleFavourite = vm::toggleFavourite
            )
        }
    }
}