package com.upsaclay.news.domain

import com.upsaclay.news.domain.entity.AnnouncementState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AnnouncementUseCaseTest {
    private val announcementRepository: AnnouncementRepository = mockk()

    private lateinit var createAnnouncementUseCase: CreateAnnouncementUseCase
    private lateinit var deleteAnnouncementUseCase: DeleteAnnouncementUseCase
    private lateinit var getAnnouncementsUseCase: GetAnnouncementsUseCase
    private lateinit var getAnnouncementUseCase: GetAnnouncementUseCase
    private lateinit var updateAnnouncementUseCase: UpdateAnnouncementUseCase

    @Before
    fun setUp() {
        createAnnouncementUseCase = CreateAnnouncementUseCase(announcementRepository)
        deleteAnnouncementUseCase = DeleteAnnouncementUseCase(announcementRepository)
        getAnnouncementsUseCase = GetAnnouncementsUseCase(announcementRepository)
        getAnnouncementUseCase = GetAnnouncementUseCase(announcementRepository)
        updateAnnouncementUseCase = UpdateAnnouncementUseCase(announcementRepository)

        coEvery { announcementRepository.createAnnouncement(any()) } returns Unit
        coEvery { announcementRepository.updateAnnouncement(any()) } returns Unit
        coEvery { announcementRepository.deleteAnnouncement(any()) } returns Unit
        coEvery { announcementRepository.updateAnnouncement(any()) } returns Unit
        every { announcementRepository.announcements } returns MutableStateFlow(listOf(announcementFixture))
        every { announcementRepository.getAnnouncement(any()) } returns announcementFixture
    }

    @Test
    fun `createAnnouncementUseCase should create announcement`() = runTest {
        // When
        createAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.createAnnouncement(announcementFixture) }
        coVerify { announcementRepository.updateAnnouncement(announcementFixture.copy(state = AnnouncementState.DEFAULT)) }
    }

    @Test
    fun `createAnnouncementUseCase should update announcement state to error when error`() = runTest {
        // Given
        coEvery { announcementRepository.createAnnouncement(any()) } throws Exception()

        // When
        createAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.updateAnnouncement(announcementFixture.copy(state = AnnouncementState.ERROR)) }
    }

    @Test
    fun `deleteAnnouncementUseCase should delete announcement`() = runTest {
        // When
        deleteAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.deleteAnnouncement(announcementFixture) }
    }

    @Test
    fun `getAnnouncementsUseCase should return announcements`() = runTest {
        // When
        val result = getAnnouncementsUseCase()

        // Then
        assert(result.first().contains(announcementFixture))
    }

    @Test
    fun `getAnnouncementUseCase should return announcement`() = runTest {
        // When
        val result = getAnnouncementUseCase(announcementFixture.id)

        // Then
        assertEquals(announcementFixture, result)
    }

    @Test
    fun `updateAnnouncement should update announcement`() = runTest {
        // When
        updateAnnouncementUseCase(announcementFixture)

        // Then
        coVerify { announcementRepository.updateAnnouncement(announcementFixture) }
    }
}