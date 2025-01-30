package com.upsaclay.authentication.presentation.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.RegistrationState
import com.upsaclay.authentication.presentation.components.OutlinedEmailInput
import com.upsaclay.authentication.presentation.components.OutlinedPasswordInput
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorTextWithIcon
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThirdRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        registrationViewModel.resetRegistrationState()
        registrationViewModel.resetSchoolLevel()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val registrationState by registrationViewModel.registrationState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val isLoading = registrationState == RegistrationState.LOADING

    val inputsError = when (registrationState) {
        RegistrationState.UNRECOGNIZED_ACCOUNT, RegistrationState.INPUTS_EMPTY_ERROR -> true
        else -> false
    }

    val errorMessage = when (registrationState) {
        RegistrationState.UNRECOGNIZED_ACCOUNT -> stringResource(id = R.string.unrecognized_account)
        RegistrationState.INPUTS_EMPTY_ERROR -> stringResource(id = com.upsaclay.common.R.string.empty_fields_error)
        RegistrationState.EMAIL_FORMAT_ERROR -> stringResource(id = R.string.error_incorrect_email_format)
        RegistrationState.PASSWORD_LENGTH_ERROR -> stringResource(id = R.string.error_password_length)
        RegistrationState.USER_ALREADY_EXIST -> stringResource(id = R.string.email_already_associated)
        else -> null
    }

    LaunchedEffect(registrationState) {
        when (registrationState) {
            RegistrationState.USER_NOT_EXIST -> {
                registrationViewModel.register()
            }

            RegistrationState.REGISTERED -> {
                registrationViewModel.resetRegistrationState()
                navController.navigate(Screen.EMAIL_VERIFICATION.route + "?email=${registrationViewModel.email}") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }

            RegistrationState.ERROR -> showToast(
                context,
                com.upsaclay.common.R.string.unknown_error
            )

            else -> {}
        }
    }

    if (isLoading) {
        TopLinearLoadingScreen()
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
                }
        ) {
            Text(
                text = stringResource(id = R.string.enter_email_password),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlinedEmailInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                text = registrationViewModel.email,
                isError = inputsError,
                isEnable = !isLoading,
                onValueChange = { registrationViewModel.updateEmail(it) }
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            OutlinedPasswordInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                text = registrationViewModel.password,
                isError = inputsError,
                isEnable = !isLoading,
                onValueChange = { registrationViewModel.updatePassword(it) }
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                ErrorTextWithIcon(
                    modifier = Modifier.align(Alignment.Start),
                    text = errorMessage
                )
            }
        }

        PrimaryButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            isEnable = !isLoading,
            text = stringResource(id = com.upsaclay.common.R.string.next),
            onClick = {
                keyboardController?.hide()
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
    val mail = "pierre.dupont@universite-paris-saclay.fr"
    val password = "password"
    var isLoading by remember { mutableStateOf(false) }
    val isError = false
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    GedoiseTheme {
        if (isLoading) {
            TopLinearLoadingScreen()
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
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.enter_email_password),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlinedEmailInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    text = mail,
                    isEnable = !isLoading,
                    isError = isError,
                    onValueChange = {}
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlinedPasswordInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    text = password,
                    isEnable = !isLoading,
                    isError = isError,
                    onValueChange = {}
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