package com.czy4201b.fastfill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.czy4201b.fastfill.MainViewModel
import com.czy4201b.fastfill.feature.update.UpdateViewModel
import kotlinx.coroutines.delay

@Composable
fun FastFillApp() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val updateViewModel: UpdateViewModel = hiltViewModel()

    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        mainViewModel.setNavController(navController)
        mainViewModel.setupUpdateListening(updateViewModel)

        // 延迟检查更新
        delay(1000)
        updateViewModel.checkUpdate("SpeechlessMatt", "FastFill")
    }

    FastFillNavHost(
        updateVm = updateViewModel,
        navController = navController
    )
}