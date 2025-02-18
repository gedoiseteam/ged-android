package com.upsaclay.authentication.domain

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
import com.upsaclay.authentication.domain.usecase.IsEmailVerifiedUseCase
import com.upsaclay.authentication.domain.usecase.IsUserAuthenticatedUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import com.upsaclay.authentication.domain.usecase.LogoutUseCase
import com.upsaclay.authentication.domain.usecase.RegisterUseCase
import com.upsaclay.authentication.domain.usecase.SendVerificationEmailUseCase
import com.upsaclay.authentication.domain.usecase.SetUserAuthenticatedUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotEquals

class AuthenticationUseCaseTest {
    private val authenticationRepository: AuthenticationRepository = mockk()

    private lateinit var generateHashUseCase: GenerateHashUseCase
    private lateinit var isEmailVerifiedUseCase: IsEmailVerifiedUseCase
    private lateinit var isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var sendVerificationEmailUseCase: SendVerificationEmailUseCase
    private lateinit var setUserAuthenticatedUseCase: SetUserAuthenticatedUseCase
    private lateinit var verifyEmailFormatUseCase: VerifyEmailFormatUseCase

    @Before
    fun setUp() {
        generateHashUseCase = GenerateHashUseCase()
        isEmailVerifiedUseCase = IsEmailVerifiedUseCase(authenticationRepository)
        isUserAuthenticatedUseCase = IsUserAuthenticatedUseCase(authenticationRepository)
        loginUseCase = LoginUseCase(authenticationRepository)
        logoutUseCase = LogoutUseCase(authenticationRepository)
        registerUseCase = RegisterUseCase(authenticationRepository)
        sendVerificationEmailUseCase = SendVerificationEmailUseCase(authenticationRepository)
        setUserAuthenticatedUseCase = SetUserAuthenticatedUseCase(authenticationRepository)
        verifyEmailFormatUseCase = VerifyEmailFormatUseCase

        every { authenticationRepository.isAuthenticated } returns flowOf(false)
        coEvery { authenticationRepository.isUserEmailVerified() } returns false
        coEvery { authenticationRepository.loginWithEmailAndPassword(any(), any()) } returns Unit
        coEvery { authenticationRepository.registerWithEmailAndPassword(any(), any()) } returns ""
        coEvery { authenticationRepository.logout() } returns Unit
        coEvery { authenticationRepository.sendVerificationEmail() } returns Unit
        coEvery { authenticationRepository.setAuthenticated(any()) } returns Unit
    }

    @Test
    fun `generateHashUseCase should not return same value`() {
        for (i in 0..100) {
            val hash1 = generateHashUseCase()
            val hash2 = generateHashUseCase()
            assertNotEquals(hash1, hash2)
        }
    }

    @Test
    fun `isEmailVerifiedUseCase should return false when email is not verified`() = runTest {
        // When
        val result = isEmailVerifiedUseCase()

        // Then
        assert(!result)
    }

    @Test
    fun `isEmailVerifiedUseCase should return false when email is verified`() = runTest {
        // Given
        coEvery { authenticationRepository.isUserEmailVerified() } returns true

        // When
        val result = isEmailVerifiedUseCase()

        // Then
        assert(result)
    }

    @Test
    fun `isUserAuthenticatedUseCase should return false when user is not authenticated`() = runTest {
        // When
        val result = isUserAuthenticatedUseCase().first()!!

        // Then
        assert(!result)
    }

    @Test
    fun `isUserAuthenticatedUseCase should return true when user is authenticated`() = runTest {
        // Given
        every { authenticationRepository.isAuthenticated } returns flowOf(true)

        // When
        val result = isUserAuthenticatedUseCase().first()!!

        // Then
        assert(result)
    }

    @Test
    fun `loginUseCase should login`() = runTest {
        // Given
        val email = "email"
        val password = "password"

        // When
        loginUseCase(email, password)

        // Then
        coVerify { authenticationRepository.loginWithEmailAndPassword(email, password) }
    }

    @Test
    fun `logoutUseCase should logout`() = runTest {
        // When
        logoutUseCase()

        // Then
        coVerify { authenticationRepository.logout() }
    }

    @Test
    fun `registerUseCase should register`() = runTest {
        // Given
        val email = "email"
        val password = "password"

        // When
        registerUseCase(email, password)

        // Then
        coVerify { authenticationRepository.registerWithEmailAndPassword(email, password) }
    }

    @Test
    fun `sendVerificationEmailUseCase should send verification email`() = runTest {
        // When
        sendVerificationEmailUseCase()

        // Then
        coVerify { authenticationRepository.sendVerificationEmail() }
    }

    @Test
    fun `setUserAuthenticatedUseCase should set user authenticated`() = runTest {
        // Given
        val isAuthenticated = true

        // When
        setUserAuthenticatedUseCase(isAuthenticated)

        // Then
        coVerify { authenticationRepository.setAuthenticated(isAuthenticated) }
    }

    @Test
    fun `verifyEmailFormatUseCase should return true when email is valid`() {
        // Given
        val email = "example@email.com"

        // When
        val result = verifyEmailFormatUseCase(email)

        // Then
        assert(result)
    }

    @Test
    fun `verifyEmailFormatUseCase should return false when email is invalid`() {
        // Given
        val email = "example@nonvalid"

        // When
        val result = verifyEmailFormatUseCase(email)

        // Then
        assert(!result)
    }
}