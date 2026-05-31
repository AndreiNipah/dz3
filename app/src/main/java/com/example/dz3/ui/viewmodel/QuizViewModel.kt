package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.quiz.QuizRepository
import com.example.dz3.model.QuizAnswerResult
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizQuestion
import com.example.dz3.model.QuizResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface QuizUiState {
    data object Idle : QuizUiState
    data object Loading : QuizUiState

    data class InProgress(
        val area: QuizArea,
        val questions: List<QuizQuestion>,
        val currentIndex: Int,
        val answers: List<QuizAnswerResult>,
        val selectedCapital: String? = null,
        val answerChecked: Boolean = false
    ) : QuizUiState {
        val currentQuestion: QuizQuestion
            get() = questions[currentIndex]

        val currentNumber: Int
            get() = currentIndex + 1

        val totalCount: Int
            get() = questions.size

        val correctCount: Int
            get() = answers.count { it.isCorrect }

        val canGoNext: Boolean
            get() = selectedCapital != null && answerChecked

        val isLastQuestion: Boolean
            get() = currentIndex == questions.lastIndex
    }

    data class Finished(
        val result: QuizResult
    ) : QuizUiState

    data class Error(
        val message: String
    ) : QuizUiState
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Idle)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun startQuiz(
        area: QuizArea,
        questions: List<QuizQuestion>
    ) {
        if (questions.isEmpty()) {
            _uiState.value = QuizUiState.Error("No questions generated")
            return
        }

        _uiState.value = QuizUiState.InProgress(
            area = area,
            questions = questions,
            currentIndex = 0,
            answers = emptyList()
        )
    }

    fun selectAnswer(capital: String) {
        val state = _uiState.value as? QuizUiState.InProgress ?: return

        if (state.answerChecked) return

        _uiState.value = state.copy(
            selectedCapital = capital
        )
    }

    fun checkAnswer() {
        val state = _uiState.value as? QuizUiState.InProgress ?: return
        val selected = state.selectedCapital ?: return

        if (state.answerChecked) return

        val question = state.currentQuestion
        val isCorrect = selected == question.correctCapital

        val answer = QuizAnswerResult(
            question = question,
            selectedCapital = selected,
            isCorrect = isCorrect
        )

        _uiState.value = state.copy(
            answers = state.answers + answer,
            answerChecked = true
        )
    }

    fun nextQuestion() {
        val state = _uiState.value as? QuizUiState.InProgress ?: return

        if (!state.canGoNext) return

        if (state.isLastQuestion) {
            finishQuiz(state)
        } else {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedCapital = null,
                answerChecked = false
            )
        }
    }

    private fun finishQuiz(state: QuizUiState.InProgress) {
        val result = QuizResult(
            area = state.area,
            answers = state.answers
        )

        _uiState.value = QuizUiState.Finished(result)

        viewModelScope.launch {
            quizRepository.saveQuizResult(result)
        }
    }

    fun reset() {
        _uiState.value = QuizUiState.Idle
    }
}