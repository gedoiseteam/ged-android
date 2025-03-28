package com.upsaclay.gedoise.data

import com.upsaclay.common.domain.entity.ScreenRoute

class ScreenRepositoryImpl: ScreenRepository {
    private var _currentScreenRoute: ScreenRoute? = null
    override val currentScreenRoute: ScreenRoute?
        get() = _currentScreenRoute

    override fun setCurrentScreen(screenRoute: ScreenRoute?) {
        _currentScreenRoute = screenRoute
    }
}