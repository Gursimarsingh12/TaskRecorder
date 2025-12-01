package com.app.taskrecorder.features.task_history.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.taskrecorder.core.ui.components.StatsCard
import com.app.taskrecorder.core.ui.components.TaskHistoryItem
import com.app.taskrecorder.core.ui.components.TaskPreviewDialog
import com.app.taskrecorder.features.task_history.domain.model.Task
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHistoryScreen(
    onBackClick: () -> Unit,
    viewModel: TaskHistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Task History") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "Work Report",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsCard(
                    value = "${state.stats.totalTasks}",
                    label = "Total\nTasks",
                    modifier = Modifier.weight(1f)
                )
                
                val minutes = state.stats.totalDuration / 60
                val seconds = state.stats.totalDuration % 60
                StatsCard(
                    value = "${minutes}m ${seconds}s",
                    label = "Duration\nRecorded",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Tasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            if (state.tasks.isEmpty() && !state.isLoading) {
                Text(
                    text = "No tasks yet",
                    fontSize = 14.sp,
                    color = Color(0xFF78909C),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            var selectedTask by remember { mutableStateOf<Task?>(null) }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.tasks) { task ->
                    TaskHistoryItem(
                        task = task,
                        onPreviewClick = { selectedTask = task }
                    )
                }
            }
            
            selectedTask?.let { task ->
                TaskPreviewDialog(
                    task = task,
                    onDismiss = { selectedTask = null }
                )
            }
        }
    }
}
