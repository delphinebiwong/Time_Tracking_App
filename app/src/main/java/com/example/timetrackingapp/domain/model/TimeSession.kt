package com.example.timetrackingapp.domain.model

import java.util.Date

data class TimeSession(
    val id: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val startTime: Date = Date(),
    val endTime: Date = Date(),
    val durationSeconds: Long = 0L,
    val userId: String = ""
)