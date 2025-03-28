package com.upsaclay.news

import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementEvent
import com.upsaclay.news.domain.repository.AnnouncementRepository
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
import java.net.ConnectException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EditAnnouncementViewModelTest {
    private val announcementRepository: AnnouncementRepository = mockk()

    private lateinit var editAnnouncementViewModel: EditAnnouncementViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val title = "Title"
    private val content = "Content"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { announcementRepository.getAnnouncement(announcementFixture.id) } returns announcementFixture
        coEvery { announcementRepository.updateAnnouncement(announcementFixture) } returns Unit

        editAnnouncementViewModel = EditAnnouncementViewModel(
            announcementId = announcementFixture.id,
            announcementRepository = announcementRepository
        )
    }

    @Test
    fun default_values_are_correct() {
        assertEquals(announcementFixture, editAnnouncementViewModel.announcement.value)
        assertEquals(false, editAnnouncementViewModel.isAnnouncementModified.value)
        assertEquals(announcementFixture.title, editAnnouncementViewModel.title.value)
        assertEquals(announcementFixture.content, editAnnouncementViewModel.content.value)
    }

    @Test
    fun updateTitle_should_update_title() {
        // When
        editAnnouncementViewModel.updateTitle(title)

        // Then
        assertEquals(title, editAnnouncementViewModel.title.value)
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
        assertEquals(content, editAnnouncementViewModel.content.value)
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
}