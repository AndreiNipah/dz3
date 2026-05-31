package com.example.dz3.model

data class QuizQuestion(
    val countryCode: String,
    val countryName: String,
    val correctCapital: String,
    val options: List<String>
)

data class QuizAnswerResult(
    val question: QuizQuestion,
    val selectedCapital: String,
    val isCorrect: Boolean
)

data class QuizResult(
    val area: QuizArea,
    val answers: List<QuizAnswerResult>
) {
    val questionCount: Int
        get() = answers.size

    val correctCount: Int
        get() = answers.count { it.isCorrect }
}