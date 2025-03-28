package com.upsaclay.news

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.userFixture
import com.upsaclay.news.domain.announcementFixture
import com.upsaclay.news.domain.entity.AnnouncementEvent
import com.upsaclay.news.domain.repository.AnnouncementRepository
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementFlowUseCase
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
import java.net.ConnectException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ReadAnnouncementViewModelTest {
    private val getAnnouncementFlowUseCase: GetAnnouncementFlowUseCase = mockk()
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase = mockk()

    private val userRepository: UserRepository = mockk()
    private val announcementRepository: AnnouncementRepository = mockk()

    private lateinit var readAnnouncementViewModel: ReadAnnouncementViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userRepository.currentUser } returns MutableStateFlow(announcementFixture.author)
        every { announcementRepository.getAnnouncement(announcementFixture.id) } returns announcementFixture
        every { getAnnouncementFlowUseCase(any()) } returns MutableStateFlow(announcementFixture)
        coEvery { deleteAnnouncementUseCase(announcementFixture) } returns Unit

        readAnnouncementViewModel = ReadAnnouncementViewModel(
            announcementId = announcementFixture.id,
            getAnnouncementFlowUseCase = getAnnouncementFlowUseCase,
            deleteAnnouncementUseCase = deleteAnnouncementUseCase,
            userRepository = userRepository,
            announcementRepository = announcementRepository
        )
    }

    @Test
    fun default_values_are_correct() {
        assertEquals(announcementFixture, readAnnouncementViewModel.announcement.value)
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
        every { announcementRepository.getAnnouncement(announcementFixture.id) } returns null
        every { getAnnouncementFlowUseCase(any()) } returns flowOf()

        readAnnouncementViewModel = ReadAnnouncementViewModel(
            announcementId = announcementFixture.id,
            userRepository = userRepository,
            getAnnouncementFlowUseCase = getAnnouncementFlowUseCase,
            deleteAnnouncementUseCase = deleteAnnouncementUseCase,
            announcementRepository = announcementRepository
        )

        // When
        readAnnouncementViewModel.deleteAnnouncement()

        // Then
        coVerify(exactly = 0) { deleteAnnouncementUseCase(announcementFixture) }
    }
}