package com.example.dz3.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.dz3.model.QuizResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    result: QuizResult?,
    onBack: () -> Unit,
    onTryAgain: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Result") },
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
        if (result == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("No quiz result")
                Button(onClick = onTryAgain) {
                    Text("Start new quiz")
                }
            }
        } else {
            val percent = if (result.questionCount == 0) {
                0
            } else {
                result.correctCount * 100 / result.questionCount
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Your result",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "${result.correctCount} / ${result.questionCount}",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Text(
                                text = "$percent% correct",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "Area: ${result.area.title}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = onTryAgain,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Try again")
                    }
                }

                items(result.answers) { answer ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = answer.question.countryName,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Your answer: ${answer.selectedCapital}")
                            Text("Correct answer: ${answer.question.correctCapital}")

                            Text(
                                text = if (answer.isCorrect) "Correct" else "Wrong",
                                color = if (answer.isCorrect) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}