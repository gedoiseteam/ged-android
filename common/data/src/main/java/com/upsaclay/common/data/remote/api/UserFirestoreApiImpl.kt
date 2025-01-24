package com.upsaclay.common.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.upsaclay.common.data.remote.UserFieldsRemote
import com.upsaclay.common.data.remote.UserFirestoreModel
import com.upsaclay.common.domain.e
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class UserFirestoreApiImpl : UserFirestoreApi {
    private val users = Firebase.firestore.collection("users")

    override suspend fun getUser(userId: Int): UserFirestoreModel? =
        suspendCoroutine { continuation ->
            users.document(userId.toString()).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(UserFirestoreModel::class.java)
                    continuation.resume(user)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

    override suspend fun getUserWithEmail(userEmail: String): UserFirestoreModel? = suspendCoroutine { continuation ->
        users.whereEqualTo(UserFieldsRemote.EMAIL, userEmail).get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.documents.firstOrNull()?.toObject(UserFirestoreModel::class.java)
                continuation.resume(user)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    override suspend fun getUsers(): List<UserFirestoreModel> {
        return suspendCoroutine { continuation ->
            users.get()
                .addOnSuccessListener { snapshot ->
                    val users = snapshot?.documents?.mapNotNull {
                        it.toObject(UserFirestoreModel::class.java)
                    } ?: emptyList()

                    continuation.resume(users)
                }
                .addOnFailureListener {
                    e("Error getting all users", it)
                    continuation.resume(emptyList())
                }
        }
    }

    override suspend fun createUser(userFirestoreModel: UserFirestoreModel): Result<Unit> = suspendCoroutine { continuation ->
        users.document(userFirestoreModel.userId).set(userFirestoreModel)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    override suspend fun updateProfilePictureUrl(
        userId: String,
        profilePictureUrl: String?
    ): Result<Unit> =
        suspendCoroutine { continuation ->
            users.document(userId)
                .update(UserFieldsRemote.PROFILE_PICTURE_URL, profilePictureUrl)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

    override suspend fun isUserExist(email: String): Boolean = suspendCoroutine { continuation ->
        users.whereEqualTo(UserFieldsRemote.EMAIL, email).get()
            .addOnSuccessListener { querySnapshot ->
                continuation.resume(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }
}