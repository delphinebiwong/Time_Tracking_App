package com.example.timetrackingapp.data.repository

import com.example.timetrackingapp.domain.model.TimeSession
import com.example.timetrackingapp.domain.repository.HistoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HistoryRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    override fun getHistory(): Flow<List<TimeSession>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("sessions")
            .whereEqualTo("userId", userId)
            .orderBy("startTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.toObjects(TimeSession::class.java) ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun saveSession(session: TimeSession) {
        val docRef = firestore.collection("sessions").document()
        val newSession = session.copy(id = docRef.id, userId = userId)
        docRef.set(newSession).await()
    }
}