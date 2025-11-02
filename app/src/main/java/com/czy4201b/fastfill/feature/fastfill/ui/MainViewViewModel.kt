package com.czy4201b.fastfill.feature.fastfill.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy4201b.fastfill.feature.fastfill.data.repository.LoginRepository
import com.czy4201b.fastfill.feature.fastfill.data.state.FastFillConfigState
import com.czy4201b.fastfill.feature.fastfill.data.state.FastFillStateContainer
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS
import com.czy4201b.fastfill.feature.fastfill.javaScripts.impl.TxDocFill
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.String

@HiltViewModel
class MainViewViewModel @Inject constructor(
    private val stateContainer: FastFillStateContainer
) : ViewModel() {

    // 在同一个文件中定义相关的数据类
    private data class MainViewLocalState(
        val isLoading: Boolean = true,
        val url: String = "",
        val currentTab: Int = 0,
        val isShowLoginWeb: Boolean = false,
        val isUrlInvalid: Boolean = false,
        val isStartFilling: Boolean = false,
    )

    private val _localState = MutableStateFlow(MainViewLocalState())

    val state: StateFlow<MainViewUiState> = combine(
        stateContainer.configState,
        _localState
    ) { configState, localState ->
        MainViewUiState(
            currentFastFillJS = configState.currentFastFillJS,
            isCurrentLogin = configState.isCurrentLogin,
            allLoginMap = configState.allLoginMap,
            isLoading = localState.isLoading,
            url = localState.url,
            currentTab = localState.currentTab,
            isShowLoginWeb = localState.isShowLoginWeb,
            isUrlInvalid = localState.isUrlInvalid,
            isStartFilling = localState.isStartFilling,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainViewUiState()
    )

    // 暂时写法 为后续完成数据库做准备
    init {
        selectFastFillJS(TxDocFill)
    }

    private fun checkUrlValid(): Boolean =
        state.value.currentFastFillJS?.let { fastFillJS ->
            val isValid = _localState.value.url.startsWith(fastFillJS.domain)
            _localState.update { state ->
                state.copy(
                    isUrlInvalid = !isValid
                )
            }
            Log.d("CheckUrl", "valid: $isValid")
            isValid
        } ?: false

    private fun checkAllLogin() {
        stateContainer.refreshAllLoginState()
    }

    private fun checkLogin(fastFillJS: FastFillJS) {
        stateContainer.refreshLoginState(fastFillJS)
    }

    private fun exitLogin(fastFillJS: FastFillJS) {
        fastFillJS.exitLogin()
        checkLogin(fastFillJS)
    }

    /* ---------UI部分--------- */

    fun selectFastFillJS(fastFillJS: FastFillJS) {
        stateContainer.selectedFastFillJS(fastFillJS)
    }

    fun showLoginWeb() {
        _localState.update { state ->
            state.copy(
                isShowLoginWeb = true
            )
        }
    }

    fun closeLoginWeb() {
        _localState.update { state ->
            state.copy(
                isShowLoginWeb = false
            )
        }
        checkAllLogin()
    }

    fun selectTab(index: Int) {
        _localState.update { state ->
            state.copy(
                currentTab = index
            )
        }
    }

    fun startFilling() {
        if (checkUrlValid()) {
            _localState.update { state ->
                state.copy(
                    isStartFilling = true
                )
            }
        }
    }

    fun endFilling() {
        _localState.update { state ->
            state.copy(
                isStartFilling = false
            )
        }
    }

    fun updateUrl(url: String) {
        _localState.update { state ->
            state.copy(url = url)
        }
    }

    fun clearUrl() {
        _localState.update { state ->
            state.copy(url = "", isUrlInvalid = false)
        }
    }
}