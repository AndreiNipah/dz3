package com.example.dz3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dz3.data.settings.SettingsRepository
import com.example.dz3.model.AppSettings
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<AppSettings> = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    fun setDefaultQuizArea(area: QuizArea) {
        viewModelScope.launch {
            settingsRepository.setDefaultQuizArea(area)
        }
    }

    fun setDefaultQuizMode(mode: QuizMode) {
        viewModelScope.launch {
            settingsRepository.setDefaultQuizMode(mode)
        }
    }

    fun setDefaultQuestionCount(count: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultQuestionCount(count)
        }
    }

    fun setCacheTtlHours(hours: Int) {
        viewModelScope.launch {
            settingsRepository.setCacheTtlHours(hours)
        }
    }

    fun setUpdateOnlyWifi(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUpdateOnlyWifi(value)
        }
    }
}