package com.example.dz3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dz3.ui.screen.country.CountryDetailsScreen
import com.example.dz3.ui.screen.country.FavouritesScreen
import com.example.dz3.ui.screen.country.HistoryScreen
import com.example.dz3.ui.screen.country.SearchScreen
import com.example.dz3.ui.viewmodel.CountryDetailsViewModel
import com.example.dz3.ui.viewmodel.HistoryViewModel
import com.example.dz3.ui.viewmodel.SearchViewModel
import com.example.dz3.ui.screen.quiz.QuizSetupScreen
import com.example.dz3.ui.viewmodel.QuizSetupViewModel
import com.example.dz3.ui.screen.quiz.QuizResultScreen
import com.example.dz3.ui.screen.quiz.QuizScreen
import com.example.dz3.ui.viewmodel.QuizUiState
import com.example.dz3.ui.viewmodel.QuizViewModel
import com.example.dz3.ui.screen.quiz.QuizHistoryScreen
import com.example.dz3.ui.viewmodel.QuizHistoryViewModel
import com.example.dz3.ui.screen.settings.SettingsScreen
import com.example.dz3.ui.viewmodel.SettingsViewModel

sealed class CountriesRoute(val route: String) {
    data object Search : CountriesRoute("search")
    data object Favourites : CountriesRoute("favourites")
    data object History : CountriesRoute("history")
    data object QuizSetup : CountriesRoute("quiz_setup")
    data object Quiz : CountriesRoute("quiz")
    data object QuizResult : CountriesRoute("quiz_result")
    data object QuizHistory : CountriesRoute("quiz_history")
    data object Settings : CountriesRoute("settings")
    data object Detail : CountriesRoute("detail/{code}") {
        const val ARG_CODE = "code"
        fun createRoute(code: String) = "detail/$code"
    }
}

@Composable
fun CountriesApp() {
    val nav = rememberNavController()
    val quizVm: QuizViewModel = hiltViewModel()

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
                onOpenQuizHistory = { nav.navigate(CountriesRoute.QuizHistory.route) },
                onOpenSettings = { nav.navigate(CountriesRoute.Settings.route) },
                onOpenFavourites = { nav.navigate(CountriesRoute.Favourites.route) },
                onOpenHistory = { nav.navigate(CountriesRoute.History.route) },
                onOpenQuiz = { nav.navigate(CountriesRoute.QuizSetup.route) },
                onToggleFavourite = vm::toggleFavourite,
                onFilterChange = vm::updateFilter
            )
        }

        composable(CountriesRoute.Favourites.route) {
            val vm: SearchViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            FavouritesScreen(
                uiState = uiState,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onToggleFavourite = vm::toggleFavourite,
                onBack = { nav.popBackStack() }
            )
        }

        composable(CountriesRoute.History.route) {
            val vm: HistoryViewModel = hiltViewModel()

            HistoryScreen(
                uiState = vm.uiState,
                onOpenDetails = { country ->
                    nav.navigate(CountriesRoute.Detail.createRoute(country.code))
                },
                onToggleFavourite = vm::toggleFavourite,
                onClearHistory = vm::clearHistory,
                onBack = { nav.popBackStack() }
            )
        }

        composable(CountriesRoute.QuizSetup.route) {
            val vm: QuizSetupViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            QuizSetupScreen(
                uiState = uiState,
                onBack = { nav.popBackStack() },
                onAreaChange = vm::selectArea,
                onModeChange = vm::selectMode,
                onQuestionCountChange = vm::selectQuestionCount,
                onPrepareQuiz = vm::prepareQuiz,
                onStartQuiz = {
                    quizVm.startQuiz(
                        area = uiState.selectedArea,
                        questions = uiState.generatedQuestions
                    )
                    nav.navigate(CountriesRoute.Quiz.route)
                }
            )
        }

        composable(CountriesRoute.Quiz.route) {
            val quizState by quizVm.uiState.collectAsState()

            QuizScreen(
                uiState = quizState,
                onBack = { nav.popBackStack() },
                onSelectAnswer = quizVm::selectAnswer,
                onCheckAnswer = quizVm::checkAnswer,
                onNextQuestion = {
                    quizVm.nextQuestion()

                    if (quizVm.uiState.value is QuizUiState.Finished) {
                        nav.navigate(CountriesRoute.QuizResult.route) {
                            popUpTo(CountriesRoute.Quiz.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(CountriesRoute.QuizResult.route) {
            val quizState by quizVm.uiState.collectAsState()
            val result = (quizState as? QuizUiState.Finished)?.result

            QuizResultScreen(
                result = result,
                onBack = {
                    quizVm.reset()
                    nav.popBackStack(CountriesRoute.Search.route, inclusive = false)
                },
                onTryAgain = {
                    quizVm.reset()
                    nav.navigate(CountriesRoute.QuizSetup.route) {
                        popUpTo(CountriesRoute.QuizResult.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(CountriesRoute.QuizHistory.route) {
            val vm: QuizHistoryViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            QuizHistoryScreen(
                uiState = uiState,
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

            val code = backStackEntry.arguments
                ?.getString(CountriesRoute.Detail.ARG_CODE)
                .orEmpty()

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

        composable(CountriesRoute.Settings.route) {
            val vm: SettingsViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()

            SettingsScreen(
                uiState = uiState,
                onBack = { nav.popBackStack() },
                onDefaultQuizAreaChange = vm::setDefaultQuizArea,
                onDefaultQuizModeChange = vm::setDefaultQuizMode,
                onDefaultQuestionCountChange = vm::setDefaultQuestionCount,
                onCacheTtlChange = vm::setCacheTtlHours,
                onUpdateOnlyWifiChange = vm::setUpdateOnlyWifi
            )
        }

    }
}