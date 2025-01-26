package com.upsaclay.authentication.presentation.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.RegistrationState
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorTextWithIcon
import com.upsaclay.common.presentation.components.OverlayLinearLoadingScreen
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun FirstRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val registrationState = registrationViewModel.registrationState.collectAsState()
    val isError = registrationState.value == RegistrationState.INPUTS_EMPTY_ERROR
    val isLoading = registrationState.value == RegistrationState.LOADING
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        registrationViewModel.resetRegistrationState()
        registrationViewModel.resetFirstName()
        registrationViewModel.resetLastName()
        registrationViewModel.resetEmail()
        registrationViewModel.resetPassword()
        registrationViewModel.resetSchoolLevel()
    }

    RegistrationTopBar(
        navController = navController
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                },
        ) {
            Spacer(Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(id = R.string.enter_first_last_name),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = registrationViewModel.lastName,
                isError = isError,
                enabled = !isLoading,
                placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.last_name)) },
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                onValueChange = { registrationViewModel.updateLastName(it) },
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = registrationViewModel.firstName,
                isError = isError,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.first_name)) },
                onValueChange = { registrationViewModel.updateFirstName(it) },
            )

            if (isError) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                ErrorTextWithIcon(
                    modifier = Modifier.align(Alignment.Start),
                    text = stringResource(id = com.upsaclay.common.R.string.empty_fields_error)
                )
            }
        }

        PrimaryButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            text = stringResource(id = com.upsaclay.common.R.string.next),
            isEnable = !isLoading,
            shape = MaterialTheme.shapes.small,
            onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                if (registrationViewModel.verifyNamesInputs()) {
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

@Preview
@Composable
private fun FirstRegistrationScreenPreview() {
    val firstName = ""
    val lastName = ""
    val isError = true
    var isLoading by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    GedoiseTheme {
        if(isLoading) {
            OverlayLinearLoadingScreen()
        }

        LaunchedEffect(isLoading) {
            if(isLoading) {
                delay(1000)
                isLoading = false
            }
        }

        RegistrationTopBar(
            navController = rememberNavController()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            focusManager.clearFocus()
                        })
                    },
            ) {
                Spacer(Modifier.height(MaterialTheme.spacing.large))

                Text(
                    text = stringResource(id = R.string.enter_first_last_name),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.last_name)) },
                    value = lastName,
                    isError = isError,
                    enabled = !isLoading,
                    onValueChange = { },
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = firstName,
                    isError = isError,
                    enabled = !isLoading,
                    placeholder = { Text(text = stringResource(id = com.upsaclay.common.R.string.first_name)) },
                    onValueChange = { },
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                    ErrorTextWithIcon(text = stringResource(id = com.upsaclay.common.R.string.empty_fields_error))
                }
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.small,
                isEnable = !isLoading,
                text = stringResource(id = com.upsaclay.common.R.string.next),
                onClick = { isLoading = true }
            )
        }
    }
}