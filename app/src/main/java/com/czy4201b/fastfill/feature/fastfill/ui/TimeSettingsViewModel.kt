package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimeSettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimeSettingsUiState())
    val state: StateFlow<TimeSettingsUiState> = _state.asStateFlow()
}