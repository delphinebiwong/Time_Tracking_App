package com.example.timetrackingapp.domain.repository

import com.example.timetrackingapp.domain.model.TimeSession
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<TimeSession>>
    suspend fun saveSession(session: TimeSession)
}