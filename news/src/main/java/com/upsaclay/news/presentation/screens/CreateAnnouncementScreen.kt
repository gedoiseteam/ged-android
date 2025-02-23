package com.upsaclay.news.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.common.presentation.components.SmallTopBarAction
import com.upsaclay.common.presentation.components.TransparentFocusedTextField
import com.upsaclay.common.presentation.components.TransparentTextField
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.news.R
import com.upsaclay.news.presentation.viewmodels.CreateAnnouncementViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateAnnouncementScreen(
    navController: NavController,
    createAnnouncementViewModel: CreateAnnouncementViewModel = koinViewModel()
) {
    val title = createAnnouncementViewModel.title
    val content = createAnnouncementViewModel.content
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            SmallTopBarAction(
                modifier = Modifier.fillMaxWidth(),
                buttonText = stringResource(id = com.upsaclay.common.R.string.publish),
                onCancelClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    navController.popBackStack()
                },
                onActionClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    createAnnouncementViewModel.createAnnouncement()
                    navController.popBackStack()
                },
                isButtonEnable = content.isNotBlank()
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
                .fillMaxSize()
        ) {
            Column {
                TransparentFocusedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_field_entry),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                        )
                    },
                    onValueChange = { createAnnouncementViewModel.updateTitle(it) },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                    )
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                TransparentTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = content,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.content_field_entry),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onValueChange = { createAnnouncementViewModel.updateContent(it) },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateAnnouncementScreenPreview() {
    val title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    GedoiseTheme {
        Scaffold(
            topBar = {
                SmallTopBarAction(
                    onCancelClick = { },
                    onActionClick = { },
                    buttonText = stringResource(id = com.upsaclay.common.R.string.publish),
                    isButtonEnable = content.isNotBlank()
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.medium
                    )
                    .fillMaxSize()
            ) {
                TransparentFocusedTextField(
                    value = title,
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_field_entry),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                        )
                    },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.2f
                    )
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                TransparentTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.content_field_entry),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}