package com.upsaclay.authentication.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.upsaclay.common.presentation.components.ErrorTextWithIcon
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import org.koin.androidx.compose.koinViewModel

private const val CURRENT_STEP = 1

@Composable
fun FirstRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val registrationState = registrationViewModel.screenState.collectAsState()
    val emptyFields = registrationState.value == RegistrationScreenState.EMPTY_FIELDS_ERROR
    val isLoading = registrationState.value == RegistrationScreenState.LOADING
    val focusManager = LocalFocusManager.current

    RegistrationTopBar(
        navController = navController,
        currentStep = CURRENT_STEP
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

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = registrationViewModel.firstName,
                isError = emptyFields,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.first_name)) },
                onValueChange = { registrationViewModel.updateFirstName(it) },
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = registrationViewModel.lastName,
                isError = emptyFields,
                enabled = !isLoading,
                placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.last_name)) },
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                onValueChange = { registrationViewModel.updateLastName(it) },
            )

            if (emptyFields) {
                ErrorTextWithIcon(
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
        RegistrationTopBar(
            navController = rememberNavController(),
            currentStep = 1
        ) {
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

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.last_name)) },
                    value = lastName,
                    isError = isError,
                    onValueChange = { lastName = it }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = firstName,
                    isError = isError,
                    placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.first_name)) },
                    onValueChange = { firstName = it }
                )

                if (isError) {
                    ErrorTextWithIcon(text = stringResource(id = com.upsaclay.common.R.string.empty_fields_error))
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