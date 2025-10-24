package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.compose.runtime.mutableStateMapOf
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS

data class MainViewUiState(
    val url: String = "",
    val loginMap: Map<FastFillJS, Boolean> = mutableStateMapOf(),
    val currentTab: Int = 0,
    val isShowLoginWeb: Boolean = false,
    val isUrlInvalid: Boolean = false,
    val isStartFilling: Boolean = false,
)