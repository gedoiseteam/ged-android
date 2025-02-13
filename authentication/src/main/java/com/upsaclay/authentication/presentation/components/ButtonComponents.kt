package com.upsaclay.authentication.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upsaclay.common.presentation.components.CircularProgressBar
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    if (isLoading) {
        LoadingLargeButton(modifier = modifier)
    } else {
        PrimaryButton(
            text = text,
            onClick = onClick,
            modifier = modifier
        )
    }
}

@Composable
private fun LoadingLargeButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { },
        enabled = false,
        colors = ButtonColors(
            contentColor = GedoiseColor.White,
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.height(45.dp)
    ) {
        CircularProgressBar(
            color = GedoiseColor.White,
            scale = 0.6f
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginButtonPreview() {
    var isLoading by remember { mutableStateOf(false) }

    GedoiseTheme {
        LoginButton(
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
            text = "Se connecter",
            onClick = { isLoading = !isLoading }
        )
    }
}