package com.upsaclay.authentication.domain

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
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
    private lateinit var generateHashUseCase: GenerateHashUseCase
    private lateinit var verifyEmailFormatUseCase: VerifyEmailFormatUseCase

    @Before
    fun setUp() {
        generateHashUseCase = GenerateHashUseCase()
        verifyEmailFormatUseCase = VerifyEmailFormatUseCase
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