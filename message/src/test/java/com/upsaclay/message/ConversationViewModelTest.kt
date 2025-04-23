package com.upsaclay.message

import androidx.paging.PagingData
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationViewModelTest {
    private val getPagedConversationsUIUseCase: GetPagedConversationsUIUseCase = mockk()
    private val deleteConversationUseCase: DeleteConversationUseCase = mockk()

    private lateinit var conversationViewModel: ConversationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getPagedConversationsUIUseCase() } returns flowOf(PagingData.from(conversationsUIFixture))
        coEvery { deleteConversationUseCase(any()) } returns Unit

        conversationViewModel = ConversationViewModel(
            getPagedConversationsUIUseCase = getPagedConversationsUIUseCase,
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
    }
}