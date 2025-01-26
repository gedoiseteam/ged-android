package com.upsaclay.authentication

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.authentication.domain.repository.FirebaseAuthenticationRepository
import com.upsaclay.authentication.domain.usecase.GenerateHashUseCase
import com.upsaclay.authentication.domain.usecase.LoginUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginWithParisSaclayUseCaseTest {
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var generateHashUseCase: GenerateHashUseCase
    private lateinit var firebaseAuthenticationRepository: FirebaseAuthenticationRepository
    private val email = "firstname.name@universite-paris-saclay.fr"
    private val password = "strongpassword12345"
    private val hash = "9f64d054ac7498f0a16d46460ec8e4c2f0a1d2a02692652dd01aa3e25d9a72f5"

    @Before
    fun setUp() {
        generateHashUseCase = mockk()
        firebaseAuthenticationRepository = mockk()
        loginUseCase = LoginUseCase(firebaseAuthenticationRepository)

        coEvery {
            firebaseAuthenticationRepository.registerWithEmailAndPassword(any(), any())
        } returns ""
        coEvery { generateHashUseCase() } returns hash
    }

    @Test
    fun login_with_paris_saclay_call_generate_hash_once() = runTest {
        loginUseCase(email, password)
        verify(exactly = 1) { generateHashUseCase() }
    }

    @Test
    fun login_with_paris_saclay_return_success_when_login_is_correct() = runTest {
        loginUseCase(email, password)
    }

    @Test
    fun login_with_paris_saclay_return_fail_when_login_is_incorrect() = runTest {
        loginUseCase(email, password)
    }
}