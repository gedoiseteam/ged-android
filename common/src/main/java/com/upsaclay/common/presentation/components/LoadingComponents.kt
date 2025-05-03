package com.upsaclay.common.presentation.components

import androidx.compose.foundation.background
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
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.overlay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OverlayCircularLoadingScreen(scale: Float = 2.5f) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f)
            .background(MaterialTheme.colorScheme.overlay)
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f)
            .background(MaterialTheme.colorScheme.overlay)
            .pointerInteropFilter { true }
    ) {
        LinearProgressBar(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(),
        )
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