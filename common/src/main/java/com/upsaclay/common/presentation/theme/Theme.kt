package com.upsaclay.common.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.upsaclay.common.presentation.theme.GedoiseColor.InputBackground
import com.upsaclay.common.presentation.theme.GedoiseColor.Primary
import com.upsaclay.common.presentation.theme.GedoiseColor.PrimaryVariant
import com.upsaclay.common.presentation.theme.GedoiseColor.Red
import com.upsaclay.common.presentation.theme.GedoiseColor.Secondary
import com.upsaclay.common.presentation.theme.GedoiseColor.Tertiary
import com.upsaclay.common.presentation.theme.GedoiseColor.White

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    primaryContainer = Tertiary,
    background = White,
    error = Red,
    surface = White,
    surfaceVariant = InputBackground,
    secondaryContainer = PrimaryVariant,
    surfaceContainerHigh = PrimaryVariant,
    outlineVariant = Color.LightGray
)

@Composable
fun GedoiseTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = when {
        darkTheme -> LightColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}