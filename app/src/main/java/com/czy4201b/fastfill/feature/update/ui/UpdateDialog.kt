package com.czy4201b.fastfill.feature.update.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.czy4201b.fastfill.feature.update.UpdateViewModel

@Composable
fun UpdateDialog(
    updateVm: UpdateViewModel
) {
    val info by updateVm.updateInfo.collectAsStateWithLifecycle() // 版本信息

    AlertDialog(
        onDismissRequest = {  },   // 关闭事件
        title = { Text("发现新版本 ${info?.version ?: ""}") },
        text = {
            Column {
                Text("发布时间：${info?.publishedAt ?: ""}")
                Text(text = info?.changelog ?: "", maxLines = 5)
            }
        },
        confirmButton = {
            Button(
                onClick = {  }, // 下载
                enabled = true
            ) {
                Text(if (true) "下载中…" else "立即更新")
            }
        },
        dismissButton = {
            TextButton(onClick = { }) {
                Text("稍后")
            }
        }
    )
}