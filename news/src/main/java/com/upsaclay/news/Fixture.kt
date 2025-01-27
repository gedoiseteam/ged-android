package com.upsaclay.news

import com.upsaclay.common.domain.entity.User
import com.upsaclay.news.domain.entity.Announcement
import com.upsaclay.news.domain.entity.AnnouncementState
import java.time.LocalDateTime

internal val announcementFixture = Announcement(
    id = "1",
    title = "Rappel : Visite de cabinet le 23/03.",
    date = LocalDateTime.of(2024, 7, 20, 10, 0),
    content = "Nous vous informons que la visite de votre " +
        "cabinet médical est programmée pour le 23 mars. " +
        "Cette visite a pour but de s'assurer que toutes les normes de sécurité " +
        "et de conformité sont respectées, ainsi que de vérifier l'état général " +
        "des installations et des équipements médicaux." +
        "Nous vous recommandons de préparer tous les documents nécessaires et " +
        "de veiller à ce que votre personnel soit disponible pour répondre " +
        "à d'éventuelles questions ou fournir des informations supplémentaires. " +
        "Une préparation adéquate permettra de garantir que la visite se déroule " +
        "sans heurts et de manière efficace. N'hésitez pas à nous contacter si " +
        "vous avez des questions ou si vous avez besoin de plus amples informations" +
        " avant la date prévue",
    state = AnnouncementState.DEFAULT,
    author = User(
        id = "1",
        firstName = "Patrick",
        lastName = "Dupont",
        email = "patrick.dupont@example.com",
        schoolLevel = "GED 1",
        isMember = false,
        profilePictureUrl = "https://i-mom.unimedias.fr/2020/09/16/dragon-ball-songoku.jpg?auto=format,compress&cs=tinysrgb&w=1200"
    )
)

internal val announcementsFixture = listOf(
    announcementFixture,
    announcementFixture,
    announcementFixture,
    announcementFixture,
    announcementFixture
)