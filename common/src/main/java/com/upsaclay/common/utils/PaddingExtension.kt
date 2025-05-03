package com.upsaclay.common.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.upsaclay.common.presentation.theme.spacing

@Composable
fun Modifier.mediumPadding(topPadding: PaddingValues): Modifier {
    return this.padding(
        top = topPadding.calculateTopPadding(),
        start = MaterialTheme.spacing.medium,
        end = MaterialTheme.spacing.medium,
        bottom = MaterialTheme.spacing.medium
    )
}

@Composable
fun Modifier.mediumPadding(): Modifier {
    return this.padding(
        start = MaterialTheme.spacing.medium,
        end = MaterialTheme.spacing.medium,
        bottom = MaterialTheme.spacing.medium
    )
}