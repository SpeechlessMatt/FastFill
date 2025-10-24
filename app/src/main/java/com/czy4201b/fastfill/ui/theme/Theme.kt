package com.czy4201b.fastfill.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AlmostBlack,
    onPrimary = White,

    secondary = MediumGray,
    onSecondary = White,

    tertiary = LightGray,
    onTertiary = AlmostBlack,

    background = AlmostWhiteBg,
    onBackground = AlmostBlack,

    surface = PureWhite,
    onSurface = AlmostBlack,

    surfaceVariant = SurfaceGray,
    onSurfaceVariant = MediumGray,

    error = SoftRed,
    onError = White,

    outline = DividerGray,

    primaryContainer = DarkGrayFocus,
    onPrimaryContainer = White
)

private val DarkColorScheme = darkColorScheme(
    primary = AlmostWhite,
    onPrimary = AlmostBlackBg,

    secondary = Gray500,
    onSecondary = AlmostBlackBg,

    tertiary = Gray700,
    onTertiary = AlmostWhite,

    background = AlmostBlackBg,
    onBackground = AlmostWhite,

    surface = SurfaceDark,
    onSurface = AlmostWhite,

    surfaceVariant = SurfaceDark2,
    onSurfaceVariant = Gray500,

    error = SoftRedDark,
    onError = AlmostBlackBg,

    outline = Gray700,

    primaryContainer = Gray500,
    onPrimaryContainer = AlmostBlackBg
)

@Composable
fun FastFillTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}