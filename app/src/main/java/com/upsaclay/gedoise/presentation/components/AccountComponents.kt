package com.upsaclay.gedoise.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.upsaclay.common.presentation.components.ClickableItem
import com.upsaclay.common.presentation.components.SmallTopBarAction
import com.upsaclay.common.presentation.components.SmallTopBarBack
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.gedoise.R
import com.upsaclay.gedoise.domain.entities.AccountInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountModelBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onNewProfilePictureClick: () -> Unit,
    showDeleteProfilePicture: Boolean = false,
    onDeleteProfilePictureClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        ClickableItem(
            modifier = Modifier.fillMaxWidth(),
            text = { Text(text = stringResource(id = R.string.new_profile_picture)) },
            icon = {
                Icon(
                    painter = painterResource(id = com.upsaclay.common.R.drawable.ic_picture),
                    contentDescription = null
                )
            },
            onClick = onNewProfilePictureClick
        )

        if (showDeleteProfilePicture) {
            ClickableItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(id = R.string.account_screen_delete_profile_picture_button_tag)),
                text = {
                    Text(
                        text = stringResource(id = R.string.delete_profile_picture),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = onDeleteProfilePictureClick
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
    }
}

@Composable
internal fun AccountInfoItem(
    modifier: Modifier = Modifier,
    accountInfo: AccountInfo
) {
    val labelColor = if (isSystemInDarkTheme()) GedoiseColor.PreviewTextDark else GedoiseColor.PreviewTextLight

    Column(
        modifier = modifier.padding(vertical = MaterialTheme.spacing.smallMedium)
    ) {
        Text(
            text = accountInfo.label,
            color = labelColor,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = accountInfo.value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
internal fun AccountTopBar(
    isEdited: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onBackClick: () -> Unit
) {
    if (isEdited) {
        SmallTopBarAction(
            title = stringResource(id = R.string.edit_profile),
            onCancelClick = onCancelClick,
            onActionClick = onSaveClick,
            buttonText = stringResource(id = com.upsaclay.common.R.string.save)
        )
    } else {
        SmallTopBarBack(
            title = stringResource(id = R.string.account_informations),
            onBackClick = onBackClick
        )
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AccountItemPreview() {
    GedoiseTheme {
        AccountInfoItem(
            accountInfo = AccountInfo(
                label = "Label",
                value = "Value"
            )
        )
    }
}