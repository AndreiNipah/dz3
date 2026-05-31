package com.example.dz3.data.quiz

import com.example.dz3.data.local.cache.CachedCountryDao
import com.example.dz3.data.local.cache.CachedCountryDetailsEntity
import com.example.dz3.data.local.cache.CachedCountryEntity
import com.example.dz3.data.local.quiz.CountryLearningProgressEntity
import com.example.dz3.data.local.quiz.QuizAnswerEntity
import com.example.dz3.data.local.quiz.QuizDao
import com.example.dz3.data.local.quiz.QuizSessionEntity
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizRepositoryMistakesTest {

    @Test
    fun generateQuestions_inMistakesMode_usesOnlyCountriesWithMistakes() = runTest {
        val cachedDao = FakeCachedCountryDao(
            countries = listOf(
                country("FRA", "France", "Paris"),
                country("DEU", "Germany", "Berlin"),
                country("ITA", "Italy", "Rome"),
                country("ESP", "Spain", "Madrid"),
                country("POL", "Poland", "Warsaw"),
                country("JPN", "Japan", "Tokyo")
            )
        )

        val quizDao = FakeQuizDao(
            mistakes = listOf(
                progress("FRA", "France"),
                progress("DEU", "Germany"),
                progress("ITA", "Italy"),
                progress("ESP", "Spain")
            )
        )

        val repository = QuizRepository(
            cachedCountryDao = cachedDao,
            quizDao = quizDao,
            questionGenerator = QuizQuestionGenerator()
        )

        val questions = repository.generateQuestions(
            area = QuizArea.WORLD,
            mode = QuizMode.MISTAKES,
            questionCount = 10
        )

        assertEquals(4, questions.size)
        assertTrue(questions.all { it.countryCode in setOf("FRA", "DEU", "ITA", "ESP") })
    }

    private fun country(
        code: String,
        name: String,
        capital: String
    ): CachedCountryEntity {
        return CachedCountryEntity(
            code = code,
            name = name,
            capital = capital,
            region = "Europe",
            subregion = "Western Europe",
            flagUrl = "",
            population = 1L,
            quizAreas = "WORLD,EUROPE,EURASIA",
            updatedAt = 0L
        )
    }

    private fun progress(
        code: String,
        name: String
    ): CountryLearningProgressEntity {
        return CountryLearningProgressEntity(
            countryCode = code,
            countryName = name,
            timesAnswered = 1,
            correctAnswers = 0,
            wrongAnswers = 1,
            lastAnsweredAt = 0L
        )
    }
}

private class FakeCachedCountryDao(
    private val countries: List<CachedCountryEntity>
) : CachedCountryDao {

    override fun observeAllCountries(): Flow<List<CachedCountryEntity>> {
        return MutableStateFlow(countries)
    }

    override suspend fun getAllCountries(): List<CachedCountryEntity> {
        return countries
    }

    override suspend fun searchCountriesByName(query: String): List<CachedCountryEntity> {
        return countries.filter { it.name.contains(query, ignoreCase = true) }
    }

    override suspend fun getCountryByCode(code: String): CachedCountryEntity? {
        return countries.firstOrNull { it.code == code }
    }

    override suspend fun getCountriesByQuizArea(quizArea: String): List<CachedCountryEntity> {
        return countries.filter { it.quizAreas.contains(quizArea) }
    }

    override suspend fun upsertCountries(countries: List<CachedCountryEntity>) = Unit

    override suspend fun getDetailsByCode(code: String): CachedCountryDetailsEntity? {
        return null
    }

    override suspend fun upsertDetails(details: CachedCountryDetailsEntity) = Unit
}

private class FakeQuizDao(
    private val mistakes: List<CountryLearningProgressEntity>
) : QuizDao {

    private val sessionsFlow = MutableStateFlow<List<QuizSessionEntity>>(emptyList())
    private val progressFlow = MutableStateFlow(mistakes)

    override suspend fun insertSession(session: QuizSessionEntity): Long {
        return 1L
    }

    override suspend fun insertAnswers(answers: List<QuizAnswerEntity>) = Unit

    override fun observeSessions(): Flow<List<QuizSessionEntity>> {
        return sessionsFlow
    }

    override suspend fun getAnswersForSession(sessionId: Long): List<QuizAnswerEntity> {
        return emptyList()
    }

    override fun observeProgress(): Flow<List<CountryLearningProgressEntity>> {
        return progressFlow
    }

    override suspend fun getCountriesWithMistakes(): List<CountryLearningProgressEntity> {
        return mistakes
    }

    override suspend fun getProgressByCode(countryCode: String): CountryLearningProgressEntity? {
        return mistakes.firstOrNull { it.countryCode == countryCode }
    }

    override suspend fun upsertProgress(progress: CountryLearningProgressEntity) = Unit
}