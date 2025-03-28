package com.upsaclay.gedoise.data

import com.upsaclay.common.domain.entity.ScreenRoute

interface ScreenRepository {
    val currentScreenRoute: ScreenRoute?

    fun setCurrentScreen(screenRoute: ScreenRoute?)
}