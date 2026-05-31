package com.example.dz3.data.local.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_answers")
data class QuizAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val countryCode: String,
    val countryName: String,
    val correctCapital: String,
    val selectedCapital: String,
    val isCorrect: Boolean
)