package com.upsaclay.message

import androidx.paging.PagingData
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUIUseCase
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationViewModelTest {
    private val getConversationUIUseCase: GetConversationUIUseCase = mockk()
    private val deleteConversationUseCase: DeleteConversationUseCase = mockk()

    private lateinit var conversationViewModel: ConversationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getConversationUIUseCase() } returns flowOf(PagingData.from(conversationsUIFixture))
        coEvery { deleteConversationUseCase(any()) } returns Unit

        conversationViewModel = ConversationViewModel(
            getConversationUIUseCase = getConversationUIUseCase,
            deleteConversationUseCase = deleteConversationUseCase
        )
    }

    @Test
    fun deleteConversation_delete_conversation() = runTest {
        // Given
        val conversation = conversationUIFixture

        // When
        conversationViewModel.deleteConversation(conversation)

        // Then
        coVerify { deleteConversationUseCase(conversation) }
        assertEquals(ConversationScreenState.SUCCESS, conversationViewModel.screenState.value)
    }
}