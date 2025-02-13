package com.upsaclay.authentication.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.AuthenticationScreenState
import com.upsaclay.authentication.presentation.components.LoginButton
import com.upsaclay.authentication.presentation.components.OutlinedEmailInput
import com.upsaclay.authentication.presentation.components.OutlinedPasswordInput
import com.upsaclay.authentication.presentation.viewmodels.AuthenticationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorTextWithIcon
import com.upsaclay.common.presentation.components.SimpleDialog
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthenticationScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel = koinViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    var showVerifyEmailDialog by remember { mutableStateOf(false) }
    val screenState by authenticationViewModel.screenState.collectAsState()
    val focusManager = LocalFocusManager.current

    val (errorMessage, inputsError) = when (screenState) {
        AuthenticationScreenState.AUTHENTICATION_ERROR -> {
            authenticationViewModel.resetPassword()
            stringResource(id = R.string.error_connection) to true
        }

        AuthenticationScreenState.EMPTY_FIELDS_ERROR -> {
            authenticationViewModel.resetPassword()
            stringResource(id = com.upsaclay.common.R.string.empty_fields_error) to true
        }

        AuthenticationScreenState.AUTHENTICATED_USER_NOT_FOUND -> {
            authenticationViewModel.resetPassword()
            stringResource(id = R.string.authenticated_user_not_found) to true
        }

        AuthenticationScreenState.TOO_MANY_REQUESTS_ERROR -> {
            authenticationViewModel.resetPassword()
            stringResource(id = R.string.too_many_request_error) to false
        }

        AuthenticationScreenState.EMAIL_FORMAT_ERROR -> {
            authenticationViewModel.resetPassword()
            stringResource(id = R.string.error_incorrect_email_format) to true
        }

        AuthenticationScreenState.SERVER_COMMUNICATION_ERROR -> {
            authenticationViewModel.resetPassword()
            stringResource(id = com.upsaclay.common.R.string.server_communication_error) to false
        }

        AuthenticationScreenState.UNKNOWN_ERROR -> {
            authenticationViewModel.resetPassword()
            stringResource(id = com.upsaclay.common.R.string.unknown_error) to false
        }

        else -> "" to false
    }

    LaunchedEffect(Unit) {
        authenticationViewModel.resetScreenState()
    }

    LaunchedEffect(screenState) {
        when (screenState) {
            AuthenticationScreenState.EMAIL_NOT_VERIFIED -> showVerifyEmailDialog = true

            else -> {}
        }
    }

    if (showVerifyEmailDialog) {
        SimpleDialog(
            modifier = Modifier.testTag(stringResource(id = R.string.authentication_screen_verify_email_dialog_tag)),
            title = stringResource(id = R.string.email_not_verified_dialog_title),
            text = stringResource(id = R.string.email_not_verified),
            confirmText = stringResource(id = com.upsaclay.common.R.string.keep_going),
            onDismiss = {
                authenticationViewModel.resetPassword()
                showVerifyEmailDialog = false
                authenticationViewModel.resetScreenState()
            },
            onConfirm = {
                authenticationViewModel.resetEmail()
                authenticationViewModel.resetPassword()
                authenticationViewModel.resetScreenState()
                navController.navigate(Screen.EMAIL_VERIFICATION.route + "?email=${authenticationViewModel.email}") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onCancel = {
                authenticationViewModel.resetPassword()
                showVerifyEmailDialog = false
                authenticationViewModel.resetScreenState()
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.aligned { size, space ->
            size + (30 * space / 100)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(MaterialTheme.spacing.medium)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        TitleSection()

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

        Column {
            InputsSection(
                email = authenticationViewModel.email,
                onEmailChange = { authenticationViewModel.updateEmail(it) },
                password = authenticationViewModel.password,
                onPasswordChange = { authenticationViewModel.updatePassword(it) },
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                errorMessage = errorMessage,
                isError = inputsError,
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            LoginButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(id = R.string.authentication_screen_login_button_tag)),
                text = stringResource(id = R.string.login),
                isLoading = screenState == AuthenticationScreenState.LOADING,
                onClick = {
                    keyboardController?.hide()
                    if (authenticationViewModel.verifyInputs()) {
                        authenticationViewModel.login()
                    }
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

            RegistrationText(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onRegistrationClick = {
                    authenticationViewModel.resetEmail()
                    authenticationViewModel.resetPassword()
                    authenticationViewModel.resetScreenState()
                    navController.navigate(Screen.FIRST_REGISTRATION.route)
                }
            )
        }
    }
}

@Composable
private fun TitleSection() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = com.upsaclay.common.R.drawable.ged_logo),
            contentDescription = stringResource(id = com.upsaclay.common.R.string.ged_logo_description),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .width(screenWidth * 0.35f)
                .height(screenHeight * 0.2f)
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Text(
            text = stringResource(id = com.upsaclay.common.R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        Text(
            text = stringResource(id = R.string.presentation_text),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun RegistrationText(
    modifier: Modifier = Modifier,
    onRegistrationClick: () -> Unit
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.not_register_yet),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))

        Text(
            text = AnnotatedString(stringResource(id = R.string.sign_up)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .clickable { onRegistrationClick() }
                .testTag(stringResource(id = R.string.authentication_screen_registration_button_tag))
        )
    }
}

@Composable
private fun InputsSection(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    keyboardActions: KeyboardActions,
    errorMessage: String,
    isError: Boolean
) {
    Column {
        OutlinedEmailInput(
            modifier = Modifier.fillMaxWidth(),
            text = email,
            onValueChange = onEmailChange,
            keyboardActions = keyboardActions,
            isError = isError
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        OutlinedPasswordInput(
            modifier = Modifier.fillMaxWidth(),
            text = password,
            onValueChange = onPasswordChange,
            keyboardActions = keyboardActions,
            isError = isError
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            ErrorTextWithIcon(text = errorMessage)
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(widthDp = 360, heightDp = 740, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AuthenticationScreenPreview() {
    var isLoading by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    GedoiseTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.aligned { size, space ->
                size + (20 * space / 100)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            TitleSection()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            Column {
                InputsSection(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    keyboardActions = KeyboardActions(onDone = { }),
                    errorMessage = "",
                    isError = false
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                LoginButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.login),
                    isLoading = isLoading,
                    onClick = { isLoading = !isLoading }
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                RegistrationText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onRegistrationClick = { }
                )
            }
        }
    }
}