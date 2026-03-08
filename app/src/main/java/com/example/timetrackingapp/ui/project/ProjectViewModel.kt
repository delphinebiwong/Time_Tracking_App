package com.example.timetrackingapp.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetrackingapp.domain.model.Project
import com.example.timetrackingapp.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<Project>> = projectRepository.getProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Enhanced project creation with user-friendly colors and goals.
     */
    fun addProjectPro(name: String, colorName: String, goalHours: Int) {
        viewModelScope.launch {
            projectRepository.addProject(
                Project(
                    name = name, 
                    colorName = colorName, 
                    goalHours = goalHours
                )
            )
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }
}