package com.czy4201b.fastfill.feature.fastfill.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.czy4201b.fastfill.feature.fastfill.data.state.FastFillStateContainer
import com.czy4201b.fastfill.feature.fastfill.notification.AlarmReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class TimeSettingsViewModel @Inject constructor(
    private val stateContainer: FastFillStateContainer
) : ViewModel() {
    private val _loginState = stateContainer.configState.value.isCurrentLogin

    private val _state = MutableStateFlow(TimeSettingsUiState())
    val state: StateFlow<TimeSettingsUiState> = _state.asStateFlow()

    fun setStartTimeEnable(isEnable: Boolean) {
        val notification = _state.value.isGetNotificationPermission
        val exactAlarm = _state.value.isGetExactAlarmPermission
        if (!notification || !exactAlarm) {
            return
        }
        _state.update { state ->
            if (!isEnable) {
                state.copy(
                    isStartTimeEnable = false,
                    timeSettings = "未设置"
                )
            } else {
                state.copy(
                    isStartTimeEnable = true
                )
            }
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

    fun showTimePicker(){
        _state.update { state ->
            state.copy(
                isShowTimePicker = true
            )
        }
    }

    fun closeTimePicker(){
        _state.update { state ->
            state.copy(
                isShowTimePicker = false
            )
        }
    }

    fun selectTime(time: List<Int>){
        val hour = time[0]
        val minute = time[1]
        val second = time[2]

        val now = java.time.LocalDateTime.now()
        val currentHour = now.hour
        val currentMinute = now.minute
        val currentSecond = now.second

        // 比较时间（只比较时分秒）
        val inputTimeSeconds = hour * 3600 + minute * 60 + second
        val currentTimeSeconds = currentHour * 3600 + currentMinute * 60 + currentSecond

        when {
            inputTimeSeconds > currentTimeSeconds -> _state.update { state ->
                state.copy(
                    timeSettings = "今日 ${hour}点${minute}分${second}秒"
                )
            }
            else -> _state.update { state ->
                state.copy(
                    timeSettings = "明日 ${hour}点${minute}分${second}秒"
                )
            }
        }

        closeTimePicker()
    }

    fun setWaitForStartEnable(isEnable: Boolean) {
        _state.update { state ->
            state.copy(
                isWaitForStart = isEnable
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