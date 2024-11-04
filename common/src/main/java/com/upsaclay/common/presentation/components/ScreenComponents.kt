package com.upsaclay.common.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OverlayCircularLoadingScreen(
    scale: Float = 2.5f
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .zIndex(1000f)
            .pointerInteropFilter { true }
    ) {
        CircularProgressBar(
            modifier = Modifier.align(Alignment.Center),
            scale = scale
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OverlayLinearLoadingScreen() {
    val backgroundColor = if (isSystemInDarkTheme()) GedoiseColor.backgroundDark else GedoiseColor.backgroundLight
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .zIndex(1000f)
            .pointerInteropFilter { true }
    ) {
        LinearProgressBar(
            modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(),
        )
    }
}

@Composable
fun TopLinearLoadingScreen() {
    Box(modifier = Modifier.fillMaxSize().zIndex(1000f)) {
        LinearProgressBar(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart))
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    GedoiseTheme {
        OverlayCircularLoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun OverlayLinearLoadingScreenPreview() {
    GedoiseTheme {
        OverlayLinearLoadingScreen()
    }
}