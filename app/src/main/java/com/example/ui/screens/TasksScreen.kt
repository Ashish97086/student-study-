package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.StudySuccess
import com.example.ui.theme.StudyTertiary
import com.example.ui.theme.StudyWarning
import com.example.viewmodel.StudyViewModel

@Composable
fun TasksScreen(
    viewModel: StudyViewModel
) {
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Pending, 2: Completed
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredTasks = when (selectedTab) {
        1 -> allTasks.filter { !it.completed }
        2 -> allTasks.filter { it.completed }
        else -> allTasks
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = GeoPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tasks & Assignments",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1C1B1F)
            )
            Text(
                text = "Track homework, assignments, and study tasks",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GeoSurfaceVariant,
                contentColor = GeoPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "All (${allTasks.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) GeoPrimary else Color(0xFF64748B)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Pending (${allTasks.count { !it.completed }})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) GeoPrimary else Color(0xFF64748B)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Done (${allTasks.count { it.completed }})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 2) GeoPrimary else Color(0xFF64748B)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(GeoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedTab == 2) "No completed tasks yet" else "No tasks found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1C1B1F)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the + button to add a new homework or revision task.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        val subject = subjects.find { it.id == task.subjectId }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, GeoOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.completed,
                                    onCheckedChange = { viewModel.toggleTaskComplete(task) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = GeoPrimary,
                                        uncheckedColor = Color(0xFF94A3B8)
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        color = if (task.completed) Color(0xFF94A3B8)
                                        else Color(0xFF1C1B1F)
                                    )

                                    if (task.description.isNotBlank()) {
                                        Text(
                                            text = task.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        if (subject != null) {
                                            Text(
                                                text = subject.name,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = GeoPrimary
                                            )
                                            Text(
                                                text = " • ",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }

                                        if (task.dueDate.isNotBlank()) {
                                            Text(
                                                text = "Due: ${task.dueDate}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                color = if (task.completed) Color(0xFF94A3B8)
                                                else Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                }

                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (task.priority) {
                                            "HIGH" -> Color(0xFFFEE2E2)
                                            "MEDIUM" -> Color(0xFFFEF3C7)
                                            else -> GeoPrimaryContainer
                                        }
                                    )
                                ) {
                                    Text(
                                        text = task.priority,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when (task.priority) {
                                            "HIGH" -> Color(0xFFDC2626)
                                            "MEDIUM" -> Color(0xFFD97706)
                                            else -> GeoOnPrimaryContainer
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteTask(task) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Task",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            subjects = subjects,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, subId, type, priority, date, time ->
                viewModel.addTask(title, desc, subId, type, priority, date, time)
                showAddTaskDialog = false
            }
        )
    }
}
