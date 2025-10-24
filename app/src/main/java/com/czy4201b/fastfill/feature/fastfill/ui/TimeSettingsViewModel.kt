package com.czy4201b.fastfill.feature.fastfill.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.czy4201b.fastfill.feature.fastfill.notification.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.core.net.toUri

class TimeSettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimeSettingsUiState())
    val state: StateFlow<TimeSettingsUiState> = _state.asStateFlow()

    fun setStartTimeEnable(isEnable: Boolean) {
        val notification = _state.value.isGetNotificationPermission
        val exactAlarm = _state.value.isGetExactAlarmPermission
        if (!notification || !exactAlarm) {
            return
        }
        _state.update { state ->
            state.copy(
                isStartTimeEnable = isEnable
            )
        }
    }

    fun getNotificationPermission() {
        _state.update { state ->
            state.copy(
                isGetNotificationPermission = true
            )
        }
    }

    fun getExactAlarmPermission() {
        _state.update { state ->
            state.copy(
                isGetExactAlarmPermission = true
            )
        }
    }

    fun setOneTimeAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!am.canScheduleExactAlarms()) {
            return      // 等用户回来再点一次
        }

        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val trigger = System.currentTimeMillis() + 5000
        am.setExact(AlarmManager.RTC_WAKEUP, trigger, pending)
    }
}