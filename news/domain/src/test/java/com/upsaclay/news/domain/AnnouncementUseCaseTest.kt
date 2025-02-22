package com.upsaclay.news.domain

import com.upsaclay.news.domain.repository.AnnouncementRepository
import com.upsaclay.news.domain.usecase.CreateAnnouncementUseCase
import com.upsaclay.news.domain.usecase.DeleteAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementUseCase
import com.upsaclay.news.domain.usecase.GetAnnouncementsUseCase
import com.upsaclay.news.domain.usecase.UpdateAnnouncementUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AnnouncementUseCaseTest {
    private val announcementRepository: AnnouncementRepository = mockk()

    private lateinit var createAnnouncementUseCase: CreateAnnouncementUseCase
    private lateinit var deleteAnnouncementUseCase: DeleteAnnouncementUseCase
    private lateinit var getAnnouncementsUseCase: GetAnnouncementsUseCase
    private lateinit var getAnnouncementUseCase: GetAnnouncementUseCase
    private lateinit var updateAnnouncementUseCase: UpdateAnnouncementUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        createAnnouncementUseCase = CreateAnnouncementUseCase(
            announcementRepository = announcementRepository,
            scope = testScope
        )
        deleteAnnouncementUseCase = DeleteAnnouncementUseCase(
            announcementRepository = announcementRepository,
            scope = testScope
        )
        getAnnouncementsUseCase = GetAnnouncementsUseCase(announcementRepository = announcementRepository)
        getAnnouncementUseCase = GetAnnouncementUseCase(announcementRepository = announcementRepository)
        updateAnnouncementUseCase = UpdateAnnouncementUseCase(announcementRepository = announcementRepository)

        coEvery { announcementRepository.createAnnouncement(any()) } returns Unit
        coEvery { announcementRepository.updateAnnouncement(any()) } returns Unit
        coEvery { announcementRepository.deleteAnnouncement(any()) } returns Unit
        coEvery { announcementRepository.updateAnnouncement(any()) } returns Unit
        every { announcementRepository.announcements } returns MutableStateFlow(listOf(announcementFixture))
        every { announcementRepository.getAnnouncement(any()) } returns announcementFixture
    }

    @Test
    fun create_announcement_use_case_should_create_announcement() = runTest {
        // When
        createAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.createAnnouncement(announcementFixture) }
    }

    @Test
    fun delete_announcement_use_case_should_delete_announcement() = runTest {
        // When
        deleteAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.deleteAnnouncement(announcementFixture) }
    }

    @Test
    fun get_announcements_use_case_should_return_announcements() = runTest {
        // When
        val result = getAnnouncementsUseCase()

        // Then
        assert(result.first().contains(announcementFixture))
    }

    @Test
    fun get_announcement_use_case_should_return_announcement() = runTest {
        // When
        val result = getAnnouncementUseCase(announcementFixture.id)

        // Then
        assertEquals(announcementFixture, result)
    }

    @Test
    fun update_announcement_should_update_announcement() = runTest {
        // When
        updateAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.updateAnnouncement(announcementFixture) }
    }
}