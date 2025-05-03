package com.upsaclay.gedoise.domain.repository


interface ScreenRepository {
    val currentRoute: Any?

    fun setCurrentScreenRoute(route: Any?)
}