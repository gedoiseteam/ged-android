package com.upsaclay.message.data.remote

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.upsaclay.message.data.remote.api.ConversationApi
import com.upsaclay.message.data.remote.model.RemoteConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class ConversationRemoteDataSource(
    private val conversationApi: ConversationApi
) {
    fun listenAllConversations(userId: String): Flow<List<RemoteConversation>> =
        conversationApi.listenAllConversations(userId)

    suspend fun createConversation(remoteConversation: RemoteConversation): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            conversationApi.createConversation(remoteConversation)
            Result.success(Unit)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }

    suspend fun deleteConversation(conversationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            conversationApi.deleteConversation(conversationId)
            Result.success(Unit)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }

    suspend fun setConversationActive(conversationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            conversationApi.setConversationActive(conversationId)
            Result.success(Unit)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        } catch (e: FirebaseException) {
            Result.failure(e)
        }
    }
}