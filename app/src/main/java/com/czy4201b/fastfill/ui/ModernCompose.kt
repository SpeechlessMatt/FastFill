package com.czy4201b.fastfill.ui

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ModernFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = if (enabled) 2.dp else 0.dp,
                shape = RoundedCornerShape(8.dp)
            ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF212121),
            disabledContainerColor = Color(0xFFE0E0E0),
            contentColor = Color.White,
            disabledContentColor = Color(0xFF9E9E9E)
        ),
        shape = RoundedCornerShape(8.dp),
        content = content
    )
}

@Composable
fun ModernOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFFFFF),
            disabledContainerColor = Color(0xFFFAFAFA),
            contentColor = Color(0xFF424242),
            disabledContentColor = Color(0xFFBDBDBD)
        ),
        shape = RoundedCornerShape(8.dp),
        content = content,
        border = if (enabled) BorderStroke(
            width = 1.dp,
            color = Color(0xFFBDBDBD)
        ) else BorderStroke(width = 1.dp, color = Color(0xFFEEEEEE))
    )
}

/**
 * 纯黑白主题开关键
 * 轨道 44×24、圆角 12、内边距 2、颜色 #E0E0E0/#424242
 * 拇指 20、白色、阴影 2、按压 22、禁用 #F5F5F5/#FAFAFA
 * 聚焦外框 1dp/#212121、间距 4、动画 200ms、涟漪
 */
@Composable
fun ModernSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val density = LocalDensity.current
    // 尺寸全部按 dp 转 px
    val trackWidth = with(density) { 44.dp.toPx() }
    val trackHeight = with(density) { 24.dp.toPx() }
    val thumbDiameter = with(density) { 20.dp.toPx() }
    val thumbRadius = thumbDiameter / 2
    val padding = with(density) { 2.dp.toPx() }
    val focusStroke = with(density) { 1.dp.toPx() }
    val focusGap = with(density) { 4.dp.toPx() }

    // 颜色
    val trackOff = Color(0xFFE0E0E0)
    val trackOn = Color(0xFF424242)
    val thumbNorm = Color.White
    val trackDisabled = Color(0xFFF5F5F5)
    val thumbDisabled = Color(0xFFFAFAFA)

    // 动画
    val transition = updateTransition(checked, label = "switch")
    val thumbOffset by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200, easing = FastOutSlowInEasing) },
        label = "offset"
    ) { if (it) trackWidth - thumbDiameter - padding else padding }

    // 按压放大
    var pressed by remember { mutableStateOf(false) }
    val thumbDp by animateFloatAsState(
        targetValue = if (pressed) 22.dp.value else 20.dp.value,
        animationSpec = tween(150), label = "size"
    )

    val semantics = Modifier.semantics {
        role = Role.Switch
        stateDescription = if (checked) "开启" else "关闭"
        if (!enabled) disabled()
    }

    val onClickable = Modifier.pointerInput(checked) {
        if (enabled) {
            detectTapGestures(
                onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                },
                onTap = {
                    onCheckedChange(!checked)
                }
            )
        }
    }

    Canvas(
        modifier
            .size(44.dp, 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(onClickable)
            .then(semantics)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 聚焦外框
        if (enabled) {
            drawRoundRect(
                color = Color(0xFF212121),
                size = Size(canvasWidth + focusGap * 2, canvasHeight + focusGap * 2),
                cornerRadius = CornerRadius(trackHeight / 2 + focusGap),
                style = Stroke(width = focusStroke),
                topLeft = Offset(-focusGap, -focusGap)
            )
        }

        // 轨道
        val trackColor = when {
            !enabled -> trackDisabled
            checked -> trackOn
            else -> trackOff
        }
        drawRoundRect(
            color = trackColor,
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = CornerRadius(trackHeight / 2)
        )

        // 拇指
        val thumbColor = if (enabled) thumbNorm else thumbDisabled

        drawCircle(
            color = thumbColor,
            radius = thumbDp * density.density / 2,
            center = Offset(
                thumbOffset + thumbRadius,
                canvasHeight / 2
            ),
        )
    }
}

@Preview
@Composable
fun ModernPreview() {
    ModernSwitch(false, {})
}