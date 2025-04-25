package com.upsaclay.common.domain.usecase

import com.upsaclay.common.domain.entity.SharedEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SharedEventsUseCase {
    private val _sharedEvent = MutableSharedFlow<SharedEvent>()
    val sharedEvents: SharedFlow<SharedEvent> = _sharedEvent

    suspend fun sendSharedEvent(event: SharedEvent) {
        _sharedEvent.emit(event)
    }
}