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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.entity.Subject
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.utils.DateUtils
import com.example.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarksScreen(
    navController: NavController,
    viewModel: StudyViewModel
) {
    val marks by viewModel.allMarks.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val averagePercentage = if (marks.isNotEmpty()) {
        marks.map { (it.obtainedMarks / it.maximumMarks.coerceAtLeast(1.0)) * 100 }.average().toInt()
    } else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marks & Grades", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1C1B1F)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GeoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_marks_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Mark")
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
            Spacer(modifier = Modifier.height(10.dp))

            // Average Performance Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, GeoOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Average Academic Score",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$averagePercentage%",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = GeoPrimary
                        )
                        Text(
                            text = "${marks.size} assessments recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GeoPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grade,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Assessments History (${marks.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1C1B1F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (marks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(GeoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No marks recorded yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1C1B1F)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap + to enter scores for your quizzes, midterms, and tests.",
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
                    items(marks, key = { it.id }) { mark ->
                        val subject = subjects.find { it.id == mark.subjectId }
                        val percentage = ((mark.obtainedMarks / mark.maximumMarks.coerceAtLeast(1.0)) * 100).toInt()
                        val grade = when {
                            percentage >= 90 -> "A+"
                            percentage >= 80 -> "A"
                            percentage >= 70 -> "B"
                            percentage >= 60 -> "C"
                            percentage >= 50 -> "D"
                            else -> "F"
                        }
                        val isPass = percentage >= 60

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, GeoOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isPass) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                                    ),
                                    border = BorderStroke(1.dp, if (isPass) Color(0xFFA7F3D0) else Color(0xFFFECACA))
                                ) {
                                    Box(
                                        modifier = Modifier.size(44.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = grade,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isPass) Color(0xFF047857) else Color(0xFFB91C1C)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mark.examName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1C1B1F)
                                    )
                                    Text(
                                        text = "${subject?.name ?: "Subject"} • ${mark.examDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${mark.obtainedMarks.toInt()} / ${mark.maximumMarks.toInt()}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GeoPrimary
                                    )
                                    Text(
                                        text = "$percentage%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(onClick = { viewModel.deleteMark(mark) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Mark",
                                        tint = Color(0xFFEF4444),
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

    if (showAddDialog) {
        AddMarkDialog(
            subjects = subjects,
            onDismiss = { showAddDialog = false },
            onConfirm = { subId, examName, obtained, max, date ->
                viewModel.addMark(subId, examName, obtained, max, date)
                showAddDialog = false
            }
        )
    }
}

// Dialog: Add Mark
@Composable
fun AddMarkDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (subId: Long, examName: String, obtained: Double, max: Double, date: String) -> Unit
) {
    var selectedSubId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 0L) }
    var examName by remember { mutableStateOf("") }
    var obtainedStr by remember { mutableStateOf("") }
    var maxStr by remember { mutableStateOf("100") }
    var examDate by remember { mutableStateOf(DateUtils.getTodayDateString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exam Mark", fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1F)) },
        text = {
            if (subjects.isEmpty()) {
                Text("Please add a subject first.", color = Color(0xFF64748B))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = examName,
                        onValueChange = { examName = it },
                        label = { Text("Assessment / Exam Name *") },
                        placeholder = { Text("e.g. Unit 1 Quiz") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            cursorColor = GeoPrimary
                        )
                    )
                    OutlinedTextField(
                        value = obtainedStr,
                        onValueChange = { obtainedStr = it },
                        label = { Text("Obtained Marks *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            cursorColor = GeoPrimary
                        )
                    )
                    OutlinedTextField(
                        value = maxStr,
                        onValueChange = { maxStr = it },
                        label = { Text("Maximum Marks *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            cursorColor = GeoPrimary
                        )
                    )
                    OutlinedTextField(
                        value = examDate,
                        onValueChange = { examDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            cursorColor = GeoPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            if (subjects.isNotEmpty()) {
                Button(
                    onClick = {
                        val obtained = obtainedStr.toDoubleOrNull() ?: 0.0
                        val max = maxStr.toDoubleOrNull() ?: 100.0
                        if (examName.isNotBlank() && max > 0) {
                            onConfirm(selectedSubId, examName, obtained, max, examDate)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF64748B))
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
