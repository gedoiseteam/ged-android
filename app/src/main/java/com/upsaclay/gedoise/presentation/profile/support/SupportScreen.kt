package com.upsaclay.gedoise.presentation.profile.support

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserFlowUseCase
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.presentation.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SupportScreen(modifier: Modifier = Modifier, navController: NavController)
{
    Column{
        val viewModel : SupportViewModel = koinViewModel()
        Scaffold (topBar = { SmallTopBarBack(onBackClick = { navController.popBackStack() }, title = stringResource(id = R.string.support))}) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ){
                Text(text = stringResource(id = R.string.support_mail_subject))
                Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.extraSmall,MaterialTheme.spacing.small))
                TextField(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                    value = "",
                    onValueChange = { newObject -> viewModel.apply { object_message = newObject } })
                Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.large))
                Text(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),text = stringResource(id = R.string.content_message))
                TextField(modifier = Modifier.size(MaterialTheme.spacing.ultraLarge,MaterialTheme.spacing.ultraLarge),
                    value = "",
                    onValueChange = { newBody -> viewModel.apply { message = newBody } })
                Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.large))
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { viewModel.contactSupport() },
                    content = { Text(text = stringResource(id = R.string.send_message_support)) })
            }
        }

    }

}


@Preview
@Composable
fun SupportScreenPreview()
{
    GedoiseTheme {
        Column{
            var objet by remember { mutableStateOf("") }
            var body by remember { mutableStateOf("") }

            Scaffold (topBar = { SmallTopBarBack(onBackClick = {  }, title = stringResource(id = R.string.support)) }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = it.calculateTopPadding())
                ){
                    Text(text = stringResource(id = R.string.support_mail_subject))
                    Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.extraSmall,MaterialTheme.spacing.small))
                    TextField(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                        value = objet,
                        onValueChange = { newObject -> objet = newObject })
                    Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.large,MaterialTheme.spacing.extraLarge))
                    Text(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),text = stringResource(id = R.string.content_message))
                    Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.extraSmall,MaterialTheme.spacing.small))
                    TextField(modifier = Modifier.
                                        size(MaterialTheme.spacing.ultraLarge,MaterialTheme.spacing.ultraLarge),
                        value = body,
                        onValueChange = { newBody -> body = newBody },

                    )
                    Spacer(modifier = Modifier.heightIn(MaterialTheme.spacing.extraLarge))
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { },
                        content = { Text(text = stringResource(id = R.string.send_message_support)) })
                }
            }

        }
    }
}