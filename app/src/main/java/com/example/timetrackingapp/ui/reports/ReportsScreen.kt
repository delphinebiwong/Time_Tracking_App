package com.example.timetrackingapp.ui.reports

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetrackingapp.domain.model.Project
import com.example.timetrackingapp.domain.model.TimeSession
import com.example.timetrackingapp.ui.history.HistoryViewModel
import com.example.timetrackingapp.ui.project.ProjectViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateToProjects: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    projectViewModel: ProjectViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val projects by projectViewModel.projects.collectAsState()
    val history by historyViewModel.history.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Productivity Reports") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProjects,
                    icon = { Text("Projects") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Text("History") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Current */ },
                    icon = { Text("Reports") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Work Distribution", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            ProjectPieChart(projects)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Weekly Productivity", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            DailyBarChart(history)
        }
    }
}

@Composable
fun ProjectPieChart(projects: List<Project>) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    800
                )
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = textColor
                legend.textSize = 12f
                setEntryLabelColor(textColor)
                setHoleColor(android.graphics.Color.TRANSPARENT)
                setCenterText("Hours")
                setCenterTextColor(textColor)
            }
        },
        update = { chart ->
            // Filter out projects with 0 time
            val activeProjects = projects.filter { it.totalTimeSeconds > 0 }
            
            if (activeProjects.isNotEmpty()) {
                val entries = activeProjects.map { project ->
                    PieEntry(project.totalTimeSeconds.toFloat() / 3600f, project.name)
                }
                val dataSet = PieDataSet(entries, "").apply {
                    // CRITICAL FIX: Match colors to active projects only
                    colors = activeProjects.map { android.graphics.Color.parseColor(it.colorHex) }
                    valueTextSize = 14f
                    valueTextColor = textColor
                }
                chart.data = PieData(dataSet)
            } else {
                chart.data = null
                chart.setNoDataText("No active work sessions tracked yet.")
                chart.setNoDataTextColor(textColor)
            }
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}

@Composable
fun DailyBarChart(history: List<TimeSession>) {
    val greenColor = android.graphics.Color.parseColor("#4CAF50")
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    800
                )
                description.isEnabled = false
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    this.textColor = textColor
                }
                
                axisLeft.apply {
                    this.textColor = textColor
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }
                
                axisRight.isEnabled = false
                legend.textColor = textColor
            }
        },
        update = { chart ->
            val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
            val last7Days = mutableMapOf<String, Float>()
            
            val calendar = Calendar.getInstance()
            for (i in 0..6) {
                val day = calendar.clone() as Calendar
                day.add(Calendar.DAY_OF_YEAR, -i)
                last7Days[dateFormat.format(day.time)] = 0f
            }

            history.forEach { session ->
                val dateStr = dateFormat.format(session.startTime)
                if (last7Days.containsKey(dateStr)) {
                    last7Days[dateStr] = last7Days[dateStr]!! + (session.durationSeconds.toFloat() / 3600f)
                }
            }

            val sortedKeys = last7Days.keys.toList().sortedBy { 
                try { dateFormat.parse(it)?.time } catch (e: Exception) { 0L }
            }
            val entries = sortedKeys.mapIndexed { index, key ->
                BarEntry(index.toFloat(), last7Days[key]!!)
            }
            
            val dataSet = BarDataSet(entries, "Total Hours").apply {
                color = greenColor
                valueTextColor = greenColor
                valueTextSize = 12f
            }
            
            chart.data = BarData(dataSet)
            chart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return sortedKeys.getOrNull(value.toInt()) ?: ""
                }
            }
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}