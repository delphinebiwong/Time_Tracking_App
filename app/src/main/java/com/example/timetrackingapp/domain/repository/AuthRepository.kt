package com.example.timetrackingapp.domain.repository

import com.example.timetrackingapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining authentication and user profile operations.
 */
interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signOut()
    fun isUserSignedIn(): Boolean
    suspend fun updateProfile(name: String): Result<Unit>
}