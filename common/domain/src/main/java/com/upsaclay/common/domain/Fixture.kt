package com.upsaclay.common.domain

import com.upsaclay.common.domain.entity.FCMData
import com.upsaclay.common.domain.entity.FCMDataType
import com.upsaclay.common.domain.entity.FCMMessage
import com.upsaclay.common.domain.entity.FCMNotification
import com.upsaclay.common.domain.entity.User

val userFixture = User(
    "12",
    "Pierre",
    "Dupont",
    "pierre.dupont@universite-paris-saclay.fr",
    "GED 1",
    true,
    "https://i-mom.unimedias.fr/2020/09/16/dragon-ball-songoku.jpg"
)

val userFixture2 = User(
    "13",
    "Alain",
    "Robert",
    "alain.robert@universite-paris-saclay.fr",
    "GED 3",
    false,
    "https://avatarfiles.alphacoders.com/330/330775.png"
)

val usersFixture = listOf(
    userFixture,
    userFixture,
    userFixture,
    userFixture,
    userFixture2,
    userFixture2,
    userFixture2,
    userFixture2,
    userFixture2
)

val fcmFixture = FCMMessage(
    recipientId = "1",
    notification = FCMNotification(
        title = "Test",
        body = "Test body"
    ),
    data = FCMData(
        type = FCMDataType.MESSAGE,
        value = "Test value"
    )
)