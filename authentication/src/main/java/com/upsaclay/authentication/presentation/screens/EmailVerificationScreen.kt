package com.upsaclay.authentication.presentation.screens

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.AuthenticationState
import com.upsaclay.authentication.domain.entity.RegistrationState
import com.upsaclay.authentication.presentation.viewmodels.EmailVerificationViewModel
import com.upsaclay.authentication.presentation.viewmodels.RegistrationViewModel
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.common.presentation.components.ErrorText
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EmailVerificationScreen(
    email: String,
    emailVerificationViewModel: EmailVerificationViewModel =
        koinViewModel(parameters = { parametersOf(email) })
) {
    val screenState by emailVerificationViewModel.screenState.collectAsState()
    var errorMessage by remember { mutableStateOf("") }
    val isLoading = screenState == AuthenticationState.LOADING
    var isForwardEmailButtonEnable by remember { mutableStateOf(true) }
    var isForwardButtonClicked by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedValue = infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1300, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.email_verification_explanation_begining))
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        ) {
            append(email)
        }
        append(stringResource(id = R.string.email_verification_explanation_end))
    }

    errorMessage = when(screenState) {
        AuthenticationState.EMAIL_NOT_VERIFIED -> stringResource(id = R.string.email_not_verified)

        AuthenticationState.UNKNOWN_ERROR -> stringResource(id = com.upsaclay.common.R.string.unknown_error)

        else -> ""
    }

    LaunchedEffect(Unit) {
        emailVerificationViewModel.sendVerificationEmail()
    }

    LaunchedEffect(isForwardButtonClicked) {
        isForwardEmailButtonEnable = false
        delay(60000)
        isForwardEmailButtonEnable = true
    }

    Scaffold(
        topBar = { Text(stringResource(id = R.string.email_verification_title)) }
    ) { contentPadding ->
        if (isLoading) {
            TopLinearLoadingScreen()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start =  MaterialTheme.spacing.medium,
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

                Text(text = annotatedString, style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                TextButton(
                    onClick = {
                        isForwardButtonClicked = true
                        emailVerificationViewModel.sendVerificationEmail()
                    },
                    enabled = !isForwardEmailButtonEnable
                ) {
                    Text(text = stringResource(id = R.string.forward_verification_email),)
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))

                    ErrorText(text = errorMessage)
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .scale(animatedValue.value)
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(MaterialTheme.spacing.medium)
                        .align(Alignment.Center)
                        .size(100.dp)
                )

                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(90.dp),
                    imageVector = Icons.Filled.Email,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = MaterialTheme.shapes.small,
                isEnable = !isLoading,
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
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedValue = infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1300, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.email_verification_explanation_begining))
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        ) {
            append(email)
        }
        append(stringResource(id = R.string.email_verification_explanation_end))
    }

    GedoiseTheme {
        Scaffold(
            topBar = { Text(stringResource(id = R.string.email_verification_title)) }
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
                Column {
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))

                    Text(
                        text = stringResource(id = R.string.email_verification_title),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(Modifier.height(MaterialTheme.spacing.medium))

                    Text(
                        text = annotatedString,
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

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .scale(animatedValue.value)
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(MaterialTheme.spacing.medium)
                            .align(Alignment.Center)
                            .size(100.dp)
                    )

                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(90.dp),
                        imageVector = Icons.Filled.Email,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = Color.White)
                    )
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