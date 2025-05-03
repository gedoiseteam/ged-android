package com.upsaclay.common.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Phone",
    group = "phones",
    device = Devices.PHONE,
    showBackground = true
)

@Preview(
    name = "Phone Dark",
    group = "phones",
    device = Devices.PHONE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class Phones
