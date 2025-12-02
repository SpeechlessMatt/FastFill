package com.czy4201b.fastfill.feature.fastfill.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.czy4201b.fastfill.core.components.BottomPicker
import com.czy4201b.fastfill.core.components.BottomPickerProperties
import com.czy4201b.fastfill.core.components.ModernOutlinedButton
import com.czy4201b.fastfill.core.components.ModernSwitch
import com.czy4201b.fastfill.core.permission.rememberExactAlarmLauncher
import com.czy4201b.fastfill.core.theme.DarkCustomBackground
import com.czy4201b.fastfill.core.theme.LightCustomBackground

@Composable
fun TimeSettings(
    modifier: Modifier = Modifier,
    vm: TimeSettingsViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val uiState by vm.state.collectAsState()

    val permissionLauncher = rememberExactAlarmLauncher(
        onAllGranted = {
            vm.getNotificationPermission()
            vm.getExactAlarmPermission()
            vm.setStartTimeEnable(true)
        },
        onNotifyDenied = {
            vm.getExactAlarmPermission()
            Toast.makeText(
                context,
                "开启通知权限才能准时提醒你开始哦",
                Toast.LENGTH_LONG
            ).show()
        },
        onExactAlarmDenied = {
            vm.getNotificationPermission()
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
            Toast.makeText(
                context,
                "开启精确闹钟权限才能准时提醒你开始哦",
                Toast.LENGTH_LONG
            ).show()
        }
    )

    // 与userFillTable 相同样式
    Surface(
        modifier = modifier,
        color = if (isSystemInDarkTheme()) DarkCustomBackground else LightCustomBackground,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxSize()
                .clickable(
                    onClick = {
                        focusManager.clearFocus()
                    },
                    indication = null,
                    interactionSource = null
                ),
        ) {
            Text(
                text = "时间设置",
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Light,
            )

            /* 设置项开始 */
            // 设置开始时间
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "设置开始时间",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                ModernSwitch(
                    modifier = Modifier,
                    checked = uiState.isStartTimeEnable,
                    onCheckedChange = { enable ->
                        if (enable) {
                            // 做个检查，低版本默认就有权限了
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                return@ModernSwitch
                            } else {
                                vm.getNotificationPermission()
                                vm.getExactAlarmPermission()
                            }
                        }
                        vm.setStartTimeEnable(enable)
                    },
                )
            }

            // 关联：设置开始时间-设置时间
            AnimatedVisibility(
                visible = uiState.isStartTimeEnable
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "开始时间",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        ModernOutlinedButton(
                            onClick = {
                                vm.showTimePicker()
                            }
                        ) {
                            Text(uiState.timeSettings, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "提前进入网站等待",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )

                        ModernSwitch(
                            modifier = Modifier,
                            checked = uiState.isWaitForStart,
                            onCheckedChange = { enable ->
                                vm.setWaitForStartEnable(enable)
                            },
                        )
                    }
                }
            }
        }
    }

    BottomPicker(
        shouldShow = uiState.isShowTimePicker,
        onResultNull = { vm.closeTimePicker() },
        onResult = { vm.selectTime(it) },
        modifier = Modifier,
        title = "- 选择开始时间 -",
        titlePadding = PaddingValues(0.dp),
        titleTextStyle = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        properties = BottomPickerProperties(
            pickerHeight = 150.dp,
            pickerBarHeight = 27.dp,
            pickerPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
            pickerTitleTextStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold),
            pickerTitlePadding = PaddingValues(start = 40.dp, top = 8.dp, bottom = 8.dp),
            pickerContentPadding = PaddingValues(start = 40.dp),
            pickerHorizontalArrangement = Arrangement.spacedBy(10.dp)
        ),
    ) {
        val now = java.time.LocalDateTime.now()
        val hour = now.hour
        val minute = now.minute
        val second = now.second

        picker(title = "时针") {
            items(
                items = (hour..23).toList() + (0 until hour).toList()
            ) {
                Text(text = "$it", fontSize = 25.sp)
            }
        }

        picker(title = "分针") {
            items(
                items = (minute..59).toList() + (0 until minute).toList()
            ) {
                Text(text = "$it", fontSize = 25.sp)
            }
        }

        picker(title = "秒针") {
            items(
                items = (second..59).toList() + (0 until second).toList()
            ) {
                Text(text = "$it", fontSize = 25.sp)
            }
        }
    }
}