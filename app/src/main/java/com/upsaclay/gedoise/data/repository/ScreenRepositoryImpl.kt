package com.upsaclay.gedoise.data.repository

import com.upsaclay.gedoise.domain.repository.ScreenRepository
import com.upsaclay.common.domain.entity.ScreenRoute

internal class ScreenRepositoryImpl: ScreenRepository {
    private var _currentScreenRoute: ScreenRoute? = null
    override val currentScreenRoute: ScreenRoute?
        get() = _currentScreenRoute

    override fun setCurrentScreenRoute(screenRoute: ScreenRoute?) {
        _currentScreenRoute = screenRoute
    }
}