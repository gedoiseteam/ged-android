package com.upsaclay.authentication.presentation.registration

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.model.RegistrationState
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.components.OverlayCircularLoadingScreen
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.components.TopLinearLoadingScreen
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.common.utils.showToast
import org.koin.androidx.compose.koinViewModel

@Composable
fun FourthRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> registrationViewModel.updateProfilePictureUri(uri) }
    )
    val registrationState = registrationViewModel.registrationState.collectAsState().value
    val isLoading = registrationState == RegistrationState.LOADING

    LaunchedEffect(registrationState) {
        if (registrationState == RegistrationState.OK) {
            navController.navigate(Screen.NEWS.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        registrationViewModel.resetRegistrationState()
    }

    if(isLoading) {
        TopLinearLoadingScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.add_profile_picture),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            ProfilePicture(
                imageUri = registrationViewModel.profilePictureUri,
                scale = 2f,
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            registrationViewModel.profilePictureUri?.let {
                TextButton(
                    onClick = { registrationViewModel.resetProfilePictureUri() }
                ) {
                    Text(
                        text = stringResource(id = com.upsaclay.common.R.string.remove),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Text(
                modifier = Modifier.padding(top = MaterialTheme.spacing.smallMedium),
                text = registrationViewModel.firstName + " " + registrationViewModel.lastName,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = registrationViewModel.email,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            if (registrationState == RegistrationState.ERROR) {
                showToast(context = LocalContext.current, stringRes = R.string.error_update_profile_picture)
            }
        }

        PrimaryButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            text = stringResource(id = com.upsaclay.common.R.string.finish),
            shape = MaterialTheme.shapes.small,
            isEnable = !isLoading,
            onClick = { registrationViewModel.updateUserProfilePicture() }
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
private fun FifthRegistrationScreenPreview() {
    val pictureChanged = false
    var isLoading by remember { mutableStateOf(false) }

    GedoiseTheme {
        if (isLoading) {
            OverlayCircularLoadingScreen()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(MaterialTheme.spacing.medium)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.add_profile_picture),
                    style = MaterialTheme.typography.titleMedium
                )

                Box(
                    modifier = Modifier.size(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.upsaclay.common.R.drawable.default_profile_picture),
                        contentDescription = "",
                        modifier = Modifier.size(100.dp).scale(2f)
                    )
                    ProfilePicture(
                        imageUrl = null,
                        scale = 2f,
                        onClick = {}
                    )
                }

                if (pictureChanged) {
                    TextButton(
                        onClick = { },
                    ) {
                        Text(
                            text = stringResource(id = com.upsaclay.common.R.string.remove),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Text(
                    modifier = Modifier.padding(top = MaterialTheme.spacing.smallMedium),
                    text = "Pierre Dupont",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                Text(
                    text = "pierre.dupont@universite-paris-saclay.fr",
                    maxLines = 1,
                    color = Color.DarkGray,
                    overflow = TextOverflow.Ellipsis
                )
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = stringResource(id = com.upsaclay.common.R.string.finish),
                shape = MaterialTheme.shapes.small,
                isEnable = !isLoading,
                onClick = { isLoading = true }
            )
        }
    }
}