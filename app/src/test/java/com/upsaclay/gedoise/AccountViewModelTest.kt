package com.upsaclay.gedoise

import android.net.Uri
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.UpdateProfilePictureUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.gedoise.domain.entities.AccountScreenState
import com.upsaclay.gedoise.presentation.viewmodels.AccountViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase = mockk()
    private val deleteProfilePictureUseCase: DeleteProfilePictureUseCase = mockk()
    private val userRepository: UserRepository = mockk()

    private lateinit var accountViewModel: AccountViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        coEvery { updateProfilePictureUseCase(any()) } returns Unit
        coEvery { deleteProfilePictureUseCase(any(), any()) } returns Unit

        accountViewModel = AccountViewModel(
            updateProfilePictureUseCase = updateProfilePictureUseCase,
            deleteProfilePictureUseCase = deleteProfilePictureUseCase,
            userRepository = userRepository
        )
    }

    @Test
    fun default_values_are_correct() {
        // Then
        assertEquals(null, accountViewModel.profilePictureUri)
        assertEquals(userFixture, accountViewModel.currentUser.value)
        assertEquals(AccountScreenState.READ, accountViewModel.screenState.value)
    }

    @Test
    fun updateProfilePictureUri_should_update_profilePictureUri() {
        // Given
        val uri = mockk<Uri>()

        // When
        accountViewModel.updateProfilePictureUri(uri)

        // Then
        assert(accountViewModel.profilePictureUri == uri)
    }

    @Test
    fun updateAccountScreenState_should_update_screen_state() {
        // Given
        val screenState = AccountScreenState.EDIT

        // When
        accountViewModel.updateScreenState(screenState)

        // Then
        assertEquals(screenState, accountViewModel.screenState.value)
    }

    @Test
    fun resetProfilePictureUri_should_reset_profilePictureUri() {
        // When
        accountViewModel.resetProfilePictureUri()

        // Then
        assert(accountViewModel.profilePictureUri == null)
    }

    @Test
    fun updateUserProfilePicture_should_update_profile_picture_when_uri_is_not_null() = runTest {
        // Given
        val uri = mockk<Uri>()
        accountViewModel.updateProfilePictureUri(uri)

        // When
        accountViewModel.updateUserProfilePicture()

        // Then
        coVerify { updateProfilePictureUseCase(any()) }
    }

    @Test
    fun deleteUserProfilePicture_should_reset_profile_picture_uri() = runTest {
        // Given
        val uri = mockk<Uri>()
        accountViewModel.updateProfilePictureUri(uri)

        // When
        accountViewModel.deleteUserProfilePicture()

        // Then
        assertEquals(null, accountViewModel.profilePictureUri)
    }
}