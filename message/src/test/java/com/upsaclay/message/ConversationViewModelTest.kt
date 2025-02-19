package com.upsaclay.message

import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.usecase.ListenConversationsUiUseCase
import com.upsaclay.message.presentation.viewmodels.ConversationViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ConversationViewModelTest {
    private val listenConversationsUiUseCase: ListenConversationsUiUseCase = mockk()

    private lateinit var conversationViewModel: ConversationViewModel

    @Before
    fun setUp() {
        every { listenConversationsUiUseCase.conversationsUI } returns flowOf(conversationsUIFixture)

        conversationViewModel = ConversationViewModel(listenConversationsUiUseCase = listenConversationsUiUseCase)
    }

    @Test
    fun default_values_are_correct() = runTest {
        assertEquals(conversationsUIFixture, conversationViewModel.conversations.first())
    }
}