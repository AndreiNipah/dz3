package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.quiz.QuizRepository
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizMode
import com.example.dz3.model.QuizQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.dz3.data.settings.SettingsRepository

data class QuizSetupState(
    val selectedArea: QuizArea = QuizArea.WORLD,
    val selectedMode: QuizMode = QuizMode.ALL_COUNTRIES,
    val selectedQuestionCount: Int = 5,
    val availableQuestionCounts: List<Int> = listOf(5),
    val availableCountriesCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val generatedQuestions: List<QuizQuestion> = emptyList()
)

@HiltViewModel
class QuizSetupViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizSetupState())
    val uiState: StateFlow<QuizSetupState> = _uiState.asStateFlow()

    private var settingsApplied = false

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                if (!settingsApplied) {
                    settingsApplied = true

                    _uiState.value = _uiState.value.copy(
                        selectedArea = settings.defaultQuizArea,
                        selectedMode = settings.defaultQuizMode,
                        selectedQuestionCount = settings.defaultQuestionCount
                    )

                    updateAvailableCounts()
                }
            }
        }
    }

    fun selectArea(area: QuizArea) {
        _uiState.value = _uiState.value.copy(
            selectedArea = area,
            generatedQuestions = emptyList(),
            errorMessage = null
        )
        updateAvailableCounts()
    }

    fun selectMode(mode: QuizMode) {
        _uiState.value = _uiState.value.copy(
            selectedMode = mode,
            generatedQuestions = emptyList(),
            errorMessage = null
        )
        updateAvailableCounts()
    }

    fun selectQuestionCount(count: Int) {
        _uiState.value = _uiState.value.copy(
            selectedQuestionCount = count,
            generatedQuestions = emptyList(),
            errorMessage = null
        )
    }

    fun prepareQuiz() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessage = null,
                generatedQuestions = emptyList()
            )

            val questions = quizRepository.generateQuestions(
                area = state.selectedArea,
                mode = state.selectedMode,
                questionCount = state.selectedQuestionCount
            )

            _uiState.value = if (questions.isEmpty()) {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = when (state.selectedMode) {
                        QuizMode.ALL_COUNTRIES -> "Not enough countries for this quiz"
                        QuizMode.MISTAKES -> "You need at least 4 countries with mistakes to start this mode"
                    }
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    generatedQuestions = questions
                )
            }
        }
    }

    private fun updateAvailableCounts() {
        val state = _uiState.value

        viewModelScope.launch {
            val maxAvailable = quizRepository.generateQuestions(
                area = state.selectedArea,
                mode = state.selectedMode,
                questionCount = 1000
            ).size

            val availableCounts = listOf(5, 10, 15, 20)
                .filter { it <= maxAvailable }

            val safeSelectedCount = when {
                availableCounts.isEmpty() -> 5
                state.selectedQuestionCount in availableCounts -> state.selectedQuestionCount
                else -> availableCounts.first()
            }

            _uiState.value = _uiState.value.copy(
                availableCountriesCount = maxAvailable,
                availableQuestionCounts = availableCounts,
                selectedQuestionCount = safeSelectedCount
            )
        }
    }
}