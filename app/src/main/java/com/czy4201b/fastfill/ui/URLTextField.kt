package com.czy4201b.fastfill.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse

@Composable
fun URLTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "请输入网址",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    maxLines: Int = 1,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    textStyle: TextStyle = LocalTextStyle.current,
    urlIcon: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    // If color is not provided via the text style, use content color as a default
    val fontSize = textStyle.fontSize.takeOrElse { 12.sp }
    val fontColor = textStyle.color.takeOrElse { Color(0xFF212121) }
    val mergedTextStyle = textStyle.merge(TextStyle(fontSize = fontSize, color = fontColor))

    Surface(
        modifier = modifier,
        color = if (enabled) Color(0xFFFFFFFF)
        else Color(0xFFF5F5F5),
        shape = RoundedCornerShape(8.dp),
        border = if (isError) BorderStroke(width = 1.dp, color = Color(0xFFFF5252))
        else if (isFocused) BorderStroke(width = 1.dp, color = Color(0xFF757575))
        else BorderStroke(width = 1.dp, color = Color(0xFFE0E0E0)),
        shadowElevation = 0.5.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            urlIcon?.invoke()
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
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
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Box(
                            modifier = Modifier.alpha(if (value.isEmpty()) 1f else 0f)
                        ) {
                            Text(text = label, color = Color(0xFF9E9E9E))
                        }
                        innerTextField()
                    }
                }
            )
        }

    }
}