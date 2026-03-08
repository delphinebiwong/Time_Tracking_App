package com.example.timetrackingapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.timetrackingapp.R
import com.example.timetrackingapp.util.TimeUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Advanced Foreground Service managing independent timers for each project.
 */
class TimerService : Service() {

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    // Persistent state for ALL projects in the current session
    private val _projectTimes = MutableStateFlow<Map<String, Long>>(emptyMap())
    val projectTimes = _projectTimes.asStateFlow()

    private val _runningProjectIds = MutableStateFlow<Set<String>>(emptySet())
    val runningProjectIds = _runningProjectIds.asStateFlow()

    // Map to store when each project session started (for precise history)
    private val sessionStartTimes = mutableMapOf<String, Long>()

    private var activeProjectId: String? = null
    private var activeProjectName: String? = null

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Starts or Resumes a timer for a specific project.
     * Automatically pauses any other running timer to ensure focused work.
     */
    fun startTimer(projectId: String, projectName: String) {
        if (activeProjectId == projectId && _runningProjectIds.value.contains(projectId)) return

        // Pause current focused timer if switching projects
        activeProjectId?.let { if (it != projectId) pauseTimer(it) }

        activeProjectId = projectId
        activeProjectName = projectName
        
        // Mark the start of a session if not already tracking
        if (!sessionStartTimes.containsKey(projectId)) {
            sessionStartTimes[projectId] = System.currentTimeMillis()
        }

        _runningProjectIds.update { it + projectId }

        val currentTime = _projectTimes.value[projectId] ?: 0L
        
        val notification = createNotification(currentTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Ticker logic
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                _projectTimes.update { currentMap ->
                    val time = currentMap[projectId] ?: 0L
                    currentMap + (projectId to (time + 1))
                }
                val updatedTime = _projectTimes.value[projectId] ?: 0L
                updateNotification(updatedTime)
            }
        }
    }

    fun pauseTimer(projectId: String) {
        if (activeProjectId == projectId) {
            timerJob?.cancel()
            activeProjectId = null
            activeProjectName = null
        }
        _runningProjectIds.update { it - projectId }
    }

    /**
     * Stops the timer and returns session metadata for database saving.
     */
    fun stopAndGetSessionData(projectId: String): Pair<Long, Long>? {
        val startTime = sessionStartTimes.remove(projectId) ?: System.currentTimeMillis()
        val totalElapsed = _projectTimes.value[projectId] ?: 0L
        
        pauseTimer(projectId)
        
        // Remove project state from current tracking
        _projectTimes.update { it - projectId }
        
        if (_runningProjectIds.value.isEmpty()) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        
        return startTime to totalElapsed
    }

    private fun updateNotification(seconds: Long) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(seconds))
    }

    private fun createNotification(seconds: Long): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking: ${activeProjectName ?: "Project"}")
            .setContentText("Focus Time: ${TimeUtils.formatDuration(seconds)}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Independent Focus Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val CHANNEL_ID = "timer_pro_v2_channel"
        private const val NOTIFICATION_ID = 2024
    }
}