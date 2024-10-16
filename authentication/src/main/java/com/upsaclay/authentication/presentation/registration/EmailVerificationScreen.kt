package com.upsaclay.authentication.presentation.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.model.RegistrationState
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.components.ErrorText
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val registrationState by registrationViewModel.registrationState.collectAsState()
    var errorMessage by remember { mutableStateOf("") }
    val isLoading = registrationState == RegistrationState.LOADING
    val context = LocalContext.current
    val text = stringResource(id = R.string.email_verification_text, registrationViewModel.email)
    var isForwardEmailButtonEnable by remember { mutableStateOf(true) }
    var isForwardButtonClicked by remember { mutableStateOf(false) }

    errorMessage = when(registrationState) {
        RegistrationState.EMAIL_NOT_VERIFIED -> stringResource(id = R.string.email_not_verified)
        else -> ""
    }

    LaunchedEffect(Unit) {
        registrationViewModel.resetRegistrationState()
        registrationViewModel.getCurrentUserIfNeeded()
    }

    LaunchedEffect(isForwardButtonClicked) {
        isForwardEmailButtonEnable = false
        delay(60000)
        isForwardEmailButtonEnable = true
    }

    LaunchedEffect(registrationState) {
        when(registrationState) {
            RegistrationState.EMAIL_VERIFIED -> {
                navController.navigate(Screen.FOURTH_REGISTRATION_SCREEN.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }

            RegistrationState.UNRECOGNIZED_ACCOUNT -> {
                registrationViewModel.resetRegistrationState()
                showToast(
                    context = context,
                    stringRes = com.upsaclay.common.R.string.unknown_error
                )
                navController.navigate(Screen.AUTHENTICATION.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }

            RegistrationState.OK -> {
                registrationViewModel.resetRegistrationState()
                registrationViewModel.sendVerificationEmail()
            }

            RegistrationState.ERROR -> {
                registrationViewModel.resetRegistrationState()
                showToast(context = context, stringRes = com.upsaclay.common.R.string.unknown_error)
            }

            else -> { }
        }
    }

    if(isLoading) {
        TopLinearLoadingScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.spacing.medium)
    ) {
        Column {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            Text(
                text = stringResource(id = R.string.email_verification_title),
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            Text(text = text, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            TextButton(
                onClick = {
                    isForwardButtonClicked = true
                    registrationViewModel.sendVerificationEmail()
                },
                enabled = !isForwardEmailButtonEnable
            ) {
                Text(text = stringResource(id = R.string.forward_verification_email),)
            }

            if(errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                ErrorText(text = errorMessage)
            }
        }

        PrimaryButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            shape = MaterialTheme.shapes.small,
            isEnable = !isLoading,
            text = stringResource(id = com.upsaclay.common.R.string.next),
            onClick = { registrationViewModel.verifyIsEmailVerified() }
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(showBackground = true)
@Composable
private fun EmailVerificationScreenPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val email = "patrick.dupont@email.com"
    val isError = false
    val text = stringResource(id = R.string.email_verification_text, email)

    GedoiseTheme {
        if(isLoading) {
            TopLinearLoadingScreen()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(MaterialTheme.spacing.medium)
        ) {
            Column {
                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    text = stringResource(id = R.string.email_verification_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                TextButton(
                    onClick = { }
                ) {
                    Text(
                        text = stringResource(id = R.string.forward_verification_email),
                    )
                }

                if (isError) {
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))

                    ErrorText(text = stringResource(id = R.string.email_not_verified),)
                }
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = stringResource(id = com.upsaclay.common.R.string.next),
                isEnable = !isLoading,
                shape = MaterialTheme.shapes.small,
                onClick = { isLoading = true }
            )
        }
    }
}