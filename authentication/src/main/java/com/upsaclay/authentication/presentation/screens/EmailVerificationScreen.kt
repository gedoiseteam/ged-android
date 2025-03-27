package com.upsaclay.authentication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.AuthenticationEvent
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.common.domain.entity.ErrorType
import com.upsaclay.common.presentation.components.ErrorText
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EmailVerificationScreen(
    email: String,
    navController: NavController,
    emailVerificationViewModel: EmailVerificationViewModel =
        koinViewModel(parameters = { parametersOf(email) })
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var isForwardEmailButtonEnable by remember { mutableStateOf(true) }
    var isForwardButtonClicked by remember { mutableStateOf(false) }

    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.email_verification_explanation_begining) + " ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append(email)
        }
        append(stringResource(id = R.string.email_verification_explanation_end))
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    LaunchedEffect(Unit) {
        emailVerificationViewModel.event.collectLatest { event ->
            loading = event == AuthenticationEvent.Loading
            when (event) {
                is AuthenticationEvent.Error -> {
                    errorMessage = if (event == AuthenticationEvent.EmailNotVerified) {
                        context.getString(R.string.email_not_verified)
                    } else ""

                    when (event.type) {
                        ErrorType.NetworkError -> showSnackbar(context.getString(com.upsaclay.common.R.string.unknown_network_error))
                        ErrorType.UnknownError -> showSnackbar(context.getString(com.upsaclay.common.R.string.unknown_error))
                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        emailVerificationViewModel.sendVerificationEmail()
    }

    LaunchedEffect(isForwardButtonClicked) {
        if (isForwardButtonClicked) {
            isForwardEmailButtonEnable = false
            delay(60000)
            isForwardEmailButtonEnable = true
        }
    }

    Scaffold(
        topBar = {
            SmallTopBarBack(
                onBackClick = { navController.popBackStack() },
                title = stringResource(id = R.string.email_verification_title)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(it)
            }
        }
    ) { contentPadding ->
        if (loading) {
            TopLinearLoadingScreen()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = MaterialTheme.spacing.medium,
                    top = contentPadding.calculateTopPadding(),
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
        ) {
            Column {
                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    text = stringResource(id = R.string.email_verification_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        ErrorText(text = errorMessage)
                    }
                }

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                OutlinedButton(
                    modifier = Modifier
                        .testTag(stringResource(id = R.string.email_verification_screen_forward_email_button_tag)),
                    onClick = {
                        isForwardButtonClicked = true
                        emailVerificationViewModel.sendVerificationEmail()
                    },
                    enabled = isForwardEmailButtonEnable && !loading
                ) {
                    Text(text = stringResource(id = R.string.forward_verification_email))
                }
            }

            PrimaryButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .testTag(stringResource(id = R.string.email_verification_screen_finish_button_tag)),
                isEnable = !loading,
                text = stringResource(id = com.upsaclay.common.R.string.next),
                onClick = { emailVerificationViewModel.verifyIsEmailVerified() }
            )
        }
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

    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.email_verification_explanation_begining) + " ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
            )
        ) {
            append(email)
        }
        append(stringResource(id = R.string.email_verification_explanation_end))
    }

    GedoiseTheme {
        Scaffold(
            topBar = {
                SmallTopBarBack(
                    onBackClick = { },
                    title = stringResource(id = R.string.email_verification_title)
                )
            }
        ) { contentPadding ->

            if (isLoading) {
                TopLinearLoadingScreen()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.medium
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    if (isError) {
                        ErrorText(text = stringResource(id = R.string.email_not_verified))
                    }

                    OutlinedButton(
                        onClick = { },
                        enabled = !isLoading
                    ) {
                        Text(text = stringResource(id = R.string.forward_verification_email))
                    }
                }

                PrimaryButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    text = stringResource(id = com.upsaclay.common.R.string.finish),
                    isEnable = !isLoading,
                    onClick = { isLoading = true }
                )
            }
        }
    }
}