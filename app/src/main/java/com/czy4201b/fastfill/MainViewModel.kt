package com.czy4201b.fastfill

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.czy4201b.fastfill.core.navigation.Route
import com.czy4201b.fastfill.feature.update.UpdateEvent
import com.czy4201b.fastfill.feature.update.UpdateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var navController: NavController? = null

    private var isListening = false  // 防止重复监听

    fun setNavController(ctrl: NavController) { navController = ctrl }

    // 不需要直接注入 UpdateViewModel，通过参数传递
    fun setupUpdateListening(updateViewModel: UpdateViewModel) {
        if (isListening) return  // 防止重复调用
        isListening = true

        viewModelScope.launch {
            updateViewModel.events.collect { event ->
                when (event) {
                    is UpdateEvent.ShowUpdateDialog -> {
                        Log.d("Update", "ShowUpdateDialog!")
                        navController?.navigate(Route.UpdateDialog.route)
                    }

                    is UpdateEvent.ShowError -> TODO()
                }
            }
        }
    }
}