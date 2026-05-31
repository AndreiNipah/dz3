package com.example.dz3.data

import com.example.dz3.model.QuizArea
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizAreaMapperTest {

    @Test
    fun europe_belongsToEuropeAndEurasia() {
        val result = buildQuizAreas(
            region = "Europe",
            subregion = "Western Europe"
        )

        assertTrue(result.contains(QuizArea.WORLD))
        assertTrue(result.contains(QuizArea.EUROPE))
        assertTrue(result.contains(QuizArea.EURASIA))
    }

    @Test
    fun americasWithUnknownSubregion_belongsToNorthAmerica() {
        val result = buildQuizAreas(
            region = "Americas",
            subregion = "Unknown"
        )

        assertTrue(result.contains(QuizArea.WORLD))
        assertTrue(result.contains(QuizArea.AMERICAS))
        assertTrue(result.contains(QuizArea.NORTH_AMERICA))
    }

    @Test
    fun caribbean_belongsToNorthAmerica() {
        val result = buildQuizAreas(
            region = "Americas",
            subregion = "Caribbean"
        )

        assertTrue(result.contains(QuizArea.NORTH_AMERICA))
    }

    @Test
    fun asia_belongsToAsiaAndEurasia() {
        val result = buildQuizAreas(
            region = "Asia",
            subregion = "Eastern Asia"
        )

        assertTrue(result.contains(QuizArea.WORLD))
        assertTrue(result.contains(QuizArea.ASIA))
        assertTrue(result.contains(QuizArea.EURASIA))
    }

    @Test
    fun southAmerica_belongsToAmericasAndSouthAmerica() {
        val result = buildQuizAreas(
            region = "Americas",
            subregion = "South America"
        )

        assertTrue(result.contains(QuizArea.WORLD))
        assertTrue(result.contains(QuizArea.AMERICAS))
        assertTrue(result.contains(QuizArea.SOUTH_AMERICA))
    }

    @Test
    fun northernAmerica_belongsToAmericasAndNorthAmerica() {
        val result = buildQuizAreas(
            region = "Americas",
            subregion = "Northern America"
        )

        assertTrue(result.contains(QuizArea.WORLD))
        assertTrue(result.contains(QuizArea.AMERICAS))
        assertTrue(result.contains(QuizArea.NORTH_AMERICA))
    }
}