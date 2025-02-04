package com.upsaclay.news

import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementScreenState
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import com.upsaclay.news.presentation.viewmodels.EditAnnouncementViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EditAnnouncementViewModelTest {
    private val getAnnouncementUseCase: GetAnnouncementUseCase = mockk()
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase = mockk()

    private lateinit var editAnnouncementViewModel: EditAnnouncementViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val title = "Title"
    private val content = "Content"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getAnnouncementUseCase(announcementFixture.id) } returns announcementFixture
        coEvery { updateAnnouncementUseCase(announcementFixture) } returns Unit

        editAnnouncementViewModel = EditAnnouncementViewModel(
            announcementId = announcementFixture.id,
            getAnnouncementUseCase = getAnnouncementUseCase,
            updateAnnouncementUseCase = updateAnnouncementUseCase
        )
    }

    @Test
    fun default_values_are_correct() {
        assertEquals(announcementFixture, editAnnouncementViewModel.announcement.value)
        assertEquals(AnnouncementScreenState.DEFAULT, editAnnouncementViewModel.screenState.value)
        assertEquals(false, editAnnouncementViewModel.isAnnouncementModified.value)
        assertEquals(announcementFixture.title, editAnnouncementViewModel.title)
        assertEquals(announcementFixture.content, editAnnouncementViewModel.content)
    }

    @Test
    fun updateTitle_should_update_title() {
        // When
        editAnnouncementViewModel.updateTitle(title)

        // Then
        assertEquals(title, editAnnouncementViewModel.title)
    }

    @Test
    fun updateTitle_should_update_isAnnouncementModified_to_true_when_title_is_different() {
        // When
        editAnnouncementViewModel.updateTitle(title)

        // Then
        assertEquals(true, editAnnouncementViewModel.isAnnouncementModified.value)
    }

    @Test
    fun updateContent_should_update_content() {
        // When
        editAnnouncementViewModel.updateContent(content)

        // Then
        assertEquals(content, editAnnouncementViewModel.content)
    }

    @Test
    fun updateAnnouncement_should_update_announcement() = runTest {
        // Given
        editAnnouncementViewModel.updateTitle(title)
        editAnnouncementViewModel.updateContent(content)

        // When
        editAnnouncementViewModel.updateAnnouncement(announcementFixture)

        // Then
        assertEquals(announcementFixture, editAnnouncementViewModel.announcement.value)
    }

    @Test
    fun update_announcement_should_update_screen_state_to_UPDATED_when_success() = runTest {
        // Given
        editAnnouncementViewModel.updateTitle(title)
        editAnnouncementViewModel.updateContent(content)

        // When
        editAnnouncementViewModel.updateAnnouncement(announcementFixture)

        // Then
        assertEquals(AnnouncementScreenState.UPDATED, editAnnouncementViewModel.screenState.value)
    }

    @Test
    fun update_announcement_should_update_screen_state_to_UPDATE_ERROR_when_failure() = runTest {
        // Given
        editAnnouncementViewModel.updateTitle(title)
        editAnnouncementViewModel.updateContent(content)
        coEvery { updateAnnouncementUseCase(announcementFixture) } throws Exception()

        // When
        editAnnouncementViewModel.updateAnnouncement(announcementFixture)

        // Then
        assertEquals(AnnouncementScreenState.UPDATE_ERROR, editAnnouncementViewModel.screenState.value)
    }
}