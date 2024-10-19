package com.upsaclay.message.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.upsaclay.common.presentation.components.ProfilePicture
import com.upsaclay.common.presentation.theme.GedoiseColor
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import com.upsaclay.message.domain.model.Message
import com.upsaclay.message.utils.messageFixture

@Composable
fun SentMessageItem(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.fillMaxWidth(0.2f))

        MessageText(
            text = text,
            textColor = Color.White,
            backgroundColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ReceiveMessageItem(
    modifier: Modifier = Modifier,
    profilePictureUrl: String?,
    message: Message,
    displayProfilePicture: Boolean
) {
    val backgroundColor = if(isSystemInDarkTheme()) GedoiseColor.DarkGray else GedoiseColor.LightGray

    Row(
        modifier = modifier.fillMaxWidth(0.8f),
        verticalAlignment = Alignment.Bottom
    ) {
        if(displayProfilePicture) {
            ProfilePicture(imageUrl = profilePictureUrl, scale = 0.3f)
        }

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))

        MessageText(
            text = message.text,
            backgroundColor = backgroundColor
        )
    }
}

@Composable
private fun MessageText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = Color.Transparent
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(MaterialTheme.spacing.large))
            .background(backgroundColor)
            .padding(
                vertical = MaterialTheme.spacing.small,
                horizontal = MaterialTheme.spacing.smallMedium
            ),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor
    )
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

private val smallText = "Bonsoir, pas de soucis."
private val mediumText = "Cela pourrait également aider à résoudre tout problème éventuel."
private val longtext = "Bonjour, j'espère que vous allez bien. " +
        "Je voulais prendre un moment pour vous parler de quelque chose d'important. " +
        "En fait, je pense qu'il est essentiel que nous discutions de la direction que prend notre projet, " +
        "car il y a plusieurs points que nous devrions clarifier. " +
        "Tout d'abord, j'ai remarqué que certains aspects de notre stratégie actuelle pourraient être améliorés. " +
        "Je crois que nous pourrions gagner en efficacité si nous ajustions certaines étapes du processus. " +
        "Par exemple, en ce qui concerne la gestion des priorités, il serait peut-être utile de revoir nos méthodes " +
        "afin d'être sûrs que nous concentrons nos efforts sur les éléments les plus importants."

@Preview
@Composable
private fun SentMessageItemPreview() {
    GedoiseTheme {
        SentMessageItem(text = mediumText)
    }
}

@Preview
@Composable
private fun ReceiveMessageItemPreview() {
    GedoiseTheme {
        ReceiveMessageItem(
            message = messageFixture.copy(text = mediumText),
            displayProfilePicture = true,
            profilePictureUrl = ""
        )
    }
}