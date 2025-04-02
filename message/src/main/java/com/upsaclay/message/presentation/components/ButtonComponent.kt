package com.upsaclay.message.presentation.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.upsaclay.message.R

@Composable
fun CreateConversationFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_message_add),
            contentDescription = stringResource(id = R.string.ic_fab_button_add_description)
        )
    }
}
