package com.upsaclay.message.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.upsaclay.common.domain.e
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField.TIMESTAMP
import com.upsaclay.message.data.remote.model.RemoteMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class MessageApiImpl : MessageApi {
    private val conversationCollection = Firebase.firestore.collection(CONVERSATIONS_TABLE_NAME)

    override fun listenMessages(conversationId: String): Flow<List<RemoteMessage>> = callbackFlow {
        val listener = conversationCollection.document(conversationId)
            .collection(MESSAGES_TABLE_NAME)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    e("Error getting last messages", it)
                    trySend(emptyList())
                }

                snapshot?.toObjects(RemoteMessage::class.java)?.let { trySend(it) }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getMessages(
        conversationId: String,
        limit: Long
    ): List<RemoteMessage> = suspendCoroutine { continuation ->
        conversationCollection.document(conversationId)
            .collection(MESSAGES_TABLE_NAME)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener {
                val messages = it.toObjects(RemoteMessage::class.java)
                continuation.resume(messages)
            }
            .addOnFailureListener { e ->
                e("Error getting messages", e)
                continuation.resume(emptyList())
            }
    }

    override suspend fun createMessage(remoteMessage: RemoteMessage) {
        suspendCoroutine { continuation ->
            conversationCollection.document(remoteMessage.conversationId)
                .collection(MESSAGES_TABLE_NAME)
                .document(remoteMessage.messageId)
                .set(remoteMessage)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    e("Error creating remote message", e)
                    continuation.resumeWithException(e)
                }
        }
    }
}