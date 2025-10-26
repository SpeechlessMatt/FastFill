package com.czy4201b.fastfill.navigation

import android.util.Log
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
import com.czy4201b.fastfill.core.navigation.Route
import com.czy4201b.fastfill.feature.fastfill.ui.MainView
import com.czy4201b.fastfill.feature.update.UpdateViewModel
import com.czy4201b.fastfill.feature.update.ui.UpdateDialog

@Composable
fun FastFillNavHost(
    modifier: Modifier = Modifier,
    mainVm: MainViewModel,
    updateVm: UpdateViewModel,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Route.Main.route
    ) {
        composable(Route.Main.route) {
            MainView(
                modifier = Modifier,
                userFillTableViewModel = viewModel(),
                timeSettingsViewModel = viewModel(),
                vm = viewModel()
            )
        }

        dialog(Route.UpdateDialog.route) { backStackEntry ->
            Log.d("Update", "route to dialog now")
            // 使用传递进来的 updateVm，而不是新建
            UpdateDialog(updateVm) {
                navController.popBackStack()
            }
        }
    }
}