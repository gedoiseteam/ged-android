package com.upsaclay.authentication.presentation.registration

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upsaclay.authentication.domain.model.RegistrationState
import com.upsaclay.authentication.domain.model.exception.AuthenticationException
import com.upsaclay.authentication.domain.model.exception.FirebaseAuthErrorCode
import com.upsaclay.authentication.domain.model.exception.TooManyRequestException
import com.upsaclay.authentication.domain.usecase.IsUserEmailVerifiedUseCase
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.VerifyEmailFormatUseCase
import com.upsaclay.common.domain.uppercaseFirstLetter
import com.upsaclay.common.domain.model.User
import com.upsaclay.common.domain.usecase.GetCurrentUserFlowUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyEmailFormatUseCase: VerifyEmailFormatUseCase,
    private val isUserExistUseCase: IsUserExistUseCase,
    private val updateUserProfilePictureUseCase: UpdateUserProfilePictureUseCase,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val isUserEmailVerifiedUseCase: IsUserEmailVerifiedUseCase,
    private val setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val _registrationState = MutableStateFlow(RegistrationState.NOT_REGISTERED)
    val registrationState: StateFlow<RegistrationState> = _registrationState
    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    val schoolLevels = persistentListOf("GED 1", "GED 2", "GED 3", "GED 4")
    var schoolLevel by mutableStateOf(schoolLevels[0])
        private set
    var profilePictureUri: Uri? by mutableStateOf(null)
        private set

    fun updateFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun updateLastName(lastName: String) {
        this.lastName = lastName
    }

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun updateSchoolLevel(schoolLevel: String) {
        this.schoolLevel = schoolLevel
    }

    fun updateProfilePictureUri(uri: Uri?) {
        uri?.let { profilePictureUri = it }
    }

    fun resetFirstName() {
        firstName = ""
    }

    fun resetLastName() {
        lastName = ""
    }

    fun resetEmail() {
        email = ""
    }

    fun resetPassword() {
        password = ""
    }

    fun resetProfilePictureUri() {
        profilePictureUri = null
    }

    fun resetSchoolLevel() {
        schoolLevel = schoolLevels[0]
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.NOT_REGISTERED
    }

    fun getCurrentUserIfNeeded() {
        if(email.isBlank()) {
            _registrationState.value = RegistrationState.LOADING

            viewModelScope.launch {
                getCurrentUserUseCase()?.let { user ->
                    firstName = user.firstName
                    lastName = user.lastName
                    email = user.email
                    schoolLevel = user.schoolLevel
                    _registrationState.value = RegistrationState.OK
                } ?: run {
                    _registrationState.value = RegistrationState.UNRECOGNIZED_ACCOUNT
                }
            }
        }
    }

    fun verifyNamesInputs(): Boolean {
        return if(firstName.isBlank() || lastName.isBlank()) {
            _registrationState.value = RegistrationState.INPUTS_EMPTY_ERROR
            false
        } else {
            firstName = firstName.uppercaseFirstLetter().trim()
            lastName = lastName.uppercaseFirstLetter().trim()
            true
        }
    }

    fun verifyPasswordFormat(): Boolean {
        return when {
            !checkEmptyEmailAndPassword() -> {
                _registrationState.value = RegistrationState.INPUTS_EMPTY_ERROR
                false
            }
            password.length >= 8 -> true
            else -> {
                _registrationState.value = RegistrationState.PASSWORD_LENGTH_ERROR
                false
            }
        }
    }

    fun verifyEmailFormat(): Boolean {
        return when {
            !checkEmptyEmailAndPassword() -> {
                _registrationState.value = RegistrationState.INPUTS_EMPTY_ERROR
                false
            }
            verifyEmailFormatUseCase(email.trim()) -> true
            else -> {
                _registrationState.value = RegistrationState.EMAIL_FORMAT_ERROR
                false
            }
        }
    }

    private fun checkEmptyEmailAndPassword(): Boolean = email.isNotBlank() && password.isNotBlank()

    fun verifyIsUserAlreadyExist() {
        _registrationState.value = RegistrationState.LOADING

        viewModelScope.launch {
            isUserExistUseCase(email.trim())
                .onSuccess { isExist ->
                    _registrationState.value = if(isExist) {
                         RegistrationState.USER_ALREADY_EXIST
                    } else {
                        RegistrationState.USER_NOT_EXIST
                    }
                }
                .onFailure { _registrationState.value = RegistrationState.ERROR }
        }
    }

    fun register() {
        _registrationState.value = RegistrationState.LOADING

        viewModelScope.launch {
            val user = User(
                firstName = firstName,
                lastName = lastName,
                email = email.trim(),
                schoolLevel = schoolLevel
            )

            registerUseCase(email.trim(), password)
                .onSuccess {
                    createUserUseCase(user)
                        .onSuccess {
                            setUserAuthenticatedUseCase(true)
                            _registrationState.value = RegistrationState.REGISTERED
                        }
                        .onFailure {
                            setUserAuthenticatedUseCase(false)
                            _registrationState.value = RegistrationState.ERROR
                        }
                }
                .onFailure { e ->
                    if(e is AuthenticationException) {
                        _registrationState.value = when(e.code) {
                            FirebaseAuthErrorCode.EMAIL_ALREADY_EXIST -> RegistrationState.USER_ALREADY_EXIST
                            else -> RegistrationState.ERROR
                        }
                    }
                    _registrationState.value = RegistrationState.ERROR
                }
        }
    }

    fun sendVerificationEmail() {
        _registrationState.value = RegistrationState.LOADING

        viewModelScope.launch {
            sendVerificationEmailUseCase.sendVerificationEmail()
                .onSuccess { _registrationState.value = RegistrationState.OK }
                .onFailure { e ->
                    when(e) {
                        is AuthenticationException -> {
                            _registrationState.value = when(e.code) {
                                FirebaseAuthErrorCode.EMAIL_ALREADY_EXIST -> RegistrationState.USER_ALREADY_EXIST
                                else -> RegistrationState.ERROR
                            }
                        }
                        is TooManyRequestException -> _registrationState.value = RegistrationState.TOO_MANY_REQUESTS

                        else -> _registrationState.value = RegistrationState.ERROR
                    }
                }
        }
    }

    fun verifyIsEmailVerified() {
        _registrationState.value = RegistrationState.LOADING

        viewModelScope.launch {
            if(isUserEmailVerifiedUseCase()) {
                delay(900)
                _registrationState.value = RegistrationState.EMAIL_VERIFIED
            } else {
                delay(900)
                _registrationState.value = RegistrationState.EMAIL_NOT_VERIFIED
            }
        }
    }

    fun updateUserProfilePicture() {
        _registrationState.value = RegistrationState.LOADING

        profilePictureUri?.let {
            viewModelScope.launch {
                updateUserProfilePictureUseCase(profilePictureUri!!)
                    .onSuccess { _registrationState.value = RegistrationState.OK }
                    .onFailure { _registrationState.value = RegistrationState.ERROR }
            }
        } ?: run { _registrationState.value = RegistrationState.OK }
    }
}