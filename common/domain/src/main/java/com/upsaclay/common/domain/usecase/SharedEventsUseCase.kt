package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.entity.SystemEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SharedEventsUseCase {
    private val _systemEvent = MutableSharedFlow<SystemEvent>()
    val systemEvents: SharedFlow<SystemEvent> = _systemEvent

    suspend fun sendSharedEvent(event: SystemEvent) {
        _systemEvent.emit(event)
    }
}