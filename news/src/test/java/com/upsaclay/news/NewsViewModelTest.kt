package com.upsaclay.news

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.userFixture
import com.upsaclay.news.domain.announcementsFixture
import com.upsaclay.news.domain.repository.AnnouncementRepository
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementUseCase
import com.upsaclay.news.presentation.viewmodels.NewsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {
    private val recreateAnnouncementUseCase: RecreateAnnouncementUseCase = mockk()
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase = mockk()
    private val refreshAnnouncementUseCase: RefreshAnnouncementUseCase = mockk()

    private val userRepository: UserRepository = mockk()
    private val announcementRepository: AnnouncementRepository = mockk()

    private lateinit var newsViewModel: NewsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { announcementRepository.announcements } returns flowOf(announcementsFixture)
        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        coEvery { refreshAnnouncementUseCase() } returns Unit

        newsViewModel = NewsViewModel(
            recreateAnnouncementUseCase = recreateAnnouncementUseCase,
            deleteAnnouncementUseCase = deleteAnnouncementUseCase,
            refreshAnnouncementUseCase = refreshAnnouncementUseCase,
            userRepository = userRepository,
            announcementRepository = announcementRepository
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        assertEquals(userFixture, newsViewModel.currentUser.value)
    }

    @Test
    fun announcements_should_be_sorted_by_date_and_truncated() = runTest {
        // Given
        val announcements = announcementsFixture
            .sortedBy { it.date }
            .map {
                it.copy(
                    title = it.title?.take(100),
                    content = it.content.take(100)
                )
        }

        // When
        val result = newsViewModel.announcements.first()

        // Then
        assertEquals(announcements, result)
    }

    @Test
    fun refreshAnnouncements_should_refresh_announcements() = runTest {
        // When
        newsViewModel.refreshAnnouncements()

        // Then
        coVerify { refreshAnnouncementUseCase() }
    }
}