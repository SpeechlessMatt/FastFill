package com.czy4201b.fastfill.core.permission

import android.app.AlarmManager
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberExactAlarmLauncher(
    onAllGranted: () -> Unit = {},
    onNotifyDenied: () -> Unit = {},
    onExactAlarmDenied: () -> Unit = {}
): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current

    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("permission", "get permission result")
        val notifyGranted = granted

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 精确闹钟权限（系统设置，非用户弹窗）
        val exactAlarmGranted = am.canScheduleExactAlarms()

        // when好用啊
        when {
            notifyGranted && exactAlarmGranted -> onAllGranted()
            !notifyGranted -> onNotifyDenied()
            else -> onExactAlarmDenied()
        }
    }
}
