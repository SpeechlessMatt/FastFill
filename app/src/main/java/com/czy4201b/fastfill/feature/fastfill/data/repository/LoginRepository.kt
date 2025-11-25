package com.czy4201b.fastfill.feature.fastfill.data.repository

import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class LoginRepository @Inject constructor() {
    private val _loginState = MutableStateFlow<Map<FastFillJS, Boolean>>(emptyMap())
    val loginState = _loginState.asStateFlow()

    private fun registerFastFillJS(fastFillJS: FastFillJS) {
        _loginState.update { map ->
            if (map.containsKey(fastFillJS)) map // 已存在，不重复添加
            else map + (fastFillJS to false)
        }
    }

    suspend fun checkAllLogin() {
        _loginState.update { map ->
            map.mapValues { (js, _) ->
                try {
                    js.checkLogin()
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }
    }

    suspend fun checkLogin(fastFillJS: FastFillJS) {
        registerFastFillJS(fastFillJS)
        try {
            val result = fastFillJS.checkLogin()
            _loginState.update { map ->
                map + (fastFillJS to result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    suspend fun logout(fastFillJS: FastFillJS) {
        checkLogin(fastFillJS)
    }
}