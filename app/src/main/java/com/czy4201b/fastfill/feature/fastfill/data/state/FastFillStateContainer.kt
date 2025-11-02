package com.czy4201b.fastfill.feature.fastfill.data.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy4201b.fastfill.feature.fastfill.data.repository.LoginRepository
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FastFillStateContainer @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _currentFastFillJS = MutableStateFlow<FastFillJS?>(null)

    // 暴露组合状态给所有观察者
    val configState: StateFlow<FastFillConfigState> = combine(
        loginRepository.loginState,
        _currentFastFillJS
    ) { loginMap, currentJS ->
        FastFillConfigState(
            currentFastFillJS = currentJS,
            isCurrentLogin = currentJS?.let { loginMap[it] } ?: false,
            allLoginMap = loginMap
        )
    }.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FastFillConfigState()
    )

    fun selectedFastFillJS(fastFillJS: FastFillJS) {
        _currentFastFillJS.value = fastFillJS
        refreshLoginState(fastFillJS)
    }

    fun refreshLoginState(fastFillJS: FastFillJS) {
        viewModelScope.launch {
            loginRepository.checkLogin(fastFillJS)
        }
    }

    fun refreshAllLoginState() {
        viewModelScope.launch {
            loginRepository.checkAllLogin()
        }
    }
}