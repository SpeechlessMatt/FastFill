package com.czy4201b.fastfill.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> BottomPicker(
    shouldShowImagePicker: Boolean,
    onResultNull: () -> Unit,
    onResult: (T) -> Unit,
    modifier: Modifier = Modifier
){
    if (shouldShowImagePicker) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable(
                    // 关掉点击动画
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        onResultNull()
                    }
                )
        )
    }

    AnimatedVisibility(
        visible = shouldShowImagePicker,
        enter = slideInVertically { fullHeight -> fullHeight }   // 从 fullHeight → 0
                + fadeIn(initialAlpha = 0.3f),                   // 淡入
        exit = slideOutVertically { fullHeight -> fullHeight }  // 从 0 → fullHeight
                + fadeOut()
    ) {

    }
}

