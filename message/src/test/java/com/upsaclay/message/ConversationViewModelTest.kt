package com.upsaclay.message

import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.usecase.GetConversationsUIUseCase
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
    private val getConversationsUIUseCase: GetConversationsUIUseCase = mockk()

    private lateinit var conversationViewModel: ConversationViewModel

    @Before
    fun setUp() {
        every { getConversationsUIUseCase() } returns flowOf(conversationsUIFixture)

        conversationViewModel = ConversationViewModel(getConversationsUIUseCase = getConversationsUIUseCase)
    }

    @Test
    fun default_values_are_correct() = runTest {
        assertEquals(conversationsUIFixture, conversationViewModel.conversations.first())
    }
}