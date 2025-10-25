package com.czy4201b.fastfill.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.czy4201b.fastfill.MainViewModel
import com.czy4201b.fastfill.feature.fastfill.ui.MainView
import com.czy4201b.fastfill.feature.update.UpdateViewModel
import com.czy4201b.fastfill.feature.update.ui.UpdateDialog

@Composable
fun FastFillNavHost(
    modifier: Modifier = Modifier,
    mainVm: MainViewModel,
    updateVm: UpdateViewModel,
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(Unit) {
        mainVm.setNavController(navController)
        mainVm.setupUpdateListening(updateVm) // 设置监听
    }

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = "main"
    ) {
        composable("main") {
            MainView(
                modifier = Modifier,
                userFillTableViewModel = viewModel(),
                timeSettingsViewModel = viewModel(),
                vm = viewModel()
            )
        }

        dialog("update_dialog") { backStackEntry ->
            // 使用传递进来的 updateVm，而不是新建
            UpdateDialog(updateVm)
        }
    }
}