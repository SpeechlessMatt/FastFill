package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse

@Composable
fun TableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    maxLines: Int = 1,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    // If color is not provided via the text style, use content color as a default
    val fontSize = textStyle.fontSize.takeOrElse { 12.sp }
    val fontColor = textStyle.color.takeOrElse { MaterialTheme.colorScheme.onSurface }
    val mergedTextStyle = textStyle.merge(TextStyle(fontSize = fontSize, color = fontColor))

    Surface(
        modifier = modifier,
        color = if (enabled) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = if (isError) BorderStroke(width = 1.5.dp, color = MaterialTheme.colorScheme.error)
        else if (isFocused) BorderStroke(width = 1.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        else null,
        shadowElevation = 0.5.dp
    ) {
        BasicTextField(
            modifier = Modifier
                .padding(contentPadding)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(fontColor),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Box(
                        modifier = Modifier.alpha(if (value.isEmpty()) 1f else 0f)
                    ) {
                        placeholder?.invoke()
                    }
                    innerTextField()
                }
            }
        )
    }
}