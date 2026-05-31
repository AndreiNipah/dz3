package com.example.dz3.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dz3.model.AppSettings
import com.example.dz3.model.QuizArea
import com.example.dz3.model.QuizMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: AppSettings,
    onBack: () -> Unit,
    onDefaultQuizAreaChange: (QuizArea) -> Unit,
    onDefaultQuizModeChange: (QuizMode) -> Unit,
    onDefaultQuestionCountChange: (Int) -> Unit,
    onCacheTtlChange: (Int) -> Unit,
    onUpdateOnlyWifiChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SettingsSection(title = "Default quiz area") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizArea.entries.forEach { area ->
                            SettingsChip(
                                text = area.title,
                                selected = uiState.defaultQuizArea == area,
                                onClick = { onDefaultQuizAreaChange(area) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Default quiz mode") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizMode.entries.forEach { mode ->
                            SettingsChip(
                                text = mode.title,
                                selected = uiState.defaultQuizMode == mode,
                                onClick = { onDefaultQuizModeChange(mode) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Default question count") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 20).forEach { count ->
                            SettingsChip(
                                text = count.toString(),
                                selected = uiState.defaultQuestionCount == count,
                                onClick = { onDefaultQuestionCountChange(count) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Cache lifetime") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            24 to "1 day",
                            72 to "3 days",
                            168 to "7 days"
                        ).forEach { (hours, title) ->
                            SettingsChip(
                                text = title,
                                selected = uiState.cacheTtlHours == hours,
                                onClick = { onCacheTtlChange(hours) }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Update cache only via Wi-Fi",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Will be used by background sync",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Switch(
                        checked = uiState.updateOnlyWifi,
                        onCheckedChange = onUpdateOnlyWifiChange
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        content()
    }
}

@Composable
private fun SettingsChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            labelColor = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    )
}