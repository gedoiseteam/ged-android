package com.upsaclay.message

import com.upsaclay.message.domain.conversationsUIFixture
import com.upsaclay.message.domain.usecase.DeleteConversationUseCase
import com.upsaclay.message.domain.usecase.GetConversationUIUseCase
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
    private val getConversationUIUseCase: GetConversationUIUseCase = mockk()
    private val deleteConversationUseCase: DeleteConversationUseCase = mockk()

    private lateinit var conversationViewModel: ConversationViewModel

    @Before
    fun setUp() {
        every { getConversationUIUseCase.conversationsUI } returns flowOf(conversationsUIFixture)

        conversationViewModel = ConversationViewModel(
            getConversationUIUseCase = getConversationUIUseCase,
            deleteConversationUseCase = deleteConversationUseCase
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        assertEquals(conversationsUIFixture, conversationViewModel.conversations.first())
    }
}