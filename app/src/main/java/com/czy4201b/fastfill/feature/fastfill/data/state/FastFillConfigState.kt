package com.czy4201b.fastfill.feature.fastfill.data.state

import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS

data class FastFillConfigState (
    val currentFastFillJS: FastFillJS? = null,
    val isCurrentLogin: Boolean = false,
    val allLoginMap: Map<FastFillJS, Boolean> = emptyMap()
)