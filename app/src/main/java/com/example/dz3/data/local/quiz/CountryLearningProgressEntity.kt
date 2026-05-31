package com.example.dz3.data.local.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_learning_progress")
data class CountryLearningProgressEntity(
    @PrimaryKey val countryCode: String,
    val countryName: String,
    val timesAnswered: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val lastAnsweredAt: Long
)