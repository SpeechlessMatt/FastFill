package com.czy4201b.fastfill.feature.update

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy4201b.fastfill.feature.update.data.UpdateInfo
import com.czy4201b.fastfill.feature.update.data.UpdateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 事件密封类
sealed class UpdateEvent {
    data class ShowUpdateDialog(val info: UpdateInfo) : UpdateEvent()
    data class ShowError(val message: String) : UpdateEvent()
    object DismissDialog : UpdateEvent()
}

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val updateRepository: UpdateRepository
) : ViewModel() {

    // 事件通道，用于发送一次性事件
    private val _events = MutableSharedFlow<UpdateEvent>()
    val events = _events.asSharedFlow()

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun checkUpdate(owner: String, repo: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("Update","start check login...")
                val info = updateRepository.getLatest(owner, repo)
                _updateInfo.value = info
            } catch (e: Exception) {
                Log.d("Update","fail: $e")
                _error.value = "检查更新失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}