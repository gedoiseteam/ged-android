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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.R
import com.upsaclay.authentication.domain.entity.AuthenticationScreenRoute
import com.upsaclay.authentication.presentation.components.RegistrationScaffold
import com.upsaclay.authentication.presentation.viewmodels.SecondRegistrationViewModel
import com.upsaclay.common.presentation.components.PrimaryButton
import com.upsaclay.common.presentation.components.SimpleDropDownMenu
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecondRegistrationScreen(
    firstName: String,
    lastName: String,
    navController: NavController,
    secondRegistrationViewModel: SecondRegistrationViewModel = koinViewModel()
) {
    val schoolLevel by secondRegistrationViewModel.schoolLevel.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    RegistrationScaffold(navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { expanded = false })
                }
        ) {
            Text(
                text = stringResource(id = R.string.select_level_school),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            SimpleDropDownMenu(
                items = secondRegistrationViewModel.schoolLevels,
                selectedItem = schoolLevel,
                onItemClicked = { item ->
                    secondRegistrationViewModel.updateSchoolLevel(item)
                    expanded = false
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            )
        }

        PrimaryButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .testTag(stringResource(id = R.string.registration_screen_next_button_tag)),
            text = stringResource(id = com.upsaclay.common.R.string.next),
            onClick = {
                navController.navigate(
                    AuthenticationScreenRoute.ThirdRegistration(
                        firstName = firstName,
                        lastName = lastName,
                        schoolLevel = schoolLevel
                    ).route
                )
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
private fun SecondRegistrationScreenPreview() {
    val items = listOf("GED 1", "GED 2", "GED 3")
    var selectedItem by remember { mutableStateOf(items[0]) }
    var expanded by remember { mutableStateOf(false) }

    GedoiseTheme {
        RegistrationScaffold(navController = rememberNavController()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
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
                    onExpandedChange = { expanded = it },
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PrimaryButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = stringResource(id = com.upsaclay.common.R.string.next),
                onClick = { }
            )
        }
    }
}