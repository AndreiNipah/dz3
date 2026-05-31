package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.local.quiz.CountryLearningProgressEntity
import com.example.dz3.data.local.quiz.QuizSessionEntity
import com.example.dz3.data.quiz.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class QuizHistoryState(
    val sessions: List<QuizSessionEntity> = emptyList(),
    val progress: List<CountryLearningProgressEntity> = emptyList()
) {
    val totalSessions: Int
        get() = sessions.size

    val totalAnsweredCountries: Int
        get() = progress.size

    val totalCorrectAnswers: Int
        get() = progress.sumOf { it.correctAnswers }

    val totalWrongAnswers: Int
        get() = progress.sumOf { it.wrongAnswers }

    val weakCountries: List<CountryLearningProgressEntity>
        get() = progress
            .filter { it.wrongAnswers > 0 }
            .sortedWith(
                compareByDescending<CountryLearningProgressEntity> { it.wrongAnswers }
                    .thenBy { it.countryName }
            )
}

@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    quizRepository: QuizRepository
) : ViewModel() {

    val uiState: StateFlow<QuizHistoryState> = combine(
        quizRepository.observeSessions(),
        quizRepository.observeProgress()
    ) { sessions, progress ->
        QuizHistoryState(
            sessions = sessions,
            progress = progress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuizHistoryState()
    )
}