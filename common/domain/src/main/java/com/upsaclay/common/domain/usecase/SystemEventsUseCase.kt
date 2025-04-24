package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.entity.SystemEvents
import kotlinx.coroutines.flow.MutableSharedFlow

class SystemEventsUseCase {
    private val _systemEvents = MutableSharedFlow<SystemEvents>()
    val systemEvents = _systemEvents

    suspend fun sendSystemEvent(event: SystemEvents) {
        _systemEvents.emit(event)
    }
}