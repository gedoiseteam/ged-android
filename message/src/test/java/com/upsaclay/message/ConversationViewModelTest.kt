package com.upsaclay.message

import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.conversationUIFixture
import com.upsaclay.message.domain.conversationsMessageFixture
import com.upsaclay.message.domain.repository.ConversationMessageRepository
import com.upsaclay.message.domain.repository.ConversationRepository
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.presentation.conversation.ConversationViewModel
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
    private val conversationMessageRepository: ConversationMessageRepository = mockk()
    private val deleteConversationUseCase: DeleteConversationUseCase = mockk()

    private lateinit var conversationViewModel: ConversationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { deleteConversationUseCase(any()) } returns Unit

        conversationViewModel = ConversationViewModel(
            conversationMessageRepository = conversationMessageRepository,
            deleteConversationUseCase = deleteConversationUseCase
        )
    }

    @Test
    fun deleteConversation_delete_conversation() = runTest {
        // Given
        val conversation = conversationFixture

        // When
        conversationViewModel.deleteConversation(conversation)

        // Then
        coVerify { deleteConversationUseCase(conversation) }
    }
}