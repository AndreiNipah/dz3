package com.example.dz3.model

data class AppSettings(
    val defaultQuizArea: QuizArea = QuizArea.WORLD,
    val defaultQuizMode: QuizMode = QuizMode.ALL_COUNTRIES,
    val defaultQuestionCount: Int = 5,
    val cacheTtlHours: Int = 24,
    val updateOnlyWifi: Boolean = false
)