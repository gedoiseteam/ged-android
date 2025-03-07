package com.upsaclay.authentication.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.RegistrationScreenState
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorText
import com.upsaclay.common.presentation.components.OutlineTextField
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun FirstRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val registrationState = registrationViewModel.screenState.collectAsState()
    val emptyFields = registrationState.value == RegistrationScreenState.EMPTY_FIELDS_ERROR
    val isLoading = registrationState.value == RegistrationScreenState.LOADING
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    RegistrationTopBar(
        navController = navController,
        onBackClick = {
            keyboardController?.hide()
            focusManager.clearFocus()
            navController.popBackStack()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(id = R.string.enter_first_last_name),
                style = MaterialTheme.typography.titleMedium
            )

            OutlineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = registrationViewModel.firstName,
                label = stringResource(com.upsaclay.common.R.string.first_name),
                onValueChange = registrationViewModel::updateFirstName,
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                isError = emptyFields,
                enabled = !isLoading
            )

            OutlineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = registrationViewModel.lastName,
                label = stringResource(com.upsaclay.common.R.string.last_name),
                onValueChange = registrationViewModel::updateLastName,
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                isError = emptyFields,
                enabled = !isLoading
            )

            if (emptyFields) {
                ErrorText(
                    modifier = Modifier.align(Alignment.Start),
                    text = stringResource(id = com.upsaclay.common.R.string.empty_fields_error)
                )
            }
        }

        PrimaryButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag(stringResource(R.string.registration_screen_next_button_tag)),
            text = stringResource(id = com.upsaclay.common.R.string.next),
            isEnable = !isLoading,
            onClick = {
                if (registrationViewModel.verifyNamesInputs()) {
                    registrationViewModel.resetScreenState()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    navController.navigate(Screen.SECOND_REGISTRATION.route)
                }
            }
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
private fun FirstRegistrationScreenPreview() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val isError = false
    val focusManager = LocalFocusManager.current

    GedoiseTheme {
        RegistrationTopBar(navController = rememberNavController()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            focusManager.clearFocus()
                        })
                    },
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Text(
                    text = stringResource(id = R.string.enter_first_last_name),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = firstName,
                    label = stringResource(com.upsaclay.common.R.string.first_name),
                    onValueChange = { firstName = it },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    isError = isError
                )

                OutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = lastName,
                    label = stringResource(com.upsaclay.common.R.string.last_name),
                    onValueChange = { lastName = it },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    isError = isError
                )

                if (isError) {
                    ErrorText(text = stringResource(id = com.upsaclay.common.R.string.empty_fields_error))
                }
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = stringResource(id = com.upsaclay.common.R.string.next),
                onClick = { }
            )
        }
    }
}