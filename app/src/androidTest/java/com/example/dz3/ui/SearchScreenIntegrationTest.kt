package com.example.dz3.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dz3.CountriesRoute
import com.example.dz3.model.Country
import com.example.dz3.model.CountryDetails
import com.example.dz3.ui.screen.CountryDetailsScreen
import com.example.dz3.ui.screen.SearchScreen
import com.example.dz3.ui.viewmodel.CountryDetailsScreenState
import com.example.dz3.ui.viewmodel.DetailsUiState
import com.example.dz3.ui.viewmodel.SearchScreenState
import com.example.dz3.ui.viewmodel.SearchUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val usaCountry = Country(
        code = "USA",
        name = "United States",
        capital = "Washington",
        region = "Americas",
        flagUrl = "",
        population = 100L
    )

    private val usaDetails = CountryDetails(
        code = "USA",
        name = "United States",
        officialName = "United States of America",
        capital = "Washington",
        region = "Americas",
        subregion = "North America",
        flagUrl = "",
        population = 100L,
        languages = "English",
        currencies = "United States dollar ($, USD)",
        timezones = "UTC-05:00",
        borders = "CAN, MEX",
        googleMapsUrl = "https://maps.google.com"
    )

    @Test
    fun clickOnCountry_navigatesToDetailsScreenForCorrectCode() {
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = CountriesRoute.Search.route
            ) {
                composable(CountriesRoute.Search.route) {
                    SearchScreen(
                        uiState = SearchScreenState(
                            search = SearchUiState.Success(listOf(usaCountry))
                        ),
                        onSearchChange = {},
                        onRefresh = {},
                        onOpenDetails = { country ->
                            navController.navigate(
                                CountriesRoute.Detail.createRoute(country.code)
                            )
                        },
                        onOpenFavourites = {},
                        onOpenHistory = {},
                        onToggleFavourite = {}
                    )
                }

                composable(
                    route = CountriesRoute.Detail.route,
                    arguments = listOf(
                        navArgument(CountriesRoute.Detail.ARG_CODE) {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val code = backStackEntry.arguments
                        ?.getString(CountriesRoute.Detail.ARG_CODE)
                        .orEmpty()

                    assertEquals("USA", code)

                    CountryDetailsScreen(
                        code = code,
                        uiState = CountryDetailsScreenState(
                            details = DetailsUiState.Success(usaDetails)
                        ),
                        onLoad = {},
                        onBack = {},
                        onToggleFavourite = {}
                    )
                }
            }
        }

        composeRule.onNodeWithText("United States").assertIsDisplayed()

        composeRule
            .onNodeWithTag("country_card_USA")
            .performClick()

        composeRule.onNodeWithText("United States of America").assertIsDisplayed()
        composeRule.onNodeWithText("Washington").assertIsDisplayed()
        composeRule.onNodeWithText("North America").assertIsDisplayed()
        composeRule.onNodeWithText("English").assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetryButton_andClickCallsRefresh() {
        var retryClicks = 0

        composeRule.setContent {
            SearchScreen(
                uiState = SearchScreenState(
                    query = "usa",
                    search = SearchUiState.Error("Failed to load countries")
                ),
                onSearchChange = {},
                onRefresh = { retryClicks++ },
                onOpenDetails = {},
                onOpenFavourites = {},
                onOpenHistory = {},
                onToggleFavourite = {}
            )
        }

        composeRule.onNodeWithText("Failed to load countries").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertEquals(1, retryClicks)
    }
}