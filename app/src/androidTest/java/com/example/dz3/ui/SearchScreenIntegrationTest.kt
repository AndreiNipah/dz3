package com.example.dz3.ui


import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.dz3.model.Country
import com.example.dz3.ui.screen.SearchScreen
import com.example.dz3.ui.viewmodel.SearchScreenState
import com.example.dz3.ui.viewmodel.SearchUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun clickOnCountry_opensCorrectDetails() {
        val country = Country(
            code = "USA",
            name = "United States",
            capital = "Washington",
            region = "Americas",
            flagUrl = "",
            population = 100L
        )

        var clickedCode: String? = null

        composeRule.setContent {
            SearchScreen(
                uiState = SearchScreenState(
                    search = SearchUiState.Success(listOf(country))
                ),
                onSearchChange = {},
                onRefresh = {},
                onOpenDetails = { clickedCode = it.code },
                onOpenFavourites = {},
                onOpenHistory = {},
                onToggleFavourite = {}
            )
        }

        composeRule.onNodeWithText("United States").assertExists()
        composeRule.onNodeWithText("United States").performClick()

        assertEquals("USA", clickedCode)
    }

    @Test
    fun errorState_showsRetryButton() {
        composeRule.setContent {
            SearchScreen(
                uiState = SearchScreenState(
                    query = "usa",
                    search = SearchUiState.Error("Failed to load countries")
                ),
                onSearchChange = {},
                onRefresh = {},
                onOpenDetails = {},
                onOpenFavourites = {},
                onOpenHistory = {},
                onToggleFavourite = {}
            )
        }

        composeRule.onNodeWithText("Retry").assertExists()
    }
}