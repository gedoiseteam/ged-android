package com.upsaclay.gedoise

import com.upsaclay.authentication.domain.repository.AuthenticationRepository
import com.upsaclay.common.domain.ConnectivityObserver
import com.upsaclay.common.domain.entity.FcmToken
import com.upsaclay.common.domain.repository.CredentialsRepository
import com.upsaclay.common.domain.repository.UserRepository
import com.upsaclay.gedoise.domain.usecase.ClearDataUseCase
import com.upsaclay.gedoise.domain.usecase.FCMTokenUseCase
import com.upsaclay.gedoise.domain.usecase.StartListeningDataUseCase
import com.upsaclay.gedoise.domain.usecase.StopListeningDataUseCase
import com.upsaclay.message.domain.repository.MessageRepository
import com.upsaclay.message.domain.repository.UserConversationRepository
import com.upsaclay.message.domain.usecase.ListenRemoteConversationsUseCase
import com.upsaclay.message.domain.usecase.ListenRemoteMessagesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val userConversationRepository: UserConversationRepository = mockk()
    private val messageRepository: MessageRepository = mockk()
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val credentialsRepository: CredentialsRepository = mockk()
    private val connectivityObserver: ConnectivityObserver = mockk()

    private val listenRemoteMessagesUseCase: ListenRemoteMessagesUseCase = mockk()
    private val listenRemoteConversationsUseCase: ListenRemoteConversationsUseCase = mockk()

    private lateinit var clearDataUseCase: ClearDataUseCase
    private lateinit var startListeningDataUseCase: StartListeningDataUseCase
    private lateinit var stopListeningDataUseCase: StopListeningDataUseCase
    private lateinit var fcmTokenUseCase: FCMTokenUseCase

    private val testScope = TestScope(UnconfinedTestDispatcher())
    val fcmToken = FcmToken("userId", "token")

    @Before
    fun setUp() {
        every { listenRemoteConversationsUseCase.start() } returns Unit
        every { listenRemoteConversationsUseCase.stop() } returns Unit
        every { listenRemoteMessagesUseCase.start() } returns Unit
        every { listenRemoteMessagesUseCase.stop() } returns Unit
        every { authenticationRepository.isAuthenticated } returns MutableStateFlow(true)
        every { connectivityObserver.isConnected } returns MutableStateFlow(true)
        coEvery { userRepository.deleteCurrentUser() } returns Unit
        coEvery { userConversationRepository.deleteLocalConversations() } returns Unit
        coEvery { messageRepository.deleteLocalMessages() } returns Unit
        coEvery { credentialsRepository.sendFcmToken(any()) } returns Unit
        coEvery { credentialsRepository.getUnsentFcmToken() } returns fcmToken
        coEvery { credentialsRepository.removeUnsentFcmToken() } returns Unit
        coEvery { credentialsRepository.storeUnsentFcmToken(any()) } returns Unit



        clearDataUseCase = ClearDataUseCase(
            userRepository = userRepository,
            userConversationRepository = userConversationRepository,
            messageRepository = messageRepository
        )

        startListeningDataUseCase = StartListeningDataUseCase(
            listenRemoteMessagesUseCase = listenRemoteMessagesUseCase,
            listenRemoteConversationsUseCase = listenRemoteConversationsUseCase
        )

        stopListeningDataUseCase = StopListeningDataUseCase(
            listenRemoteMessagesUseCase = listenRemoteMessagesUseCase,
            listenRemoteConversationsUseCase = listenRemoteConversationsUseCase
        )

        fcmTokenUseCase = FCMTokenUseCase(
            userRepository = userRepository,
            authenticationRepository = authenticationRepository,
            credentialsRepository = credentialsRepository,
            connectivityObserver = connectivityObserver,
            scope = testScope
        )
    }

    @Test
    fun clearDataUseCase_should_delete_all_data() = runTest {
        // When
        clearDataUseCase()

        // Then
        coVerify { userRepository.deleteCurrentUser() }
        coVerify { userConversationRepository.deleteLocalConversations() }
        coVerify { messageRepository.deleteLocalMessages() }
    }

    @Test
    fun startListeningDataUseCase_should_start_listening_data() = runTest {
        // When
        startListeningDataUseCase()

        // Then
        every { listenRemoteConversationsUseCase.start() }
        every { listenRemoteMessagesUseCase.start() }
    }

    @Test
    fun stopListeningDataUseCase_should_stop_listening_data() = runTest {
        // When
        stopListeningDataUseCase()

        // Then
        every { listenRemoteConversationsUseCase.stop() }
        every { listenRemoteMessagesUseCase.stop() }
    }

    @Test
    fun fcmTokenUseCase_should_send_unsent_token_when_authenticated() {
        // When
        fcmTokenUseCase.listenEvents()

        // Then
        coVerify { credentialsRepository.sendFcmToken(fcmToken) }
    }

    @Test
    fun fcmTokenUseCase_should_delete_token_when_unauthenticated() {
        // Given
        coEvery { authenticationRepository.isAuthenticated } returns MutableStateFlow(false)

        // When
        fcmTokenUseCase.listenEvents()

        // Then
        coVerify { credentialsRepository.removeUnsentFcmToken() }
    }

    @Test
    fun fcmTokenUseCase_sendFcmToken_should_remove_unsent_token_when_success() = runTest {
        // Given

        // When
        fcmTokenUseCase.sendFcmToken(fcmToken)

        // Then
        coVerify { credentialsRepository.removeUnsentFcmToken() }
    }

    @Test
    fun fcmTokenUseCase_sendFcmToken_should_store_unsent_token_when_fails() = runTest {
        // Given
        coEvery { credentialsRepository.sendFcmToken(any()) } throws Exception()

        // When
        fcmTokenUseCase.sendFcmToken(fcmToken)

        // Then
        coVerify { credentialsRepository.storeUnsentFcmToken(fcmToken) }
    }
}