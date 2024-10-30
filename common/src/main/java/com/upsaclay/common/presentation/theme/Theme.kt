package com.upsaclay.common.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseColor.backgroundDark
import com.upsaclay.common.presentation.theme.GedoiseColor.backgroundLight
import com.upsaclay.common.presentation.theme.GedoiseColor.errorContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.errorContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.errorDark
import com.upsaclay.common.presentation.theme.GedoiseColor.errorLight
import com.upsaclay.common.presentation.theme.GedoiseColor.inverseOnSurfaceDark
import com.upsaclay.common.presentation.theme.GedoiseColor.inverseOnSurfaceLight
import com.upsaclay.common.presentation.theme.GedoiseColor.inversePrimaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.inversePrimaryLight
import com.upsaclay.common.presentation.theme.GedoiseColor.inverseSurfaceDark
import com.upsaclay.common.presentation.theme.GedoiseColor.inverseSurfaceLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onBackgroundDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onBackgroundLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onErrorContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onErrorContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onErrorDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onErrorLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onPrimaryContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onPrimaryContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onPrimaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onPrimaryLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onSecondaryContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onSecondaryContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onSecondaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onSecondaryLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onSurfaceDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onSurfaceLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onSurfaceVariantDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onSurfaceVariantLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onTertiaryContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onTertiaryContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.onTertiaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.onTertiaryLight
import com.upsaclay.common.presentation.theme.GedoiseColor.outlineDark
import com.upsaclay.common.presentation.theme.GedoiseColor.outlineLight
import com.upsaclay.common.presentation.theme.GedoiseColor.outlineVariantDark
import com.upsaclay.common.presentation.theme.GedoiseColor.outlineVariantLight
import com.upsaclay.common.presentation.theme.GedoiseColor.primaryContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.primaryContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.primaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.primaryLight
import com.upsaclay.common.presentation.theme.GedoiseColor.scrimDark
import com.upsaclay.common.presentation.theme.GedoiseColor.scrimLight
import com.upsaclay.common.presentation.theme.GedoiseColor.secondaryContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.secondaryContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.secondaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.secondaryLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceBrightDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceBrightLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerHighDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerHighLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerHighestDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerHighestLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerLowDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerLowLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerLowestDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceContainerLowestLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceDimDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceDimLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceLight
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceVariantDark
import com.upsaclay.common.presentation.theme.GedoiseColor.surfaceVariantLight
import com.upsaclay.common.presentation.theme.GedoiseColor.tertiaryContainerDark
import com.upsaclay.common.presentation.theme.GedoiseColor.tertiaryContainerLight
import com.upsaclay.common.presentation.theme.GedoiseColor.tertiaryDark
import com.upsaclay.common.presentation.theme.GedoiseColor.tertiaryLight

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)
private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun GedoiseTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) darkScheme else lightScheme
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