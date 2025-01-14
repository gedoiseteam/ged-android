package com.upsaclay.authentication.presentation

import android.os.Build
import android.view.ViewTreeObserver
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.model.AuthenticationState
import com.upsaclay.authentication.presentation.components.LoginButton
import com.upsaclay.authentication.presentation.components.OutlinedEmailInput
import com.upsaclay.authentication.presentation.components.OutlinedPasswordInput
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.components.ErrorTextWithIcon
import com.upsaclay.common.presentation.components.SimpleDialog
import com.upsaclay.common.presentation.theme.GedoiseColor.Primary
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthenticationScreen(
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel = koinViewModel()
) {

    val authenticationState = authenticationViewModel.authenticationState.collectAsState().value
    var inputsError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showVerifyEmailDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val view = LocalView.current
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    inputsError = authenticationState == AuthenticationState.AUTHENTICATION_ERROR ||
            authenticationState == AuthenticationState.INPUTS_EMPTY_ERROR

    errorMessage = when (authenticationState) {
        AuthenticationState.AUTHENTICATION_ERROR ->
            stringResource(id = R.string.error_connection)

        AuthenticationState.INPUTS_EMPTY_ERROR ->
            stringResource(id = com.upsaclay.common.R.string.empty_fields_error)

        AuthenticationState.AUTHENTICATED_USER_NOT_FOUND ->
            stringResource(id = R.string.authenticated_user_not_found)

        AuthenticationState.TOO_MANY_REQUESTS_ERROR ->
            stringResource(id = R.string.too_many_request_error)

        else -> ""
    }

    LaunchedEffect(Unit) {
        authenticationViewModel.resetAuthenticationState()
    }

    LaunchedEffect(authenticationState) {
        when(authenticationState) {
            AuthenticationState.AUTHENTICATED -> {
                navController.navigate(Screen.NEWS.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }

            AuthenticationState.NETWORK_ERROR -> {
                showToast(context = context, stringRes = com.upsaclay.common.R.string.network_error)
            }

            AuthenticationState.EMAIL_NOT_VERIFIED -> {
                showVerifyEmailDialog = true
            }

            AuthenticationState.UNKNOWN_ERROR -> {
                showToast(context = context, stringRes = com.upsaclay.common.R.string.unknown_error)
            }

            else -> {}
        }
    }

    DisposableEffect(context) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val insets = view.rootWindowInsets
            val keyboardHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
            } else {
                insets?.systemWindowInsetBottom ?: 0
            }
            isKeyboardVisible = keyboardHeight > 0
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    LaunchedEffect(isKeyboardVisible) {
        if (isKeyboardVisible) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    if(showVerifyEmailDialog) {
        SimpleDialog(
            message = stringResource(id = R.string.email_not_verified_dialog_message),
            confirmText = stringResource(id = com.upsaclay.common.R.string.keep_going),
            onDismiss = {
                showVerifyEmailDialog = false
                authenticationViewModel.resetAuthenticationState()
            },
            onConfirm = {
                navController.navigate(Screen.CHECK_EMAIL_VERIFIED_SCREEN.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onCancel = {
                showVerifyEmailDialog = false
                authenticationViewModel.resetAuthenticationState()
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
                detectTapGestures(onPress = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
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
                focusRequester = focusRequester
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            LoginButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.login),
                isLoading = authenticationState == AuthenticationState.LOADING ||
                        authenticationState == AuthenticationState.AUTHENTICATED,
                onClick = {
                    keyboardController?.hide()
                    authenticationViewModel.login()
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

            RegistrationSection(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onRegistrationClick = { navController.navigate(Screen.FIRST_REGISTRATION_SCREEN.route) }
            )
        }
    }
    TODO("une fois authentifier, transmettre le même Flow lors de chaque changement de page, en le transmettant en parametre du navigator, il devra donc être le paramètre de chaque viewModel pour qu'ils se recon" +
            "construisent le même objet flow du début de la connexion")
}

@Composable
private fun TitleSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = com.upsaclay.common.R.drawable.ged_logo),
            contentDescription = stringResource(id = com.upsaclay.common.R.string.ged_logo_description),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        Text(
            text = stringResource(id = R.string.authentication_screen_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        Text(
            text = stringResource(id = R.string.presentation_text),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Primary
        )
    }
}

@Composable
private fun RegistrationSection(
    modifier: Modifier = Modifier,
    onRegistrationClick: () -> Unit
) {
    Row(modifier = modifier) {
        Text(text = stringResource(id = R.string.first_arrival))

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))

        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.sign_up)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            onClick = { onRegistrationClick() }
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
    isError: Boolean,
    focusRequester: FocusRequester
) {
    Column {
        OutlinedEmailInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = email,
            onValueChange = onEmailChange,
            keyboardActions = keyboardActions,
            isError = isError
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        OutlinedPasswordInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = password,
            onValueChange = onPasswordChange,
            keyboardActions = keyboardActions,
            isError = isError
        )

        if(errorMessage.isNotEmpty()) {
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

@Preview(widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun AuthenticationScreenPreview() {
    var isLoading by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
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
                    isError = false,
                    focusRequester = focusRequester
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                LoginButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.login),
                    isLoading = isLoading,
                    onClick = { isLoading = !isLoading }
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallMedium))

                RegistrationSection(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onRegistrationClick = { }
                )
            }
        }
    }
}