package com.example.dz3.data.quiz

import com.example.dz3.data.local.cache.CachedCountryEntity
import com.example.dz3.model.QuizArea
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizQuestionGeneratorTest {

    private val generator = QuizQuestionGenerator()

    @Test
    fun generateQuestions_asiaDoesNotIncludeEuropeBecauseOfEurasiaSubstring() {
        val countries = listOf(
            country("FRA", "France", "Paris", "Europe", "WORLD,EUROPE,EURASIA"),
            country("DEU", "Germany", "Berlin", "Europe", "WORLD,EUROPE,EURASIA"),
            country("ITA", "Italy", "Rome", "Europe", "WORLD,EUROPE,EURASIA"),
            country("ESP", "Spain", "Madrid", "Europe", "WORLD,EUROPE,EURASIA"),

            country("JPN", "Japan", "Tokyo", "Asia", "WORLD,ASIA,EURASIA"),
            country("CHN", "China", "Beijing", "Asia", "WORLD,ASIA,EURASIA"),
            country("KOR", "South Korea", "Seoul", "Asia", "WORLD,ASIA,EURASIA"),
            country("THA", "Thailand", "Bangkok", "Asia", "WORLD,ASIA,EURASIA")
        )

        val questions = generator.generateQuestions(
            countries = countries,
            fallbackCountries = countries,
            area = QuizArea.ASIA,
            questionCount = 10
        )

        assertTrue(questions.isNotEmpty())
        assertTrue(
            questions.all {
                it.countryName in listOf("Japan", "China", "South Korea", "Thailand")
            }
        )
    }

    @Test
    fun generateQuestions_createsQuestionsWithFourOptions() {
        val countries = listOf(
            country("FRA", "France", "Paris", "Europe", "WORLD,EUROPE,EURASIA"),
            country("DEU", "Germany", "Berlin", "Europe", "WORLD,EUROPE,EURASIA"),
            country("ITA", "Italy", "Rome", "Europe", "WORLD,EUROPE,EURASIA"),
            country("ESP", "Spain", "Madrid", "Europe", "WORLD,EUROPE,EURASIA"),
            country("POL", "Poland", "Warsaw", "Europe", "WORLD,EUROPE,EURASIA")
        )

        val questions = generator.generateQuestions(
            countries = countries,
            fallbackCountries = countries,
            area = QuizArea.EUROPE,
            questionCount = 3
        )

        assertEquals(3, questions.size)

        questions.forEach { question ->
            assertEquals(4, question.options.size)
            assertTrue(question.options.contains(question.correctCapital))
            assertEquals(4, question.options.distinct().size)
        }
    }

    @Test
    fun generateQuestions_filtersByArea() {
        val countries = listOf(
            country("FRA", "France", "Paris", "Europe", "WORLD,EUROPE,EURASIA"),
            country("DEU", "Germany", "Berlin", "Europe", "WORLD,EUROPE,EURASIA"),
            country("ITA", "Italy", "Rome", "Europe", "WORLD,EUROPE,EURASIA"),
            country("ESP", "Spain", "Madrid", "Europe", "WORLD,EUROPE,EURASIA"),
            country("JPN", "Japan", "Tokyo", "Asia", "WORLD,ASIA,EURASIA"),
            country("CHN", "China", "Beijing", "Asia", "WORLD,ASIA,EURASIA")
        )

        val questions = generator.generateQuestions(
            countries = countries,
            fallbackCountries = countries,
            area = QuizArea.EUROPE,
            questionCount = 10
        )

        assertTrue(questions.isNotEmpty())
        assertTrue(questions.all { it.countryName in listOf("France", "Germany", "Italy", "Spain") })
    }

    @Test
    fun generateQuestions_returnsEmpty_whenNotEnoughCountries() {
        val countries = listOf(
            country("FRA", "France", "Paris", "Europe", "WORLD,EUROPE,EURASIA"),
            country("DEU", "Germany", "Berlin", "Europe", "WORLD,EUROPE,EURASIA")
        )

        val questions = generator.generateQuestions(
            countries = countries,
            fallbackCountries = countries,
            area = QuizArea.EUROPE,
            questionCount = 5
        )

        assertTrue(questions.isEmpty())
    }

    private fun country(
        code: String,
        name: String,
        capital: String,
        region: String,
        quizAreas: String
    ): CachedCountryEntity {
        return CachedCountryEntity(
            code = code,
            name = name,
            capital = capital,
            region = region,
            subregion = "Test subregion",
            flagUrl = "",
            population = 1L,
            quizAreas = quizAreas,
            updatedAt = 0L
        )
    }
}