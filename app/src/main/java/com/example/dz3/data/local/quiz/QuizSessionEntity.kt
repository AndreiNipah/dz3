package com.example.dz3.data.local.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_sessions")
data class QuizSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizArea: String,
    val questionCount: Int,
    val correctCount: Int,
    val startedAt: Long,
    val finishedAt: Long
)