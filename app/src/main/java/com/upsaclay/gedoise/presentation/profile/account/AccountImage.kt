package com.upsaclay.gedoise.presentation.profile.account

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.components.ProfilePictureWithIcon

@Composable
fun AccountImage(
    modifier: Modifier = Modifier,
    isEdited: Boolean,
    profilePictureUri: Uri?,
    profilePictureUrl: String?,
    onClick: () -> Unit
) {
    val scaleImage = 1.8f

    AnimatedContent(
        targetState = profilePictureUri,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = fadeIn(),
                initialContentExit = fadeOut()
            )
        }
    ) { uri ->
        when(uri) {
            null -> {
                ProfilePictureWithIcon(
                    modifier = modifier,
                    url = profilePictureUrl,
                    iconVector = Icons.Default.Edit,
                    scale = scaleImage,
                    onClick = onClick
                )
            }

            else -> {
                if (isEdited) {
                    ProfilePicture(
                        modifier = modifier,
                        uri = uri,
                        scale = scaleImage,
                        onClick = onClick
                    )
                } else {
                    ProfilePicture(
                        modifier = modifier,
                        uri = uri,
                        scale = scaleImage,
                        onClick = onClick
                    )
                }
            }
        }
    }
}