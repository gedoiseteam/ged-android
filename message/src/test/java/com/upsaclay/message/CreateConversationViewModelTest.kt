package com.upsaclay.message

import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.common.domain.userFixture
import com.upsaclay.common.domain.usersFixture
import com.upsaclay.message.domain.conversationFixture
import com.upsaclay.message.domain.entity.ConversationState
import com.upsaclay.message.domain.repository.UserConversationRepository
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
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CreateConversationViewModelTest {
    private val userRepository: UserRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()

    private lateinit var createConversationViewModel: CreateConversationViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userRepository.currentUser } returns MutableStateFlow(userFixture)
        coEvery { userConversationRepository.getConversation(any()) } returns conversationFixture
        coEvery { userRepository.getUsers() } returns usersFixture

        createConversationViewModel = CreateConversationViewModel(
            userConversationRepository = userConversationRepository,
            userRepository = userRepository
        )
    }

    @Test
    fun default_values_are_correct() = runTest {
        val users = usersFixture.filterNot { it.id == userFixture.id }
        assertEquals(users, createConversationViewModel.users.first())
    }

    @Test
    fun generate_conversation_should_generate_new_conversation() {
        // Given
        val interlocutor = userFixture
        val state = ConversationState.NOT_CREATED

        // When
        val result = createConversationViewModel.generateConversation(userFixture)

        // Then
        assertEquals(interlocutor, result.interlocutor)
        assertEquals(state, result.state)
    }

    @Test
    fun getConversation_should_return_conversation_when_present() = runTest {
        // When
        val result = createConversationViewModel.getConversation(conversationFixture.interlocutor.id)

        // Then
        assertEquals(conversationFixture, result)
    }

    @Test
    fun getConversation_should_return_null_when_no_interlocutor_found() = runTest {
        // Given
        val interlocutorId = "unknown"
        coEvery { userConversationRepository.getConversation(interlocutorId) } returns null

        // When
        val result = createConversationViewModel.getConversation(interlocutorId)

        // Then
        assertNull(result)
    }
}