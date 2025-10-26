package com.czy4201b.fastfill.feature.update

import android.app.Application
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
}

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val updateRepository: UpdateRepository,
    private val application: Application
) : ViewModel() {

    // 事件通道，用于发送一次性事件
    private val _events = MutableSharedFlow<UpdateEvent>()
    val events = _events.asSharedFlow()

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo

    private val _hasUpdate = MutableStateFlow<Boolean>(false)
    val hasUpdate: StateFlow<Boolean> = _hasUpdate

    fun checkUpdate(owner: String, repo: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("Update", "start check login...")
                val info = updateRepository.getLatest(owner, repo)
                _updateInfo.value = info
                val canUpdate = isUpdateAvailable(
                    currentVersion = getCurrentAppVersion(),
                    latestVersion = info.version
                )
                if (canUpdate){
                    Log.d("Update", "发现新版本!")
                    _hasUpdate.value = true
                    _events.emit(UpdateEvent.ShowUpdateDialog(info))
                } else {
                    Log.d("Update", "未发现新版本")
                }

            } catch (e: Exception) {
                Log.d("Update", "fail: $e")
            }
        }
    }

    private fun isUpdateAvailable(currentVersion: String, latestVersion: String): Boolean {
        // 清理版本号（移除 "v" 前缀等非数字字符）
        val cleanCurrent = currentVersion.replace("^[vV]".toRegex(), "").trim()
        val cleanLatest = latestVersion.replace("^[vV]".toRegex(), "").trim()

        Log.d("Update", "清理后版本: '$cleanCurrent' vs '$cleanLatest'")

        val currentParts = cleanCurrent.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = cleanLatest.split(".").map { it.toIntOrNull() ?: 0 }

        // 比较每个版本号部分
        val maxLength = maxOf(currentParts.size, latestParts.size)

        for (i in 0 until maxLength) {
            val currentPart = currentParts.getOrElse(i) { 0 }
            val latestPart = latestParts.getOrElse(i) { 0 }
            // 有一个部分大于就可以说要更新了
            if (currentPart < latestPart) return true
        }
        return false
    }

    private fun getCurrentAppVersion(): String {
        return try {
            val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            Log.e("Update", "获取当前版本失败: $e")
            "1.0.0"
        }
    }
}