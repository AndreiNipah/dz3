package com.example.dz3.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dz3.model.AppSettings
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            defaultQuizArea = preferences[DEFAULT_QUIZ_AREA]
                ?.let { runCatching { QuizArea.valueOf(it) }.getOrNull() }
                ?: QuizArea.WORLD,

            defaultQuizMode = preferences[DEFAULT_QUIZ_MODE]
                ?.let { runCatching { QuizMode.valueOf(it) }.getOrNull() }
                ?: QuizMode.ALL_COUNTRIES,

            defaultQuestionCount = preferences[DEFAULT_QUESTION_COUNT] ?: 5,

            cacheTtlHours = preferences[CACHE_TTL_HOURS] ?: 24,

            updateOnlyWifi = preferences[UPDATE_ONLY_WIFI] ?: false
        )
    }

    suspend fun setDefaultQuizArea(area: QuizArea) {
        context.settingsDataStore.edit { preferences ->
            preferences[DEFAULT_QUIZ_AREA] = area.name
        }
    }

    suspend fun setDefaultQuizMode(mode: QuizMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[DEFAULT_QUIZ_MODE] = mode.name
        }
    }

    suspend fun setDefaultQuestionCount(count: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[DEFAULT_QUESTION_COUNT] = count
        }
    }

    suspend fun setCacheTtlHours(hours: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[CACHE_TTL_HOURS] = hours
        }
    }

    suspend fun setUpdateOnlyWifi(value: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[UPDATE_ONLY_WIFI] = value
        }
    }

    private companion object {
        val DEFAULT_QUIZ_AREA = stringPreferencesKey("default_quiz_area")
        val DEFAULT_QUIZ_MODE = stringPreferencesKey("default_quiz_mode")
        val DEFAULT_QUESTION_COUNT = intPreferencesKey("default_question_count")
        val CACHE_TTL_HOURS = intPreferencesKey("cache_ttl_hours")
        val UPDATE_ONLY_WIFI = booleanPreferencesKey("update_only_wifi")
    }
}