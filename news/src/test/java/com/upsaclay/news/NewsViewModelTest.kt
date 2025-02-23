package com.upsaclay.news

import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.news.domain.announcementsFixture
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.RecreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.RefreshAnnouncementsUseCase
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
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()
    private val refreshAnnouncementsUseCase: RefreshAnnouncementsUseCase = mockk()
    private val recreateAnnouncementUseCase: RecreateAnnouncementUseCase = mockk()
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase = mockk()

    private lateinit var newsViewModel: NewsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getAnnouncementsUseCase() } returns flowOf(announcementsFixture)
        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        coEvery { refreshAnnouncementsUseCase() } returns Unit

        newsViewModel = NewsViewModel(
            getAnnouncementsUseCase = getAnnouncementsUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            refreshAnnouncementsUseCase = refreshAnnouncementsUseCase,
            recreateAnnouncementUseCase = recreateAnnouncementUseCase,
            deleteAnnouncementUseCase = deleteAnnouncementUseCase
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        assertEquals(userFixture, newsViewModel.currentUser.value)
        assertEquals(false, newsViewModel.isRefreshing)
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
        coVerify { refreshAnnouncementsUseCase() }
        assertEquals(false, newsViewModel.isRefreshing)
    }
}