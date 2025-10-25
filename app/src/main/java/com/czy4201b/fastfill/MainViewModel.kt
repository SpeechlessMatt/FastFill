package com.czy4201b.fastfill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.czy4201b.fastfill.feature.update.UpdateEvent
import com.czy4201b.fastfill.feature.update.UpdateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var navController: NavController? = null
    fun setNavController(ctrl: NavController) { navController = ctrl }

    // 不需要直接注入 UpdateViewModel，通过参数传递
    fun setupUpdateListening(updateViewModel: UpdateViewModel) {
        viewModelScope.launch {
            updateViewModel.events.collect { event ->
                when (event) {
                    is UpdateEvent.ShowUpdateDialog -> {
                        navController?.navigate("update_dialog/${event.info.version}")
                    }
                    is UpdateEvent.ShowError -> {
                        // 处理错误，比如显示 Snackbar
                        // 你可以通过另一个事件流将错误传回 UI
                    }
                    UpdateEvent.DismissDialog -> {
                        // 处理对话框关闭
                    }
                }
            }
        }
    }
}