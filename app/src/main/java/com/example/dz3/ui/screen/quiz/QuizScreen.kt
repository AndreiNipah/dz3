package com.example.dz3.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dz3.ui.viewmodel.QuizUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onBack: () -> Unit,
    onSelectAnswer: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capital Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            QuizUiState.Idle -> {
                EmptyQuizContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    text = "Quiz is not started"
                )
            }

            QuizUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading quiz...")
                }
            }

            is QuizUiState.Error -> {
                EmptyQuizContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    text = uiState.message
                )
            }

            is QuizUiState.Finished -> {
                EmptyQuizContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    text = "Quiz is finished"
                )
            }

            is QuizUiState.InProgress -> {
                QuizInProgressContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = uiState,
                    onSelectAnswer = onSelectAnswer,
                    onCheckAnswer = onCheckAnswer,
                    onNextQuestion = onNextQuestion
                )
            }
        }
    }
}

@Composable
private fun QuizInProgressContent(
    modifier: Modifier,
    state: QuizUiState.InProgress,
    onSelectAnswer: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    val question = state.currentQuestion
    val progress = state.currentNumber.toFloat() / state.totalCount.toFloat()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Question ${state.currentNumber} of ${state.totalCount}",
                    style = MaterialTheme.typography.titleMedium
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Correct answers: ${state.correctCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Choose the capital of:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = question.countryName,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEach { option ->
                    val selected = state.selectedCapital == option

                    val answerColor = when {
                        !state.answerChecked && selected ->
                            MaterialTheme.colorScheme.primaryContainer

                        state.answerChecked && option == question.correctCapital ->
                            MaterialTheme.colorScheme.primaryContainer

                        state.answerChecked && selected && option != question.correctCapital ->
                            MaterialTheme.colorScheme.errorContainer

                        else ->
                            MaterialTheme.colorScheme.surface
                    }

                    OutlinedButton(
                        onClick = { onSelectAnswer(option) },
                        enabled = !state.answerChecked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = option,
                            color = when {
                                state.answerChecked && selected && option != question.correctCapital ->
                                    MaterialTheme.colorScheme.error

                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    if (selected || state.answerChecked && option == question.correctCapital) {
                        Text(
                            text = when {
                                state.answerChecked && option == question.correctCapital -> "Correct answer"
                                selected -> "Selected"
                                else -> ""
                            },
                            color = answerColor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCheckAnswer,
                    enabled = state.selectedCapital != null && !state.answerChecked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check")
                }

                Button(
                    onClick = onNextQuestion,
                    enabled = state.canGoNext,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.isLastQuestion) "Finish quiz" else "Next question")
                }
            }
        }
    }
}

@Composable
private fun EmptyQuizContent(
    modifier: Modifier,
    text: String
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text)
    }
}