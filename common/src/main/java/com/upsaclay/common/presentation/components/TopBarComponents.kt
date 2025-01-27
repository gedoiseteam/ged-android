package com.upsaclay.common.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing

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
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                icon()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopBarEdit(
    title: String = "",
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    isButtonEnable: Boolean = true,
    confirmText: String = stringResource(id = com.upsaclay.common.R.string.save)
) {
    TopAppBar(
        title = { Text(text = title) },
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
                enabled = isButtonEnable,
                contentPadding = PaddingValues(
                    vertical = MaterialTheme.spacing.default,
                    horizontal = MaterialTheme.spacing.smallMedium
                ),
                onClick = onSaveClick
            ) {
                Text(text = confirmText)
            }
        }
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

@Preview
@Composable
private fun SmallTopBarEditPreview() {
    GedoiseTheme {
        SmallTopBarEdit(
            title = "Title",
            onCancelClick = { },
            onSaveClick = { }
        )
    }
}