package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.czy4201b.fastfill.core.components.ModernSwitch
import com.czy4201b.fastfill.core.theme.DarkCustomBackground
import com.czy4201b.fastfill.core.theme.LightCustomBackground

@Composable
fun TimeSettings(
    modifier: Modifier = Modifier
) {
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
                .fillMaxSize(),
        ) {
            Text(
                text = "时间设置",
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Light,
            )
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "定时开始",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                ModernSwitch(
                    modifier = Modifier,
                    checked = false,
                    onCheckedChange = {

                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun TimeSettingsPreview() {
    TimeSettings(
        modifier = Modifier.fillMaxSize()
    )
}