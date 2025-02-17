package com.upsaclay.news

import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementFlowUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.presentation.viewmodels.ReadAnnouncementViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ReadAnnouncementViewModelTest {
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val getAnnouncementUseCase: GetAnnouncementUseCase = mockk()
    private val getAnnouncementFlowUseCase: GetAnnouncementFlowUseCase = mockk()
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase = mockk()
    private val recreateAnnouncementUseCase: RecreateAnnouncementUseCase = mockk()

    private lateinit var readAnnouncementViewModel: ReadAnnouncementViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getCurrentUserUseCase() } returns MutableStateFlow(announcementFixture.author)
        every { getAnnouncementUseCase(announcementFixture.id) } returns announcementFixture
        every { getAnnouncementFlowUseCase(any()) } returns MutableStateFlow(announcementFixture)
        coEvery { deleteAnnouncementUseCase(announcementFixture) } returns Unit

        readAnnouncementViewModel = ReadAnnouncementViewModel(
            announcementId = announcementFixture.id,
            getCurrentUserUseCase = getCurrentUserUseCase,
            getAnnouncementUseCase = getAnnouncementUseCase,
            getAnnouncementFlowUseCase = getAnnouncementFlowUseCase,
            deleteAnnouncementUseCase = deleteAnnouncementUseCase,
            recreateAnnouncementUseCase = recreateAnnouncementUseCase
        )
    }

    @Test
    fun default_values_are_correct() {
        assertEquals(announcementFixture, readAnnouncementViewModel.announcement.value)
        assertEquals(AnnouncementScreenState.DEFAULT, readAnnouncementViewModel.screenState.value)
        assertEquals(userFixture, readAnnouncementViewModel.currentUser.value)
    }

    @Test
    fun deleteAnnouncement_should_delete_announcement() {
        // When
        readAnnouncementViewModel.deleteAnnouncement()

        // Then
        coVerify { deleteAnnouncementUseCase(announcementFixture) }
    }

    @Test
    fun deleteAnnouncement_should_not_delete_announcement_when_announcement_is_null() {
        // Given
        every { getAnnouncementUseCase(announcementFixture.id) } returns null
        every { getAnnouncementFlowUseCase(any()) } returns flowOf()

        readAnnouncementViewModel = ReadAnnouncementViewModel(
            announcementId = announcementFixture.id,
            getCurrentUserUseCase = getCurrentUserUseCase,
            getAnnouncementUseCase = getAnnouncementUseCase,
            getAnnouncementFlowUseCase = getAnnouncementFlowUseCase,
            deleteAnnouncementUseCase = deleteAnnouncementUseCase,
            recreateAnnouncementUseCase = recreateAnnouncementUseCase
        )

        // When
        readAnnouncementViewModel.deleteAnnouncement()

        // Then
        coVerify(exactly = 0) { deleteAnnouncementUseCase(announcementFixture) }
    }

    @Test
    fun deleteAnnouncement_should_update_screen_state_to_DELETED_when_success() {
        // When
        readAnnouncementViewModel.deleteAnnouncement()

        // Then
        assertEquals(AnnouncementScreenState.DELETED, readAnnouncementViewModel.screenState.value)
    }

    @Test
    fun deleteAnnouncement_should_update_screen_state_to_DELETE_ERROR_when_exception_is_thrown() {
        // Given
        coEvery { deleteAnnouncementUseCase(announcementFixture) } throws Exception()

        // When
        readAnnouncementViewModel.deleteAnnouncement()

        // Then
        assertEquals(AnnouncementScreenState.DELETE_ERROR, readAnnouncementViewModel.screenState.value)
    }
}