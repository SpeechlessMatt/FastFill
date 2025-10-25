package com.czy4201b.fastfill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy4201b.fastfill.core.theme.FastFillTheme
import com.czy4201b.fastfill.feature.fastfill.ui.MainView
import com.czy4201b.fastfill.feature.update.UpdateViewModel
import com.czy4201b.fastfill.navigation.FastFillNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 获取两个 ViewModel 实例
    private val mainViewModel: MainViewModel by viewModels()
    private val updateViewModel: UpdateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 建立两个 ViewModel 之间的通信
        setupViewModelCommunication()
        enableEdgeToEdge()
        setContent {
            FastFillTheme {
                FastFillNavHost(
                    modifier = Modifier.fillMaxSize(),
                    mainVm = mainViewModel,
                    updateVm = updateViewModel
                )
            }
        }
    }

    private fun setupViewModelCommunication() {
        // 连接 MainViewModel 和 UpdateViewModel
        mainViewModel.setupUpdateListening(updateViewModel)

        // 触发更新检查
        updateViewModel.checkUpdate("SpeechlessMatt", "FastFill")
    }
}