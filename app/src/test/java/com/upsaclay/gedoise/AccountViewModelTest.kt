package com.upsaclay.gedoise

import android.net.Uri
import com.upsaclay.common.domain.usecase.DeleteProfilePictureUseCase
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()

    private lateinit var accountViewModel: AccountViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        coEvery { updateProfilePictureUseCase(any()) } returns Unit
        coEvery { deleteProfilePictureUseCase(any(), any()) } returns Unit

        accountViewModel = AccountViewModel(
            updateProfilePictureUseCase = updateProfilePictureUseCase,
            deleteProfilePictureUseCase = deleteProfilePictureUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase
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
    fun updateAccountScreenState_should_update_accountScreenState() {
        // Given
        val screenState = AccountScreenState.LOADING

        // When
        accountViewModel.updateAccountScreenState(screenState)

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
        assertEquals(AccountScreenState.PROFILE_PICTURE_UPDATED, accountViewModel.screenState.value)
        coVerify { updateProfilePictureUseCase(any()) }
    }

    @Test
    fun updateUserProfilePicture_should_reset_profile_picture_uri_after_update() = runTest {
        // Given
        val uri = mockk<Uri>()
        accountViewModel.updateProfilePictureUri(uri)

        // When
        accountViewModel.updateUserProfilePicture()

        // Then
        assertEquals(null, accountViewModel.profilePictureUri)
    }

    @Test
    fun updateUserProfilePicture_should_set_profile_picture_update_error_when_exception_is_thrown() = runTest {
        // Given
        val uri = mockk<Uri>()
        accountViewModel.updateProfilePictureUri(uri)
        coEvery { updateProfilePictureUseCase(any()) } throws Exception()

        // When
        accountViewModel.updateUserProfilePicture()

        // Then
        assertEquals(AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR, accountViewModel.screenState.value)
    }

    @Test
    fun deleteUserProfilePicture_should_delete_profile_picture_when_user_is_not_null() = runTest {
        // When
        accountViewModel.deleteUserProfilePicture()

        // Then
        assertEquals(AccountScreenState.PROFILE_PICTURE_UPDATED, accountViewModel.screenState.value)
        coVerify { deleteProfilePictureUseCase(userFixture.id, userFixture.profilePictureUrl!!) }
    }

    @Test
    fun deleteUserProfilePicture_should_reset_profile_picture_uri_after_delete() = runTest {
        // When
        accountViewModel.deleteUserProfilePicture()

        // Then
        assertEquals(null, accountViewModel.profilePictureUri)
    }

    @Test
    fun deleteUserProfilePicture_should_set_profile_picture_update_error_when_exception_is_thrown() = runTest {
        // Given
        coEvery { deleteProfilePictureUseCase(any(), any()) } throws Exception()

        // When
        accountViewModel.deleteUserProfilePicture()

        // Then
        assertEquals(AccountScreenState.PROFILE_PICTURE_UPDATE_ERROR, accountViewModel.screenState.value)
    }
}