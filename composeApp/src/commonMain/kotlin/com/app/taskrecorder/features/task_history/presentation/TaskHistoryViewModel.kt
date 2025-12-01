package com.app.taskrecorder.features.task_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.usecase.GetAllTasksUseCase
import com.app.taskrecorder.features.task_history.domain.usecase.GetTaskStatsUseCase
import com.app.taskrecorder.features.task_history.domain.usecase.TaskStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskHistoryState(
    val tasks: List<Task> = emptyList(),
    val stats: TaskStats = TaskStats(0, 0),
    val isLoading: Boolean = false
)

class TaskHistoryViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getTaskStatsUseCase: GetTaskStatsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(TaskHistoryState())
    val state: StateFlow<TaskHistoryState> = _state.asStateFlow()
    
    init {
        loadTasks()
        loadStats()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllTasksUseCase().collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            getTaskStatsUseCase().collect { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }
}
