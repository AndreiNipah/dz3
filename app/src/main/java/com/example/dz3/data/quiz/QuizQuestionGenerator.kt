package com.example.dz3.data.quiz

import com.example.dz3.data.local.cache.CachedCountryEntity
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizQuestion
import javax.inject.Inject
import kotlin.random.Random

class QuizQuestionGenerator @Inject constructor() {

    fun generateQuestions(
        countries: List<CachedCountryEntity>,
        fallbackCountries: List<CachedCountryEntity>,
        area: QuizArea,
        questionCount: Int
    ): List<QuizQuestion> {
        val availableCountries = countries
            .filter { it.hasCapital() }
            .filter { area == QuizArea.WORLD || it.hasQuizArea(area) }

        if (availableCountries.size < MIN_COUNTRIES_FOR_QUIZ) {
            return emptyList()
        }

        val realQuestionCount = minOf(questionCount, availableCountries.size)

        return availableCountries
            .shuffled()
            .take(realQuestionCount)
            .mapNotNull { country ->
                buildQuestion(
                    targetCountry = country,
                    areaCountries = availableCountries,
                    fallbackCountries = fallbackCountries
                )
            }
    }

    private fun CachedCountryEntity.hasQuizArea(area: QuizArea): Boolean {
        return quizAreas
            .split(",")
            .map { it.trim() }
            .contains(area.name)
    }

    private fun buildQuestion(
        targetCountry: CachedCountryEntity,
        areaCountries: List<CachedCountryEntity>,
        fallbackCountries: List<CachedCountryEntity>
    ): QuizQuestion? {
        val correctCapital = targetCountry.capital
        if (correctCapital.isBlank() || correctCapital == "—") return null

        val wrongOptionsFromArea = areaCountries
            .filter { it.code != targetCountry.code }
            .map { it.capital }
            .filter { it.isValidCapital() }
            .distinct()
            .shuffled()

        val wrongOptionsFromFallback = fallbackCountries
            .filter { it.code != targetCountry.code }
            .map { it.capital }
            .filter { it.isValidCapital() }
            .distinct()
            .shuffled()

        val wrongOptions = (wrongOptionsFromArea + wrongOptionsFromFallback)
            .filter { it != correctCapital }
            .distinct()
            .take(3)

        if (wrongOptions.size < 3) return null

        val options = (wrongOptions + correctCapital)
            .shuffled(Random(System.nanoTime()))

        return QuizQuestion(
            countryCode = targetCountry.code,
            countryName = targetCountry.name,
            correctCapital = correctCapital,
            options = options
        )
    }

    private fun CachedCountryEntity.hasCapital(): Boolean {
        return capital.isValidCapital()
    }

    private fun String.isValidCapital(): Boolean {
        return isNotBlank() && this != "—"
    }

    companion object {
        private const val MIN_COUNTRIES_FOR_QUIZ = 4
    }
}