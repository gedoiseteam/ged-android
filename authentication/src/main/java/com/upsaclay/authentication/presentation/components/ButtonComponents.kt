package com.upsaclay.authentication.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upsaclay.authentication.R
import com.upsaclay.common.presentation.components.CircularProgressBar
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing

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
fun GoogleLoginInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.height(45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.LightGray),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.width(MaterialTheme.spacing.large),
            painter = painterResource(R.drawable.g_logo),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

        Text(text = stringResource(id = R.string.continue_with_google))
    }
}

@Composable
private fun LoadingLargeButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { },
        enabled = false,
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.height(45.dp)
    ) {
        CircularProgressBar(
            color = Color.White,
            scale = 0.6f
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview
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

@Preview
@Composable
private fun GoogleLoginButtonPreview() {
    GedoiseTheme {
        GoogleLoginInButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        )
    }
}