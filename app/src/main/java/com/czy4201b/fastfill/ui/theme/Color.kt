package com.czy4201b.fastfill.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

// ==================== 亮色调色板 ====================
val AlmostBlack   = Color(0xFF212121)
val White         = Color(0xFFFFFFFF)
val MediumGray    = Color(0xFF757575)
val LightGray     = Color(0xFFF5F5F5)
val AlmostWhiteBg = Color(0xFFFAFAFA)
val PureWhite     = Color(0xFFFFFFFF)
val SurfaceGray   = Color(0xFFEEEEEE)
val SoftRed       = Color(0xFFBA1A1A)
val DividerGray   = Color(0xFFE0E0E0)
val DarkGrayFocus = Color(0xFF424242)

// ==================== 暗色调色板 ====================
val AlmostWhite   = Color(0xFFE0E0E0)
val AlmostBlackBg = Color(0xFF121212)
val Gray500       = Color(0xFF9E9E9E)
val Gray700       = Color(0xFF424242)
val SurfaceDark   = Color(0xFF1E1E1E)
val SurfaceDark2  = Color(0xFF2D2D2D)
val SoftRedDark   = Color(0xFFFF5252)

// 在颜色方案中添加自定义颜色
val LightCustomBackground = Color(0xFFF8F9FA)
val DarkCustomBackground = Color(0xFF252525)

// ==================== 玻璃调色板 ====================
// 浅色主题磨砂玻璃
val LightHazeStyle = HazeStyle(
    backgroundColor = Color.White.copy(alpha = 0.20f),    // 保持白色底板
    tint = HazeTint(Color.White.copy(alpha = 0.15f)),     // 白色雾效
    blurRadius = 20.dp,
    noiseFactor = 0.05f
)

// 深色主题磨砂玻璃
val DarkHazeStyle = HazeStyle(
    backgroundColor = Color.Black.copy(alpha = 0.15f),    // 改为黑色底板
    tint = HazeTint(Color.Black.copy(alpha = 0.10f)),     // 黑色雾效
    blurRadius = 20.dp,
    noiseFactor = 0.08f                                   // 深色下颗粒感可以稍强
)
