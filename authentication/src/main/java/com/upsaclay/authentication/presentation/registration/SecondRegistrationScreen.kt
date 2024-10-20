package com.upsaclay.authentication.presentation.registration

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.model.RegistrationState
import com.upsaclay.authentication.presentation.components.RegistrationTopBar
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.common.presentation.components.OverlayLinearLoadingScreen
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.SimpleDropDownMenu
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecondRegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel = koinViewModel()
) {
    var selectedItem by remember { mutableStateOf(registrationViewModel.schoolLevel) }
    var expanded by remember { mutableStateOf(false) }
    val registrationState = registrationViewModel.registrationState.collectAsState().value
    val isLoading = registrationState == RegistrationState.LOADING

    LaunchedEffect(Unit) {
        registrationViewModel.resetRegistrationState()
    }

    RegistrationTopBar(
        navController = navController
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { expanded = false })
                },
        ) {
            Spacer(Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(id = R.string.select_level_school),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            SimpleDropDownMenu(
                items = registrationViewModel.schoolLevels,
                selectedItem = selectedItem,
                onItemClicked = { item ->
                    selectedItem = item
                    expanded = false
                },
                isEnable = !isLoading,
                expanded = expanded,
                onExpandedChange = { isExpanded ->
                    expanded = isExpanded
                },
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        PrimaryButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            text = stringResource(id = com.upsaclay.common.R.string.next),
            shape = MaterialTheme.shapes.small,
            isEnable = !isLoading,
            onClick = {
                registrationViewModel.updateSchoolLevel(selectedItem)
                navController.navigate(Screen.THIRD_REGISTRATION_SCREEN.route)
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
    val items = persistentListOf("GED 1", "GED 2", "GED 3")
    var selectedItem by remember { mutableStateOf(items[0]) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if(isLoading) {
            delay(1000)
            isLoading = false
        }
    }

    GedoiseTheme {
        if(isLoading) {
            OverlayLinearLoadingScreen()
        }

        RegistrationTopBar(
            navController = rememberNavController()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Spacer(Modifier.height(MaterialTheme.spacing.large))

                Text(
                    text = stringResource(id = R.string.select_level_school),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                SimpleDropDownMenu(
                    items = items,
                    selectedItem = selectedItem,
                    onItemClicked = { item ->
                        selectedItem = item
                        expanded = false
                    },
                    expanded = expanded,
                    isEnable = !isLoading,
                    onExpandedChange = { isExpanded ->
                        expanded = isExpanded
                    },
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = stringResource(id = com.upsaclay.common.R.string.next),
                shape = MaterialTheme.shapes.small,
                isEnable = !isLoading,
                onClick = { isLoading = true }
            )
        }
    }
}