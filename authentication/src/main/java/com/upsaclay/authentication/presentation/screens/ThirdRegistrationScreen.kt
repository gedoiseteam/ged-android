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
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.authentication.presentation.components.OutlinePasswordTextField
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorText
import com.upsaclay.common.presentation.components.OutlineTextField
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThirdRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorMessage by remember { mutableStateOf("") }
    val loading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackBar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    LaunchedEffect(registrationViewModel.event) {
        registrationViewModel.event.collectLatest { event ->
            when (event) {
                RegistrationEvent.Registered ->
                    navController.navigate(Screen.EMAIL_VERIFICATION.route + "?email=${registrationViewModel.email}")

                is RegistrationEvent.Error -> {
                    errorMessage = when (event.type) {
                        RegistrationErrorType.UNRECOGNIZED_ACCOUNT -> context.getString(R.string.unrecognized_account)
                        RegistrationErrorType.EMPTY_FIELDS_ERROR -> context.getString(com.upsaclay.common.R.string.empty_fields_error)
                        RegistrationErrorType.EMAIL_FORMAT_ERROR -> context.getString(R.string.error_incorrect_email_format)
                        RegistrationErrorType.PASSWORD_LENGTH_ERROR -> context.getString(R.string.error_password_length)
                        RegistrationErrorType.USER_ALREADY_EXISTS -> context.getString(R.string.email_already_associated)
                        RegistrationErrorType.USER_CREATION_ERROR -> context.getString(R.string.user_creation_error)
                        else -> ""
                    }

                    when (event.type) {
                        ErrorType.NetworkError -> showSnackBar(context.getString(com.upsaclay.common.R.string.unknown_network_error))
                        ErrorType.InternalServerError -> showSnackBar(context.getString(com.upsaclay.common.R.string.internal_server_error))
                        ErrorType.ServerConnectError -> showSnackBar(context.getString(com.upsaclay.common.R.string.server_communication_error))
                        ErrorType.UnknownError -> showSnackBar(context.getString(com.upsaclay.common.R.string.unknown_error))
                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    if (loading) {
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
                isError = errorMessage.isNotBlank(),
                enabled = !loading,
                onValueChange = { registrationViewModel.updateEmail(it) },
                label = stringResource(com.upsaclay.common.R.string.email)
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlinePasswordTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.registration_screen_password_input_tag)),
                text = registrationViewModel.password,
                isError = errorMessage.isNotBlank(),
                isEnable = !loading,
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
            isEnable = !loading,
            text = stringResource(id = com.upsaclay.common.R.string.next),
            onClick = {
                if (registrationViewModel.validateCredentialInputs()) {
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