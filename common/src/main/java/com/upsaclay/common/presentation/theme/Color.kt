package com.upsaclay.common.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val white = Color(0xFFE6E6E6)
internal val black = Color(0xFF1D1D1D)
internal val lightGray = Color(0xFFCCCCCC)
internal val darkGray = Color(0xFF3C3C3C)

internal val primaryLight = Color(0xFF67708A)
internal val secondaryLight = Color(0xFF0B86FF)
internal val tertiaryLight = Color(0xFF009688)
internal val errorLight = Color(0xFFED5245)
internal val surfaceLight = Color(0xFFFBFCFF)
internal val surfaceVariantLight = Color(0xFFE0E4EC)
internal val onSurfaceVariantLight = Color(0xFF4F4F4F)
internal val inverseSurfaceLight = Color(0xFF303133)
internal val inverseOnSurfaceLight = Color(0xFFEFF1F4)
internal val onSurfaceLight = Color(0xFF1B1D1F)
internal val backgroundLight = Color(0xFFFFFFFF)
internal val onBackgroundLight = black
internal val outlineLight = Color(0xFF74777E)
internal val outlineVariantLight = Color(0xFFC4C8D0)

internal val primaryDark = Color(0xFF67708A)
internal val onPrimaryDark = Color(0xFF0E1B34)
internal val errorDark = Color(0xFFD64A4C)
internal val surfaceDark = Color(0xFF1B1C1F)
internal val surfaceVariantDark = Color(0xFF45484F)
internal val onSurfaceDark = Color(0xFFE1E3E6)
internal val onSurfaceVariantDark = Color(0xFFC4C7D0)
internal val inverseSurfaceDark = Color(0xFFE1E3E6)
internal val inverseOnSurfaceDark = Color(0xFF303133)
internal val onSecondaryContainerDark = white
internal val secondaryContainerDark = Color(0xFF444958)
internal val backgroundDark = Color(0xFF1B1C1F)
internal val onBackgroundDark = white
internal val outlineDark = Color(0xFF8F9299)
internal val outlineVariantDark = Color(0xFFC4C8D0)

val ColorScheme.chatInputBackground: Color
    @Composable
    get() = if(isSystemInDarkTheme()) Color(0xFF323232) else Color(0xFFEEEEEE)

val ColorScheme.chatInputForeground: Color
    @Composable
    get() = if(isSystemInDarkTheme()) Color(0xFF929298) else Color(0xFF646464)

val ColorScheme.previewText: Color
    @Composable
    get() = if(isSystemInDarkTheme()) Color(0xFFA1A4B0) else Color(0xFF6F7181)

val ColorScheme.black: Color
    @Composable
    get() = com.upsaclay.common.presentation.theme.black

val ColorScheme.white: Color
    @Composable
    get() = com.upsaclay.common.presentation.theme.white

val ColorScheme.lightGray: Color
    @Composable
    get() = com.upsaclay.common.presentation.theme.lightGray

val ColorScheme.darkGray: Color
    @Composable
    get() = com.upsaclay.common.presentation.theme.darkGray

object GedoiseColor {
    val LittleTransparentWhite = Color(0x66FFFFFF)
    val PrimaryVariant = Color(0xFFDBE0E9)
    val ProfilePictureErrorLight = Color(0xFFEBEDEE)
    val OnlineColor = Color(0xFF4ACB1B)
}