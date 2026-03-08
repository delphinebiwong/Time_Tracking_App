package com.example.timetrackingapp.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetrackingapp.ui.project.ProjectViewModel
import com.example.timetrackingapp.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TimerScreen(
    projectId: String,
    projectName: String,
    onNavigateBack: () -> Unit,
    timerViewModel: TimerViewModel = hiltViewModel(),
    projectViewModel: ProjectViewModel = hiltViewModel()
) {
    val timerService by timerViewModel.timerService.collectAsState()
    val projects by projectViewModel.projects.collectAsState()
    val currentProject = projects.find { it.id == projectId }

    // Safely collect maps from service
    val projectTimes by (timerService?.projectTimes ?: MutableStateFlow(emptyMap())).collectAsState()
    val runningProjectIds by (timerService?.runningProjectIds ?: MutableStateFlow(emptySet())).collectAsState()

    val currentSessionSeconds = projectTimes[projectId] ?: 0L
    val isRunning = runningProjectIds.contains(projectId)
    
    // Total time = Saved project time + current active session
    val totalSeconds = (currentProject?.totalTimeSeconds ?: 0L) + currentSessionSeconds

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = projectName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        
        if (currentProject != null && currentProject.goalHours > 0) {
            val progress = (totalSeconds.toFloat() / (currentProject.goalHours * 3600f)).coerceAtMost(1f)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Goal Progress: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(0.8f).height(12.dp),
                color = Color(android.graphics.Color.parseColor(currentProject.colorHex)),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        Surface(
            modifier = Modifier.size(280.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = TimeUtils.formatDuration(currentSessionSeconds),
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("ACTIVE SESSION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isRunning) {
                Button(
                    onClick = { timerViewModel.startTimer(projectId, projectName) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("START")
                }
            } else {
                FilledTonalButton(
                    onClick = { timerViewModel.pauseTimer(projectId) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("PAUSE")
                }
            }

            Button(
                onClick = {
                    timerViewModel.stopTimer(projectId, projectName)
                    onNavigateBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("FINISH")
            }
        }
    }
}