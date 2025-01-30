package com.upsaclay.message.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore
import com.upsaclay.common.domain.e
import com.upsaclay.message.data.model.CONVERSATIONS_TABLE_NAME
import com.upsaclay.message.data.model.ConversationField
import com.upsaclay.message.data.remote.model.RemoteConversation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class ConversationApiImpl : ConversationApi {
    private val conversationsCollection = Firebase.firestore.collection(CONVERSATIONS_TABLE_NAME)

    override fun listenConversations(userId: String): Flow<RemoteConversation> = callbackFlow {
        val listener = conversationsCollection
            .whereArrayContains(ConversationField.Remote.PARTICIPANTS, userId)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    e("Error getting conversations", it)
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { change ->
                    val conversation = change.document.toObject(RemoteConversation::class.java)
                    when(change.type) {
                        DocumentChange.Type.ADDED -> trySend(conversation)
                        DocumentChange.Type.MODIFIED -> trySend(conversation)
                        DocumentChange.Type.REMOVED -> return@forEach
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createConversation(remoteConversation: RemoteConversation) = suspendCoroutine { continuation ->
        conversationsCollection
            .document(remoteConversation.conversationId)
            .set(remoteConversation)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { e ->
                e("Error creating conversations", e)
                continuation.resumeWithException(e)
            }
    }

    override suspend fun deleteConversation(conversationId: String) = suspendCoroutine { continuation ->
        conversationsCollection.document(conversationId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { e ->
                e("Error deleting conversation", e)
                continuation.resumeWithException(e)
            }
    }
}