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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dz3.data.local.quiz.CountryLearningProgressEntity
import com.example.dz3.data.local.quiz.QuizSessionEntity
import com.example.dz3.model.QuizArea
import com.example.dz3.ui.viewmodel.QuizHistoryState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizHistoryScreen(
    uiState: QuizHistoryState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Progress") },
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
        if (uiState.sessions.isEmpty() && uiState.progress.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No quiz history yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Complete a quiz to see your progress here.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SummaryCard(uiState = uiState)
                }

                item {
                    Text(
                        text = "Weak countries",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.weakCountries.isEmpty()) {
                    item {
                        Text("No mistakes yet.")
                    }
                } else {
                    items(uiState.weakCountries.take(10)) { progress ->
                        WeakCountryCard(progress = progress)
                    }
                }

                item {
                    Text(
                        text = "Recent attempts",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.sessions.take(10)) { session ->
                    QuizSessionCard(session = session)
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    uiState: QuizHistoryState
) {
    val totalAnswers = uiState.totalCorrectAnswers + uiState.totalWrongAnswers
    val percent = if (totalAnswers == 0) {
        0
    } else {
        uiState.totalCorrectAnswers * 100 / totalAnswers
    }

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
                text = "Overall progress",
                style = MaterialTheme.typography.titleLarge
            )

            Text("Quiz attempts: ${uiState.totalSessions}")
            Text("Countries answered: ${uiState.totalAnsweredCountries}")
            Text("Correct answers: ${uiState.totalCorrectAnswers}")
            Text("Wrong answers: ${uiState.totalWrongAnswers}")
            Text("Accuracy: $percent%")

            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WeakCountryCard(
    progress: CountryLearningProgressEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = progress.countryName,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Answered: ${progress.timesAnswered}")
            Text("Correct: ${progress.correctAnswers}")
            Text("Wrong: ${progress.wrongAnswers}")
        }
    }
}

@Composable
private fun QuizSessionCard(
    session: QuizSessionEntity
) {
    val percent = if (session.questionCount == 0) {
        0
    } else {
        session.correctCount * 100 / session.questionCount
    }

    val areaTitle = runCatching {
        QuizArea.valueOf(session.quizArea).title
    }.getOrElse {
        session.quizArea
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = areaTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Result: ${session.correctCount} / ${session.questionCount}")
            Text("Accuracy: $percent%")
            Text("Finished: ${formatDate(session.finishedAt)}")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat(
        "dd.MM.yyyy HH:mm",
        Locale.getDefault()
    ).format(Date(timestamp))
}