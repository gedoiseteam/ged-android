package com.upsaclay.gedoise.data

import com.upsaclay.common.domain.entity.Screen

class ScreenRepositoryImpl: ScreenRepository {
    private var _currentScreen: Screen? = null
    override val currentScreen: Screen?
        get() = _currentScreen

    override fun setCurrentScreen(screen: Screen?) {
        _currentScreen = screen
    }
}