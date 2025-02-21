package com.upsaclay.message.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.LocalCacheSettings
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SnapshotListenOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import com.upsaclay.common.domain.d
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.i
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField
import com.upsaclay.message.data.model.MessageField.TIMESTAMP
import com.upsaclay.message.data.remote.model.RemoteMessage
import com.upsaclay.message.domain.entity.MessageState
import kotlinx.coroutines.channels.awaitClose
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

    override suspend fun updateMessage(remoteMessage: RemoteMessage) { suspendCoroutine { continuation ->
        val update = mapOf(MessageField.SEEN to remoteMessage.seen)
        conversationsCollection.document(remoteMessage.conversationId)
            .collection(MESSAGES_TABLE_NAME)
            .document(remoteMessage.messageId)
            .update(update)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { e ->
                e("Error updating remote message", e)
                continuation.resumeWithException(e)
            }
        }
    }
}