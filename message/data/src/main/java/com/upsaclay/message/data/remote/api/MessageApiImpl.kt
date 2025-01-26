package com.upsaclay.message.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.upsaclay.common.domain.e
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField.TIMESTAMP
import com.upsaclay.message.data.remote.model.RemoteMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class MessageApiImpl : MessageApi {
    private val conversationsCollection = Firebase.firestore.collection(CONVERSATIONS_TABLE_NAME)

    override fun listenMessages(conversationId: String): Flow<RemoteMessage> = callbackFlow {
        val listener = conversationsCollection.document(conversationId)
            .collection(MESSAGES_TABLE_NAME)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    e("Error getting last messages", it)
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { change ->
                    val message = change.document.toObject(RemoteMessage::class.java)
                    when (change.type) {
                        DocumentChange.Type.ADDED -> trySend(message)
                        DocumentChange.Type.MODIFIED -> trySend(message)
                        DocumentChange.Type.REMOVED -> return@forEach
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    override fun listenLastMessage(conversationId: String): Flow<RemoteMessage?> = callbackFlow {
        val listener = conversationsCollection.document(conversationId)
            .collection(MESSAGES_TABLE_NAME)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    e("Error getting last message", it)
                    trySend(null)
                }

                snapshot?.toObjects(RemoteMessage::class.java)?.firstOrNull()?.let { trySend(it) }
            }
        awaitClose { listener.remove() }
    }


    override suspend fun getMessages(
        conversationId: String,
        limit: Long
    ): List<RemoteMessage> = suspendCoroutine { continuation ->
        conversationsCollection.document(conversationId)
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
            conversationsCollection.document(remoteMessage.conversationId)
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