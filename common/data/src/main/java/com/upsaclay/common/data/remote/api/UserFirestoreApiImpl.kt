package com.upsaclay.common.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.upsaclay.common.data.remote.FirestoreUser
import com.upsaclay.common.data.remote.UserFieldsRemote
import com.upsaclay.common.domain.e
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class UserFirestoreApiImpl : UserFirestoreApi {
    private val usersCollection = Firebase.firestore.collection("users")

    override suspend fun getUser(userId: String): FirestoreUser? = suspendCoroutine { continuation ->
        usersCollection.document(userId).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(FirestoreUser::class.java)
                continuation.resume(user)
            }
            .addOnFailureListener { e ->
                e("Error getting firestore user", e)
                continuation.resumeWithException(e)
            }
    }

    override suspend fun getUserFlow(userId: String): Flow<FirestoreUser?> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    e("Error getting firestore user", it)
                    trySend(null)
                }

                snapshot?.let {
                    val user = it.toObject(FirestoreUser::class.java)
                    trySend(user)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getUserWithEmail(userEmail: String): FirestoreUser? = suspendCoroutine { continuation ->
        usersCollection.whereEqualTo(UserFieldsRemote.EMAIL, userEmail).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.documents.firstOrNull()?.toObject(FirestoreUser::class.java)
                continuation.resume(user)
            }
            .addOnFailureListener { e ->
                e("Error getting firestore user with email", e)
                continuation.resumeWithException(e)
            }
    }

    override suspend fun getUsers(): List<FirestoreUser> = suspendCoroutine { continuation ->
        usersCollection.get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot?.documents?.mapNotNull {
                    it.toObject(FirestoreUser::class.java)
                } ?: emptyList()

                continuation.resume(users)
            }
            .addOnFailureListener { e ->
                e("Error getting firestore users", e)
                continuation.resume(emptyList())
            }
    }

    override suspend fun createUser(firestoreUser: FirestoreUser) {
        suspendCoroutine { continuation ->
            usersCollection.document(firestoreUser.userId).set(firestoreUser)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    override suspend fun updateProfilePictureUrl(userId: String, profilePictureUrl: String?) {
        suspendCoroutine { continuation ->
            usersCollection.document(userId)
                .update(UserFieldsRemote.PROFILE_PICTURE_URL, profilePictureUrl)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    e("Error update firestore user pofile picture", e)
                    continuation.resumeWithException(e)
                }
        }
    }

    override suspend fun isUserExist(email: String): Boolean = suspendCoroutine { continuation ->
        usersCollection.whereEqualTo(UserFieldsRemote.EMAIL, email).get()
            .addOnSuccessListener { snapshot ->
                continuation.resume(!snapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                e("Error checking is firestore user exists", e)
                continuation.resumeWithException(e)
            }
    }
}