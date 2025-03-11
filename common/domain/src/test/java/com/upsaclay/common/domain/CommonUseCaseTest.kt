package com.upsaclay.common.domain

import android.net.Uri
import com.upsaclay.common.domain.repository.DrawableRepository
import com.upsaclay.common.domain.repository.FileRepository
import com.upsaclay.common.domain.repository.ImageRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.ConvertDateUseCase
import com.upsaclay.common.domain.usecase.CreateUserUseCase
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.FormatLocalDateTimeUseCase
import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetDrawableUriUseCase
import com.upsaclay.common.domain.usecase.GetElapsedTimeUseCase
import com.upsaclay.common.domain.usecase.GetUserUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.common.domain.usecase.IsUserExistUseCase
import com.upsaclay.common.domain.usecase.SetCurrentUserUseCase
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
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var deleteProfilePictureUseCase: DeleteProfilePictureUseCase
    private lateinit var formatLocalDateTimeUseCase: FormatLocalDateTimeUseCase
    private lateinit var generateIdUseCase: GenerateIdUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var getDrawableUriUseCase: GetDrawableUriUseCase
    private lateinit var getElapsedTimeUseCase: GetElapsedTimeUseCase
    private lateinit var getUsersUseCase: GetUsersUseCase
    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var isUserExistUseCase: IsUserExistUseCase
    private lateinit var setCurrentUserUseCase: SetCurrentUserUseCase
    private lateinit var updateProfilePictureUseCase: UpdateProfilePictureUseCase
    private lateinit var verifyEmailFormatUseCase: VerifyEmailFormatUseCase

    private val uri: Uri = mockk()
    private val file: File = File("file")

    @Before
    fun setUp() {
        convertDateUseCase = ConvertDateUseCase
        createUserUseCase = CreateUserUseCase(userRepository = userRepository)
        deleteProfilePictureUseCase = DeleteProfilePictureUseCase(
            userRepository = userRepository,
            imageRepository = imageRepository
        )
        formatLocalDateTimeUseCase = FormatLocalDateTimeUseCase
        generateIdUseCase = GenerateIdUseCase
        getCurrentUserUseCase = GetCurrentUserUseCase(userRepository = userRepository)
        getDrawableUriUseCase = GetDrawableUriUseCase(drawableRepository = drawableRepository)
        getElapsedTimeUseCase = GetElapsedTimeUseCase
        getUsersUseCase = GetUsersUseCase(userRepository = userRepository)
        getUserUseCase = GetUserUseCase(userRepository = userRepository)
        isUserExistUseCase = IsUserExistUseCase(userRepository = userRepository)
        setCurrentUserUseCase = SetCurrentUserUseCase(userRepository = userRepository)
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
    fun createUserUseCase_should_create_user() = runTest {
        // When
        createUserUseCase(userFixture)

        // Then
        coVerify { userRepository.createUser(userFixture) }
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
    fun getCurrentUserUseCase_should_return_current_user() = runTest {
        // When
        val result = getCurrentUserUseCase().value

        // Then
        coVerify { userRepository.currentUser }
        assertEquals(userFixture, result)
    }

    @Test
    fun getDrawableUriUseCase_should_return_drawable_uri() {
        // When
        val result = getDrawableUriUseCase(0)

        // Then
        coVerify { drawableRepository.getDrawableUri(0) }
        assertEquals(uri, result)
    }

    @Test
    fun getUsersUseCase_should_return_users() = runTest {
        // When
        val result = getUsersUseCase()

        // Then
        coVerify { userRepository.getUsers() }
        assertEquals(usersFixture, result)
    }

    @Test
    fun getUserUseCase_with_email_should_return_user() = runTest {
        // When
        val result = getUserUseCase.withEmail(userFixture.email)

        // Then
        coVerify { userRepository.getUserWithEmail(userFixture.email) }
        assertEquals(userFixture, result)
    }

    @Test
    fun getUserUseCase_with_id_should_return_user() = runTest {
        // When
        val result = getUserUseCase.withId(userFixture.id)

        // Then
        coVerify { userRepository.getUser(userFixture.id) }
        assertEquals(userFixture, result)
    }

    @Test
    fun getUserUseCase_with_email_should_return_null_when_user_does_not_exist() = runTest {
        // Given
        coEvery { userRepository.getUserWithEmail(any()) } returns null

        // When
        val result = getUserUseCase.withEmail(userFixture.email)

        // Then
        coVerify { userRepository.getUserWithEmail(userFixture.email) }
        assertEquals(null, result)
    }

    @Test
    fun getUserUseCase_with_id_should_return_null_when_user_does_not_exist() = runTest {
        // Given
        coEvery { userRepository.getUser(any()) } returns null

        // When
        val result = getUserUseCase.withId(userFixture.id)

        // Then
        coVerify { userRepository.getUser(userFixture.id) }
        assertEquals(null, result)
    }

    @Test
    fun isUserExistUseCase_should_return_false_when_user_does_not_exist() = runTest {
        // When
        val result = isUserExistUseCase(userFixture.email)

        // Then
        coVerify { userRepository.isUserExist(userFixture.email) }
        assertEquals(false, result)
    }

    @Test
    fun isUserExistUseCase_should_return_true_when_user_exists() = runTest {
        // Given
        coEvery { userRepository.isUserExist(any()) } returns true

        // When
        val result = isUserExistUseCase(userFixture.email)

        // Then
        coVerify { userRepository.isUserExist(userFixture.email) }
        assertEquals(true, result)
    }

    @Test
    fun setCurrentUserUseCase_should_set_current_user() = runTest {
        // When
        setCurrentUserUseCase(userFixture)

        // Then
        coVerify { userRepository.setCurrentUser(userFixture) }
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