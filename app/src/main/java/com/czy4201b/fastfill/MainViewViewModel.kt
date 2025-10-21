package com.czy4201b.fastfill

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy4201b.fastfill.javaScripts.FastFillJS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.forEach

data class MainViewUiState(
    val url: String = "",
    val loginMap: Map<FastFillJS, Boolean> = mutableStateMapOf(),
    val isShowLoginWeb: Boolean = false,
    val isUrlInvalid: Boolean = false,
    val isStartFilling: Boolean = false,
    val isShowTimeTable: Boolean = true,
)

class MainViewViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainViewUiState())
    val state: StateFlow<MainViewUiState> = _state.asStateFlow()

    fun showLoginWeb() {
        _state.update { state ->
            state.copy(
                isShowLoginWeb = true
            )
        }
    }

    fun closeLoginWeb() {
        _state.update { state ->
            state.copy(
                isShowLoginWeb = false
            )
        }
        checkAllLogin()
    }

    fun setShowTimeTable(isShow: Boolean) {
        Log.d("temp", "isShow: $isShow")
        _state.update { state ->
            state.copy(
                isShowTimeTable = isShow
            )
        }
    }

    fun startFilling(fastFillJS: FastFillJS) {
        if (checkUrlValid(fastFillJS)) {
            _state.update { state ->
                state.copy(
                    isStartFilling = true
                )
            }
        }
    }

    fun endFilling() {
        _state.update { state ->
            state.copy(
                isStartFilling = false
            )
        }
    }

    @Deprecated(message = "")
    fun initLogin(fastFillJS: FastFillJS) {
        _state.update { state ->
            val newLoginMap = state.loginMap.toMutableMap()
            newLoginMap[fastFillJS] = false
            state.copy(
                loginMap = newLoginMap
            )
        }
    }

    private fun checkUrlValid(fastFillJS: FastFillJS): Boolean {
        val isValid = _state.value.url.startsWith(fastFillJS.domain)
        _state.update { state ->
            state.copy(
                isUrlInvalid = !isValid
            )
        }
        Log.d("CheckUrl","valid: $isValid")
        return isValid
    }

    fun checkAllLogin() {
        viewModelScope.launch {
            val results = _state.value.loginMap.keys.map {
                async(Dispatchers.IO) {
                    it to kotlin.runCatching { it.checkLogin() }.getOrDefault(false)
                }
            }.awaitAll().toMap()

            _state.update { state ->
                state.copy(loginMap = results)
            }
        }
    }

    fun checkLogin(fastFillJS: FastFillJS) {
        viewModelScope.launch {
            val result = kotlin.runCatching { fastFillJS.checkLogin() }.getOrDefault(false)
            _state.update { state ->
                state.copy(
                    loginMap = state.loginMap + (fastFillJS to result)   // 生成新 Map
                )
            }
        }
    }

    fun updateUrl(url: String) {
        _state.update { state ->
            state.copy(url = url)
        }
    }

    fun clearUrl() {
        _state.update { state ->
            state.copy(url = "", isUrlInvalid = false)
        }
    }

    fun exitLogin(fastFillJS: FastFillJS) {
        fastFillJS.exitLogin()
        checkLogin(fastFillJS)
    }
}