package com.upsaclay.authentication

import android.net.Uri
import com.upsaclay.common.domain.usecase.CreateNewUserUseCase
import com.upsaclay.common.domain.usecase.UpdateUserProfilePictureUseCase
import com.upsaclay.common.utils.userFixture
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateNewUserUseCaseTest {
    private lateinit var createNewUserUseCase: CreateNewUserUseCase
    private lateinit var userRepository: com.upsaclay.common.domain.repository.UserRepository
    private lateinit var updateUserProfilePictureUseCase: UpdateUserProfilePictureUseCase

    @Before
    fun setUp() {
        userRepository = mockk()
        updateUserProfilePictureUseCase = mockk()
        createNewUserUseCase = CreateNewUserUseCase(userRepository)

        coEvery { userRepository.createUserWithOracle(any()) } returns 0
        coEvery { updateUserProfilePictureUseCase(any()) } returns Result.success(Unit)
    }

    @Test
    fun registration_should_call_create_user() {
        runTest {
            createNewUserUseCase(userFixture)
            coVerify(exactly = 1) { userRepository.createUserWithOracle(any()) }
        }
    }

    @Test
    fun registration_should_update_profile_picture_url_if_not_null() {
        runTest {
            createNewUserUseCase(userFixture)
            coVerify(exactly = 1) { updateUserProfilePictureUseCase(any()) }
        }
    }
}