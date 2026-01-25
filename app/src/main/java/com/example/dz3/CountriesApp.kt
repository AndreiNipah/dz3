package com.example.dz3

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dz3.ui.screen.CountryDetailsScreen
import com.example.dz3.ui.screen.FavouritesScreen
import com.example.dz3.ui.screen.SearchScreen
import com.example.dz3.ui.viewmodel.CountriesViewModel

sealed class CountriesRoute(val route: String) {
    data object Search : CountriesRoute("search")
    data object Favourites : CountriesRoute("favourites")
    data object Detail : CountriesRoute("detail/{code}") {
        const val ARG_CODE = "code"
        fun createRoute(code: String) = "detail/$code"
    }
}

@Composable
fun CountriesApp() {
    val nav = rememberNavController()
    val vm: CountriesViewModel = viewModel()

    NavHost(navController = nav, startDestination = CountriesRoute.Search.route) {

        composable(CountriesRoute.Search.route) {
            SearchScreen(
                uiState = vm.uiState,
                onSearchChange = vm::updateSearchQuery,
                onRefresh = vm::refresh,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onOpenFavourites = { nav.navigate(CountriesRoute.Favourites.route) },
                onToggleFavourite = vm::toggleFavourite
            )
        }

        composable(CountriesRoute.Favourites.route) {
            FavouritesScreen(
                uiState = vm.uiState,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onToggleFavourite = vm::toggleFavourite,
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            route = CountriesRoute.Detail.route,
            arguments = listOf(navArgument(CountriesRoute.Detail.ARG_CODE) { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString(CountriesRoute.Detail.ARG_CODE).orEmpty()

            CountryDetailsScreen(
                code = code,
                uiState = vm.uiState,
                onLoad = { vm.loadDetails(code) },
                onBack = {
                    vm.clearSelection()
                    nav.popBackStack()
                },
                onToggleFavourite = vm::toggleFavourite
            )
        }
    }
}