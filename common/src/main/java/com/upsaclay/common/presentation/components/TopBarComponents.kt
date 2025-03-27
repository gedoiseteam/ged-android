package com.upsaclay.common.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.presentation.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopBarBack(
    onBackClick: () -> Unit,
    title: String,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(id = com.upsaclay.common.R.string.arrow_back_icon_description)
        )
    }
) {
    TopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                icon()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopBarAction(
    modifier: Modifier = Modifier,
    title: String = "",
    onCancelClick: () -> Unit,
    onActionClick: () -> Unit,
    isButtonEnable: Boolean = true,
    buttonText: String
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onCancelClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        },
        actions = {
            Button(
                modifier = Modifier.padding(end = MaterialTheme.spacing.small),
                enabled = isButtonEnable,
                contentPadding = PaddingValues(
                    vertical = MaterialTheme.spacing.default,
                    horizontal = MaterialTheme.spacing.smallMedium
                ),
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.white),
                onClick = onActionClick
            ) {
                Text(text = buttonText)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview
@Composable
private fun SmallTopBarBackPreview() {
    GedoiseTheme {
        SmallTopBarBack(
            onBackClick = {},
            title = "Title"
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SmallTopBarActionPreview() {
    GedoiseTheme {
        SmallTopBarAction(
            title = "Title",
            onCancelClick = { },
            onActionClick = { },
            buttonText = "Enregister"
        )
    }
}