package com.example.dz3.data.local.quiz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Insert
    suspend fun insertSession(session: QuizSessionEntity): Long

    @Insert
    suspend fun insertAnswers(answers: List<QuizAnswerEntity>)

    @Query("SELECT * FROM quiz_sessions ORDER BY finishedAt DESC")
    fun observeSessions(): Flow<List<QuizSessionEntity>>

    @Query("SELECT * FROM quiz_answers WHERE sessionId = :sessionId")
    suspend fun getAnswersForSession(sessionId: Long): List<QuizAnswerEntity>

    @Query("SELECT * FROM country_learning_progress ORDER BY wrongAnswers DESC, lastAnsweredAt DESC")
    fun observeProgress(): Flow<List<CountryLearningProgressEntity>>

    @Query("SELECT * FROM country_learning_progress WHERE wrongAnswers > 0 ORDER BY wrongAnswers DESC")
    suspend fun getCountriesWithMistakes(): List<CountryLearningProgressEntity>

    @Query("SELECT * FROM country_learning_progress WHERE countryCode = :countryCode LIMIT 1")
    suspend fun getProgressByCode(countryCode: String): CountryLearningProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: CountryLearningProgressEntity)
}