package com.upsaclay.message.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenSource
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SnapshotListenOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import com.upsaclay.common.domain.e
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.MESSAGES_TABLE_NAME
import com.upsaclay.message.data.model.MessageField
import com.upsaclay.message.data.model.MessageField.MESSAGE_TIMESTAMP
import com.upsaclay.message.data.remote.model.RemoteMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class MessageApiImpl : MessageApi {
    private val conversationsCollection = Firebase.firestore.collection(CONVERSATIONS_TABLE_NAME)

    override fun listenMessages(conversationId: Int): Flow<List<RemoteMessage>> = callbackFlow {
        val listener = conversationsCollection.document(conversationId.toString())
            .collection(MESSAGES_TABLE_NAME)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                error?.let {
                    e("Error getting last messages", it)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents
                    ?.mapNotNull { document ->
                        document.toObject(RemoteMessage::class.java)
                    }

                messages?.let { trySend(it) }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createMessage(remoteMessage: RemoteMessage) {
        suspendCoroutine { continuation ->
            conversationsCollection.document(remoteMessage.conversationId.toString())
                .collection(MESSAGES_TABLE_NAME)
                .document(remoteMessage.messageId.toString())
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

    override suspend fun updateSeenMessage(remoteMessage: RemoteMessage) { suspendCoroutine { continuation ->
        conversationsCollection.document(remoteMessage.conversationId.toString())
            .collection(MESSAGES_TABLE_NAME)
            .document(remoteMessage.messageId.toString())
            .update(MessageField.Remote.SEEN, remoteMessage.seen)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { e ->
                e("Error updating remote message", e)
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun deleteMessages(conversationId: Int) {
        suspendCoroutine { continuation ->
            conversationsCollection.document(conversationId.toString())
                .collection(MESSAGES_TABLE_NAME)
                .get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach { document ->
                        document.reference.delete()
                    }
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    e("Error deleting remote messages", e)
                    continuation.resumeWithException(e)
                }
        }
    }
}