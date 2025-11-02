package com.czy4201b.fastfill.feature.fastfill.ui

import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS

data class MainViewUiState(
    val currentFastFillJS: FastFillJS? = null,
    val isCurrentLogin: Boolean = false,
    val allLoginMap: Map<FastFillJS, Boolean> = emptyMap(),
    val url: String = "",
    val currentTab: Int = 0,
    val isShowLoginWeb: Boolean = false,
    val isUrlInvalid: Boolean = false,
    val isStartFilling: Boolean = false,
)