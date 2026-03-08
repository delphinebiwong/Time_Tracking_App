package com.example.timetrackingapp.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetrackingapp.domain.model.TimeSession
import com.example.timetrackingapp.domain.repository.HistoryRepository
import com.example.timetrackingapp.domain.repository.ProjectRepository
import com.example.timetrackingapp.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Pro ViewModel for the Timer Screen.
 * Manages independent timers and goal tracking.
 */
@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val historyRepository: HistoryRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _timerService = MutableStateFlow<TimerService?>(null)
    val timerService = _timerService.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            _timerService.value = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _timerService.value = null
        }
    }

    init {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun startTimer(projectId: String, projectName: String) {
        val intent = Intent(context, TimerService::class.java)
        context.startForegroundService(intent)
        _timerService.value?.startTimer(projectId, projectName)
    }

    fun pauseTimer(projectId: String) {
        _timerService.value?.pauseTimer(projectId)
    }

    /**
     * Perfectly records the session and updates the project's cumulative time.
     */
    fun stopTimer(projectId: String, projectName: String) {
        val sessionData = _timerService.value?.stopAndGetSessionData(projectId) ?: return
        val (startTime, duration) = sessionData
        
        if (duration > 0) {
            viewModelScope.launch {
                // 1. Save historical record
                historyRepository.saveSession(
                    TimeSession(
                        projectId = projectId,
                        projectName = projectName,
                        startTime = Date(startTime),
                        endTime = Date(),
                        durationSeconds = duration
                    )
                )

                // 2. Update the main Project total
                val projects = projectRepository.getProjects().first()
                val project = projects.find { it.id == projectId }
                project?.let {
                    val updatedProject = it.copy(totalTimeSeconds = it.totalTimeSeconds + duration)
                    projectRepository.updateProject(updatedProject)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unbindService(serviceConnection)
        } catch (e: Exception) {}
    }
}