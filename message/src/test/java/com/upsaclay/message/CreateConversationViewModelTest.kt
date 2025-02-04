package com.upsaclay.message

import com.google.gson.GsonBuilder
import com.upsaclay.common.domain.LocalDateTimeSerializer
import com.upsaclay.common.domain.usecase.GetCurrentUserUseCase
import com.upsaclay.common.domain.usecase.GetUsersUseCase
import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.usersFixture
import com.upsaclay.message.domain.entity.ConversationScreenState
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.entity.ConversationUI
import com.upsaclay.message.domain.entity.Message
import com.upsaclay.message.presentation.viewmodels.CreateConversationViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CreateConversationViewModelTest {
    private val getUsersUseCase: GetUsersUseCase = mockk()
    private val getCurrentUserUseCase: GetCurrentUserUseCase = mockk()

    private lateinit var conversationViewModel: CreateConversationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { getCurrentUserUseCase() } returns MutableStateFlow(userFixture)
        coEvery { getUsersUseCase() } returns usersFixture

        conversationViewModel = CreateConversationViewModel(
            getUsersUseCase = getUsersUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        val users = usersFixture.filterNot { it.id == userFixture.id }
        assertEquals(ConversationScreenState.DEFAULT, conversationViewModel.screenState.value)
        assertEquals(users, conversationViewModel.users.first())
    }

    @Test
    fun generate_conversation_json() {
        // Given
        val interlocutor = userFixture
        val lastMessage: Message? = null
        val state = ConversationState.NOT_CREATED
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
            .create()

        // When
        val result = conversationViewModel.generateConversationJson(userFixture)


        // Then
        val conversationResult = gson.fromJson(result, ConversationUI::class.java)
        assertEquals(interlocutor, conversationResult.interlocutor)
        assertEquals(lastMessage, conversationResult.lastMessage)
        assertEquals(state, conversationResult.state)
    }
}