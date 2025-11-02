package com.czy4201b.fastfill.feature.fastfill.ui

data class TimeSettingsUiState(
    val isStartTimeEnable: Boolean = false,
    val isShowTimePicker: Boolean = false,
    val timeSettings: String = "未设置",
    val isWaitForStart: Boolean = true,
    val isGetNotificationPermission: Boolean = false,
    val isGetExactAlarmPermission: Boolean = false
)