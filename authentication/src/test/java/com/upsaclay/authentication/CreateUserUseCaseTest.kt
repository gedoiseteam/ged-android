package com.upsaclay.authentication

import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import com.upsaclay.common.utils.userFixture
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateUserUseCaseTest {
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var userRepository: com.upsaclay.common.domain.repository.UserRepository
    private lateinit var updateUserProfilePictureUseCase: UpdateUserProfilePictureUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        updateUserProfilePictureUseCase = mockk()
        createUserUseCase = CreateUserUseCase(userRepository)

        coEvery { userRepository.createUser(any()) } returns Result.success(0)
        coEvery { updateUserProfilePictureUseCase(any()) } returns Result.success(Unit)
    }

    @Test
    fun registration_should_call_create_user() {
        runTest {
            createUserUseCase(userFixture)
            coVerify(exactly = 1) { userRepository.createUser(any()) }
        }
    }

    @Test
    fun registration_should_update_profile_picture_url_if_not_null() {
        runTest {
            createUserUseCase(userFixture)
            coVerify(exactly = 1) { updateUserProfilePictureUseCase(any()) }
        }
    }
}