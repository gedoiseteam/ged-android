package com.upsaclay.authentication.presentation.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.RegistrationScreenState
import com.upsaclay.authentication.presentation.components.OutlinePasswordTextField
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorText
import com.upsaclay.common.presentation.components.OutlineTextField
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThirdRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val registrationState by registrationViewModel.screenState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading = registrationState == RegistrationScreenState.LOADING
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackBar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    val (errorMessage, inputsError) = when (registrationState) {
        RegistrationScreenState.UNRECOGNIZED_ACCOUNT -> stringResource(id = R.string.unrecognized_account) to true
        RegistrationScreenState.EMPTY_FIELDS_ERROR -> stringResource(id = com.upsaclay.common.R.string.empty_fields_error) to true
        RegistrationScreenState.EMAIL_FORMAT_ERROR -> stringResource(id = R.string.error_incorrect_email_format) to true
        RegistrationScreenState.PASSWORD_LENGTH_ERROR -> stringResource(id = R.string.error_password_length) to true
        RegistrationScreenState.USER_ALREADY_EXISTS -> stringResource(id = R.string.email_already_associated) to true
        RegistrationScreenState.USER_CREATION_ERROR -> stringResource(id = R.string.user_creation_error) to false
        RegistrationScreenState.UNKNOWN_ERROR -> stringResource(id = com.upsaclay.common.R.string.unknown_error) to false
        else -> null to false
    }

    LaunchedEffect(registrationState) {
        when (registrationState) {
            RegistrationScreenState.REGISTERED -> {
                registrationViewModel.resetScreenState()
                navController.navigate(Screen.EMAIL_VERIFICATION.route + "?email=${registrationViewModel.email}")
            }

            RegistrationScreenState.NETWORK_ERROR -> {
                showSnackBar(context.getString(com.upsaclay.common.R.string.unknown_network_error))
                registrationViewModel.resetScreenState()
            }

            RegistrationScreenState.SERVER_COMMUNICATION_ERROR -> {
                showSnackBar(context.getString(com.upsaclay.common.R.string.internal_server_error))
                registrationViewModel.resetScreenState()
            }

            else -> {}
        }
    }

    if (isLoading) {
        TopLinearLoadingScreen()
    }

    RegistrationTopBar(
        navController = navController,
        onBackClick = {
            keyboardController?.hide()
            focusManager.clearFocus()
            navController.popBackStack()
        },
        snackbarHostState = snackbarHostState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { focusManager.clearFocus() })
                }
        ) {
            Text(
                text = stringResource(id = R.string.enter_email_password),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlineTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.registration_screen_email_input_tag)),
                value = registrationViewModel.email,
                isError = inputsError,
                enabled = !isLoading,
                onValueChange = { registrationViewModel.updateEmail(it) },
                label = stringResource(com.upsaclay.common.R.string.email)
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlinePasswordTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.registration_screen_password_input_tag)),
                text = registrationViewModel.password,
                isError = inputsError,
                isEnable = !isLoading,
                onValueChange = { registrationViewModel.updatePassword(it) }
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                ErrorText(
                    modifier = Modifier.align(Alignment.Start),
                    text = errorMessage
                )
            }
        }

        PrimaryButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag(stringResource(R.string.registration_screen_next_button_tag)),
            isEnable = !isLoading,
            text = stringResource(id = com.upsaclay.common.R.string.next),
            onClick = {
                if (registrationViewModel.validateCredentialInputs()) {
                    registrationViewModel.resetScreenState()
                    registrationViewModel.register()
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
private fun ThirdRegistrationScreenPreview() {
    var mail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val isError = false
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    GedoiseTheme {
        if (isLoading) {
            TopLinearLoadingScreen()
        }

        RegistrationTopBar(navController = rememberNavController()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.enter_email_password),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlineTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = mail,
                    enabled = !isLoading,
                    isError = isError,
                    onValueChange = { mail = it },
                    label = stringResource(com.upsaclay.common.R.string.email)
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlinePasswordTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    text = password,
                    isEnable = !isLoading,
                    isError = isError,
                    onValueChange = { password = it }
                )
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = stringResource(id = com.upsaclay.common.R.string.next),
                isEnable = !isLoading,
                onClick = { isLoading = true }
            )
        }
    }
}