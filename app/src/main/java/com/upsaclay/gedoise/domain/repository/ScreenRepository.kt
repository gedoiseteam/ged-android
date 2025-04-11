package com.upsaclay.gedoise.domain.repository

import com.upsaclay.common.domain.entity.ScreenRoute

interface ScreenRepository {
    val currentScreenRoute: ScreenRoute?

    fun setCurrentScreenRoute(screenRoute: ScreenRoute?)
}