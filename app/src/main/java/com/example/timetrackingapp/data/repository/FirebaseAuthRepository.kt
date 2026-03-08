package com.example.timetrackingapp.data.repository

import com.example.timetrackingapp.domain.model.UserProfile
import com.example.timetrackingapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase implementation of the [AuthRepository].
 * Handles Google Sign-In, Sign-Out, and user profile management using Firebase Authentication.
 */
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    /**
     * Observes the Firebase Auth state and emits a [UserProfile] when it changes.
     */
    override val currentUser: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            val userProfile = firebaseUser?.let {
                UserProfile(
                    uid = it.uid,
                    name = it.displayName ?: "",
                    email = it.email ?: "",
                    photoUrl = it.photoUrl?.toString() ?: ""
                )
            }
            trySend(userProfile)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Signs in with Firebase using a Google ID token.
     */
    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                Result.success(UserProfile(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "" ,
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                ))
            } else {
                Result.failure(Exception("Sign in failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs out from Firebase Authentication.
     */
    override suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Checks if there is a currently authenticated user.
     */
    override fun isUserSignedIn(): Boolean = auth.currentUser != null

    /**
     * Updates the display name of the current Firebase user.
     */
    override suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("User not signed in"))
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}