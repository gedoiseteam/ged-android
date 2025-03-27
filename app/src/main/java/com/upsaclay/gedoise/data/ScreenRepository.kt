package com.upsaclay.gedoise.data

import com.upsaclay.common.domain.entity.Screen

interface ScreenRepository {
    val currentScreen: Screen?

    fun setCurrentScreen(screen: Screen?)
}