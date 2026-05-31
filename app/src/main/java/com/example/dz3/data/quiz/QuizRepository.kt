package com.example.dz3.data.quiz

import com.example.dz3.data.local.cache.CachedCountryDao
import com.example.dz3.data.local.quiz.CountryLearningProgressEntity
import com.example.dz3.data.local.quiz.QuizAnswerEntity
import com.example.dz3.data.local.quiz.QuizDao
import com.example.dz3.data.local.quiz.QuizSessionEntity
import com.example.dz3.model.QuizAnswerResult
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizMode
import com.example.dz3.model.QuizQuestion
import com.example.dz3.model.QuizResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val cachedCountryDao: CachedCountryDao,
    private val quizDao: QuizDao,
    private val questionGenerator: QuizQuestionGenerator
) {

    fun observeSessions(): Flow<List<QuizSessionEntity>> {
        return quizDao.observeSessions()
    }

    fun observeProgress(): Flow<List<CountryLearningProgressEntity>> {
        return quizDao.observeProgress()
    }

    suspend fun generateQuestions(
        area: QuizArea,
        mode: QuizMode,
        questionCount: Int
    ): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val allCountries = cachedCountryDao.getAllCountries()

        val sourceCountries = when (mode) {
            QuizMode.ALL_COUNTRIES -> allCountries

            QuizMode.MISTAKES -> {
                val mistakeCodes = quizDao.getCountriesWithMistakes()
                    .map { it.countryCode }
                    .toSet()

                allCountries.filter { it.code in mistakeCodes }
            }
        }

        questionGenerator.generateQuestions(
            countries = sourceCountries,
            fallbackCountries = allCountries,
            area = area,
            questionCount = questionCount
        )
    }

    suspend fun saveQuizResult(result: QuizResult) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        val sessionId = quizDao.insertSession(
            QuizSessionEntity(
                quizArea = result.area.name,
                questionCount = result.questionCount,
                correctCount = result.correctCount,
                startedAt = now,
                finishedAt = now
            )
        )

        val answerEntities = result.answers.map { answer ->
            QuizAnswerEntity(
                sessionId = sessionId,
                countryCode = answer.question.countryCode,
                countryName = answer.question.countryName,
                correctCapital = answer.question.correctCapital,
                selectedCapital = answer.selectedCapital,
                isCorrect = answer.isCorrect
            )
        }

        quizDao.insertAnswers(answerEntities)

        result.answers.forEach { answer ->
            updateProgress(answer = answer, answeredAt = now)
        }
    }

    private suspend fun updateProgress(
        answer: QuizAnswerResult,
        answeredAt: Long
    ) {
        val current = quizDao.getProgressByCode(answer.question.countryCode)

        val updated = if (current == null) {
            CountryLearningProgressEntity(
                countryCode = answer.question.countryCode,
                countryName = answer.question.countryName,
                timesAnswered = 1,
                correctAnswers = if (answer.isCorrect) 1 else 0,
                wrongAnswers = if (answer.isCorrect) 0 else 1,
                lastAnsweredAt = answeredAt
            )
        } else {
            current.copy(
                timesAnswered = current.timesAnswered + 1,
                correctAnswers = current.correctAnswers + if (answer.isCorrect) 1 else 0,
                wrongAnswers = current.wrongAnswers + if (answer.isCorrect) 0 else 1,
                lastAnsweredAt = answeredAt
            )
        }

        quizDao.upsertProgress(updated)
    }
}