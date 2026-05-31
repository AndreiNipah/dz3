package com.example.dz3.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dz3.model.QuizArea
import com.example.dz3.ui.viewmodel.QuizSetupState
import com.example.dz3.model.QuizMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    uiState: QuizSetupState,
    onBack: () -> Unit,
    onAreaChange: (QuizArea) -> Unit,
    onModeChange: (QuizMode) -> Unit,
    onQuestionCountChange: (Int) -> Unit,
    onPrepareQuiz: () -> Unit,
    onStartQuiz: () -> Unit
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose quiz area",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizArea.entries.forEach { area ->
                            val selected = uiState.selectedArea == area

                            AssistChip(
                                onClick = { onAreaChange(area) },
                                label = { Text(area.title) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    labelColor = if (selected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Quiz mode",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizMode.entries.forEach { mode ->
                            val selected = uiState.selectedMode == mode

                            AssistChip(
                                onClick = { onModeChange(mode) },
                                label = { Text(mode.title) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    labelColor = if (selected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Questions",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (uiState.availableQuestionCounts.isEmpty()) {
                        Text(
                            text = "Not enough countries for quiz in this area",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.availableQuestionCounts.forEach { count ->
                                val selected = uiState.selectedQuestionCount == count

                                AssistChip(
                                    onClick = { onQuestionCountChange(count) },
                                    label = { Text(count.toString()) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        labelColor = if (selected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Text("Available countries: ${uiState.availableCountriesCount}")
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator()
                    }

                    uiState.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = onPrepareQuiz,
                        enabled = !uiState.isLoading && uiState.availableQuestionCounts.isNotEmpty()
                    ) {
                        Text("Prepare quiz")
                    }

                    Button(
                        onClick = onStartQuiz,
                        enabled = uiState.generatedQuestions.isNotEmpty()
                    ) {
                        Text("Start quiz")
                    }
                }
            }
        }
    }
}