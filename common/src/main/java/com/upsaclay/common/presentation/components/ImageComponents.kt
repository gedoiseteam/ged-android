package com.upsaclay.common.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.upsaclay.common.R
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing

@Composable
fun ProfilePictureWithIcon(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    uri: Uri?,
    iconVector: ImageVector = Icons.Default.Edit,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = Color.White,
    contentDescription: String = "",
    onClick: (() -> Unit)?
) {
    ImageWithIcon(
        modifier = modifier,
        model = uri ?: R.drawable.default_profile_picture,
        scale = scale,
        iconVector = iconVector,
        iconBackgroundColor = iconBackgroundColor,
        iconColor = iconColor,
        contentDescription = contentDescription,
        onClick = onClick
    )
}

@Composable
fun ProfilePictureWithIcon(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    url: String?,
    iconVector: ImageVector = Icons.Default.Edit,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = Color.White,
    contentDescription: String = "",
    onClick: (() -> Unit)? = null
) {
    ImageWithIcon(
        modifier = modifier,
        model = url ?: R.drawable.default_profile_picture,
        scale = scale,
        iconVector = iconVector,
        iconBackgroundColor = iconBackgroundColor,
        iconColor = iconColor,
        contentDescription = contentDescription,
        onClick = onClick
    )
}

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    url: String?,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = url ?: R.drawable.default_profile_picture,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = onClick?.let {
            modifier
                .size(100.dp * scale)
                .clip(CircleShape)
                .clickable(onClick = it)
        } ?: run {
            modifier
                .size(100.dp * scale)
                .clip(CircleShape)
        },
        onLoading = { ColorPainter(GedoiseColor.ProfilePictureErrorLight) },
        error = ColorPainter(GedoiseColor.ProfilePictureErrorLight)
    )
}

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    uri: Uri?,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = uri ?: R.drawable.default_profile_picture,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = onClick?.let {
            modifier
                .size(100.dp * scale)
                .clip(CircleShape)
                .clickable(onClick = it)
        } ?: run {
            modifier
                .size(100.dp * scale)
                .clip(CircleShape)
        },
        error = ColorPainter(GedoiseColor.ProfilePictureErrorLight)
    )
}

@Composable
fun ProfilePictureWithBubble(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    url: String?,
    bubbleBackgroundColor: Color,
    contentDescription: String,
    onClick: (() -> Unit)? = null
) {
    ImageWithBubble(
        modifier = modifier,
        model = url ?: R.drawable.default_profile_picture,
        scale = scale,
        bubbleBackgroundColor = bubbleBackgroundColor,
        contentDescription = contentDescription,
        onClick = onClick
    )
}

@Composable
private fun ImageWithIcon(
    modifier: Modifier = Modifier,
    scale: Float,
    model: Any,
    iconVector: ImageVector,
    iconColor: Color,
    iconBackgroundColor: Color,
    contentDescription: String,
    onClick: (() -> Unit)?
) {
    Box(modifier = modifier.size(100.dp * scale)) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = onClick?.let {
                Modifier
                    .align(Alignment.Center)
                    .size(100.dp * scale)
                    .clip(CircleShape)
                    .clickable(onClick = it)
            } ?: run {
                Modifier
                    .align(Alignment.Center)
                    .size(100.dp * scale)
                    .clip(CircleShape)
            },
            error = ColorPainter(GedoiseColor.ProfilePictureErrorLight)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = onClick?.let {
                Modifier
                    .clip(CircleShape)
                    .background(iconBackgroundColor)
                    .align(Alignment.BottomEnd)
                    .size(30.dp * scale)
                    .clickable(onClick = it)
            } ?: run {
                Modifier
                    .clip(CircleShape)
                    .background(iconBackgroundColor)
                    .align(Alignment.BottomEnd)
                    .size(30.dp * scale)
            }
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = "",
                tint = iconColor,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.extraSmall)
                    .size(16.dp * scale)
            )
        }
    }
}

@Composable
private fun ImageWithBubble(
    modifier: Modifier,
    scale: Float = 1f,
    model: Any,
    bubbleBackgroundColor: Color,
    contentDescription: String,
    onClick: (() -> Unit)?
) {
    Box(modifier = modifier.size(100.dp * scale)) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = onClick?.let {
                Modifier
                    .align(Alignment.Center)
                    .size(100.dp * scale)
                    .clip(CircleShape)
                    .clickable(onClick = it)
            } ?: run {
                Modifier
                    .align(Alignment.Center)
                    .size(100.dp * scale)
                    .clip(CircleShape)
            },
            error = ColorPainter(GedoiseColor.ProfilePictureErrorLight)
        )

        Box(
            modifier = onClick?.let {
                Modifier
                    .clip(CircleShape)
                    .background(bubbleBackgroundColor)
                    .align(Alignment.BottomEnd)
                    .size(30.dp * scale)
                    .clickable(onClick = it)
            } ?: run {
                Modifier
                    .clip(CircleShape)
                    .background(bubbleBackgroundColor)
                    .align(Alignment.BottomEnd)
                    .size(30.dp * scale)
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
private fun ProfilePicturePreview() {
    GedoiseTheme {
        Box(Modifier.size(100.dp)) {
            ProfilePicture(
                url = null,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfilePictureWithIconPreview() {
    GedoiseTheme {
        Box(Modifier.size(100.dp)) {
            ProfilePictureWithIcon(
                url = null,
                iconVector = Icons.Default.Edit,
                contentDescription = "",
                iconBackgroundColor = MaterialTheme.colorScheme.primary,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ProfilePictureWithBubblePreview() {
    GedoiseTheme {
        Box(Modifier.size(100.dp)) {
            ProfilePictureWithBubble(
                url = null,
                bubbleBackgroundColor = GedoiseColor.OnlineColor,
                contentDescription = "",
                onClick = {}
            )
        }
    }
}