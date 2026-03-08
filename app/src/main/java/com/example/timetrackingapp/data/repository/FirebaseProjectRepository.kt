package com.example.timetrackingapp.data.repository

import com.example.timetrackingapp.domain.model.Project
import com.example.timetrackingapp.domain.repository.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseProjectRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProjectRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    override fun getProjects(): Flow<List<Project>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("projects")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val projects = snapshot?.toObjects(Project::class.java) ?: emptyList()
                trySend(projects)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addProject(project: Project) {
        val docRef = firestore.collection("projects").document()
        val newProject = project.copy(id = docRef.id, userId = userId)
        docRef.set(newProject).await()
    }

    override suspend fun updateProject(project: Project) {
        firestore.collection("projects").document(project.id).set(project).await()
    }

    override suspend fun deleteProject(projectId: String) {
        firestore.collection("projects").document(projectId).delete().await()
    }
}