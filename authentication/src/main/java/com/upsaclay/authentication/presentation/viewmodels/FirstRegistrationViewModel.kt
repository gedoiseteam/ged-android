package com.upsaclay.authentication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.entity.RegistrationErrorType
import com.upsaclay.authentication.domain.entity.RegistrationEvent
import com.upsaclay.common.domain.extensions.uppercaseFirstLetter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FirstRegistrationViewModel: ViewModel() {
    private val _event = MutableSharedFlow<RegistrationEvent>()
    val event: SharedFlow<RegistrationEvent> = _event

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    fun updateFirstName(firstName: String) {
        _firstName.value = firstName
    }

    fun updateLastName(lastName: String) {
        _lastName.value = lastName
    }

    fun verifyNamesInputs(): Boolean {
        return if (_firstName.value.isBlank() || _lastName.value.isBlank()) {
            viewModelScope.launch { _event.emit(RegistrationEvent.Error(RegistrationErrorType.EMPTY_FIELDS_ERROR)) }
            false
        } else true
    }

    fun correctNamesInputs() {
        _firstName.value = _firstName.value.trim().uppercaseFirstLetter()
        _lastName.value = _lastName.value.trim().uppercaseFirstLetter()
    }
}