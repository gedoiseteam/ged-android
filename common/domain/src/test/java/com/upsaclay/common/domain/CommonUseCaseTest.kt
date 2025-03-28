package com.upsaclay.common.domain

import android.net.Uri
import com.upsaclay.common.domain.repository.DrawableRepository
import com.upsaclay.common.domain.repository.FileRepository
import com.upsaclay.common.domain.repository.ImageRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.FormatLocalDateTimeUseCase
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetElapsedTimeUseCase
import com.upsaclay.common.domain.usecase.UpdateProfilePictureUseCase
import com.upsaclay.common.domain.usecase.VerifyEmailFormatUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CommonUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val imageRepository: ImageRepository = mockk()
    private val drawableRepository: DrawableRepository = mockk()
    private val fileRepository: FileRepository = mockk()

    private lateinit var convertDateUseCase: ConvertDateUseCase
    private lateinit var deleteProfilePictureUseCase: DeleteProfilePictureUseCase
    private lateinit var formatLocalDateTimeUseCase: FormatLocalDateTimeUseCase
    private lateinit var generateIdUseCase: GenerateIdUseCase
    private lateinit var getElapsedTimeUseCase: GetElapsedTimeUseCase
    private lateinit var updateProfilePictureUseCase: UpdateProfilePictureUseCase
    private lateinit var verifyEmailFormatUseCase: VerifyEmailFormatUseCase

    private val uri: Uri = mockk()
    private val file: File = File("file")

    @Before
    fun setUp() {
        convertDateUseCase = ConvertDateUseCase
        deleteProfilePictureUseCase = DeleteProfilePictureUseCase(
            userRepository = userRepository,
            imageRepository = imageRepository
        )
        formatLocalDateTimeUseCase = FormatLocalDateTimeUseCase
        generateIdUseCase = GenerateIdUseCase
        getElapsedTimeUseCase = GetElapsedTimeUseCase
        updateProfilePictureUseCase = UpdateProfilePictureUseCase(
            fileRepository = fileRepository,
            imageRepository = imageRepository,
            userRepository = userRepository
        )
        verifyEmailFormatUseCase = VerifyEmailFormatUseCase

        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        every { drawableRepository.getDrawableUri(any()) } returns uri
        every { fileRepository.getFileType(any()) } returns "jpg"
        coEvery { userRepository.getUsers() } returns usersFixture
        coEvery { userRepository.getUser(any()) } returns userFixture
        coEvery { userRepository.getUserFlow(any()) } returns flowOf(userFixture)
        coEvery { userRepository.getUserWithEmail(any()) } returns userFixture
        coEvery { userRepository.createUser(any()) } returns Unit
        coEvery { userRepository.setCurrentUser(any()) } returns Unit
        coEvery { userRepository.deleteCurrentUser() } returns Unit
        coEvery { userRepository.updateProfilePictureUrl(any(), any()) } returns Unit
        coEvery { userRepository.deleteProfilePictureUrl(any()) } returns Unit
        coEvery { userRepository.isUserExist(any()) } returns false
        coEvery { imageRepository.uploadImage(any()) } returns Unit
        coEvery { imageRepository.deleteImage(any()) } returns Unit
        coEvery { fileRepository.createFileFromUri(any(), any()) } returns file
        coEvery { fileRepository.createFileFromByteArray(any(), any()) } returns file
    }

    @Test
    fun deleteProfilePictureUseCase_should_delete_image() = runTest {
        // Given
        val url = "url"

        // When
        deleteProfilePictureUseCase(userFixture.id, url)

        // Then
        coVerify { userRepository.deleteProfilePictureUrl(userFixture.id) }
        coVerify { imageRepository.deleteImage(url.substringAfterLast("/")) }
    }

    @Test
    fun generateIdUseCase_should_generate_unique_id() {
        for (i in 0..100) {
            // When
            val id1 = generateIdUseCase.asString()
            val id2 = generateIdUseCase.asString()

            // Then
            assertNotEquals(id1, id2)
        }
    }

    @Test
    fun updateProfilePictureUseCase_should_update_profile_picture() = runTest {
        // When
        updateProfilePictureUseCase(uri)

        // Then
        coVerify { userRepository.updateProfilePictureUrl(userFixture.id, any()) }
        coVerify { imageRepository.uploadImage(any()) }
    }

    @Test
    fun updateProfilePictureUseCase_should_delete_previous_profile_picture() = runTest {
        // When
        updateProfilePictureUseCase(uri)

        // Then
        coVerify { userRepository.updateProfilePictureUrl(userFixture.id, any()) }
        coVerify { imageRepository.deleteImage(userFixture.profilePictureUrl!!.substringAfterLast("/")) }
    }

    @Test
    fun verifyEmailFormatUseCase_should_return_true_when_email_format_is_correct() {
        // When
        val result = verifyEmailFormatUseCase(userFixture.email)

        // Then
        assertEquals(true, result)
    }

    @Test
    fun verifyEmailFormatUseCase_should_return_false_when_email_format_is_incorrect() {
        // When
        val result = verifyEmailFormatUseCase("email")

        // Then
        assertEquals(false, result)
    }
}