package com.example.dz3.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.dz3.ui.screen.SearchScreen
import com.example.dz3.ui.viewmodel.SearchScreenState
import com.example.dz3.ui.viewmodel.SearchUiState
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyState_showsEnterMessage() {
        composeRule.setContent {
            SearchScreen(
                uiState = SearchScreenState(
                    query = "",
                    search = SearchUiState.Empty
                ),
                onSearchChange = {},
                onRefresh = {},
                onOpenDetails = {},
                onOpenFavourites = {},
                onOpenHistory = {},
                onToggleFavourite = {}
            )
        }

        composeRule.onNodeWithText("Enter a country name").assertIsDisplayed()
    }
}