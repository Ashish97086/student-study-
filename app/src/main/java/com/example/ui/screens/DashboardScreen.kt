package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.entity.Subject
import com.example.ui.navigation.Screen
import com.example.ui.theme.GeoOrangeAccent
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoStreakGreen
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTertiaryContainer
import com.example.ui.theme.GeoOnTertiaryContainer
import com.example.ui.theme.StudyPrimary
import com.example.ui.theme.StudySuccess
import com.example.ui.theme.StudyTertiary
import com.example.ui.theme.StudyWarning
import com.example.utils.DateUtils
import com.example.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: StudyViewModel
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val todayMinutes by viewModel.todayStudyMinutes.collectAsStateWithLifecycle()
    val pendingTasks by viewModel.pendingTasks.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingTaskCount.collectAsStateWithLifecycle()
    val upcomingExam by viewModel.upcomingExam.collectAsStateWithLifecycle()
    val totalChapters by viewModel.totalChaptersCount.collectAsStateWithLifecycle()
    val completedChapters by viewModel.completedChaptersCount.collectAsStateWithLifecycle()
    val todayTimetable by viewModel.todayTimetableEntries.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val studyDates by viewModel.studyDates.collectAsStateWithLifecycle()

    val currentStreak = remember(studyDates) {
        DateUtils.calculateStreak(studyDates)
    }

    // Quick Action Dialog states
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showMarkAttendanceDialog by remember { mutableStateOf(false) }
    var showAddExamDialog by remember { mutableStateOf(false) }

    val dailyTargetMinutes = profile?.dailyStudyTarget ?: 120
    val actualTodayMinutes = todayMinutes ?: 0
    val studyProgress = (actualTodayMinutes.toFloat() / dailyTargetMinutes.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val syllabusProgress = if (totalChapters > 0) completedChapters.toFloat() / totalChapters.toFloat() else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Geometric Balance Top Header Card
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = DateUtils.getTodayDisplayString().uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hello, ${profile?.name?.ifBlank { "Sarah" } ?: "Sarah"}!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                    }

                    // Initials Circle Avatar
                    val initials = remember(profile?.name) {
                        val name = profile?.name?.trim() ?: "SC"
                        val parts = name.split(" ").filter { it.isNotBlank() }
                        if (parts.size >= 2) {
                            "${parts[0].first()}${parts[1].first()}".uppercase()
                        } else if (name.length >= 2) {
                            name.take(2).uppercase()
                        } else {
                            "SC"
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GeoPrimaryContainer)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GeoOnPrimaryContainer
                        )
                    }
                }
            }
        }

        // Daily Study Target Hero Card (#0061A4, rounded-3xl)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GeoPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Daily Study Target",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${String.format("%.1f", actualTodayMinutes / 60f)} / ${String.format("%.1f", dailyTargetMinutes / 60f)}",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "hrs",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Circular 75% indicator with border-4 border-white/20 border-t-white
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(52.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White.copy(alpha = 0.2f),
                                strokeWidth = 4.dp
                            )
                            CircularProgressIndicator(
                                progress = { studyProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "${(studyProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // Streak Pill and Start Studying Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { navController.navigate(Screen.StudyHistory.route) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "🔥 $currentStreak Day Streak",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { navController.navigate(Screen.StudyTimer.route) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = GeoPrimary
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("dashboard_start_studying_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Studying",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Studying", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 2-Column Metric Cards (Tasks Pending & Exam in)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Tasks Pending (FFDAD6 / 410002)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F5E6)),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(Screen.Tasks.route) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(GeoTertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Tasks Pending",
                                tint = GeoOnTertiaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Tasks Pending",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = String.format("%02d", pendingCount),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1C1B1F)
                        )
                    }
                }

                // Card 2: Exam in (D3E4FF / 001D36)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6)),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(Screen.Exams.route) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(GeoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Exam in",
                                tint = GeoOnPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = "Exam in",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF64748B)
                        )
                        if (upcomingExam != null) {
                            val daysLeft = DateUtils.daysUntil(upcomingExam!!.examDate)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format("%02d", daysLeft.coerceAtLeast(0)),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF1C1B1F)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Days",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "None",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1C1B1F)
                            )
                        }
                    }
                }
            }
        }

        // Today's Timetable Container Card (bg-[#E1E2EC], rounded-3xl)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Timetable",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "VIEW ALL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = GeoPrimary,
                            modifier = Modifier
                                .clickable { navController.navigate(Screen.Timetable.route) }
                                .padding(4.dp)
                        )
                    }

                    if (todayTimetable.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No classes scheduled for today",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }
                    } else {
                        todayTimetable.forEachIndexed { index, entry ->
                            val subject = subjects.find { it.id == entry.subjectId }
                            val stripeColor = if (index % 2 == 0) GeoPrimary else GeoOrangeAccent

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F5E6)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 4dp left color bar
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .fillMaxHeight()
                                            .background(stripeColor)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(end = 12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = entry.startTime,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = entry.endTime,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                        Spacer(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(30.dp)
                                                .background(GeoOutline)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = subject?.name ?: "Scheduled Class",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF1C1B1F)
                                            )
                                            val details = buildList {
                                                if (entry.room.isNotBlank()) add("Room ${entry.room}")
                                                if (entry.teacher.isNotBlank()) add(entry.teacher)
                                            }.joinToString(" • ")
                                            if (details.isNotBlank()) {
                                                Text(
                                                    text = details.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.SemiBold,
                                                        letterSpacing = 0.5.sp
                                                    ),
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Quick Study Entry button (bg-[#D1E4FF], text-[#001D36], py-3, rounded-2xl)
                    Button(
                        onClick = { navController.navigate(Screen.StudyTimer.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoPrimaryContainer,
                            contentColor = GeoOnPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "+ Quick Study Entry",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Quick Actions Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        QuickActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = "Start Studying",
                            containerColor = GeoPrimary,
                            contentColor = Color(0xFFADD8E6),
                            onClick = { navController.navigate(Screen.StudyTimer.route) }
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.Add,
                            label = "Add Task",
                            containerColor = Color(0xFFADD8E6),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { showAddTaskDialog = true }
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            label = "Add Subject",
                            containerColor = Color(0xFFADD8E6),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { showAddSubjectDialog = true }
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.FactCheck,
                            label = "Attendance",
                            containerColor = Color(0xFFADD8E6),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { showMarkAttendanceDialog = true }
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.EventNote,
                            label = "Add Exam",
                            containerColor = Color(0xFFADD8E6),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { showAddExamDialog = true }
                        )
                    }
                }
            }
        }

        // Upcoming Exam Banner (if any)
        if (upcomingExam != null) {
            item {
                val daysLeft = DateUtils.daysUntil(upcomingExam!!.examDate)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6)),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { navController.navigate(Screen.Exams.route) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(GeoTertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventNote,
                                contentDescription = null,
                                tint = GeoOnTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Upcoming Exam",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = StudyTertiary
                            )
                            Text(
                                text = upcomingExam!!.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = upcomingExam!!.examDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = StudyTertiary)
                        ) {
                            Text(
                                text = if (daysLeft <= 0) "Today!" else "$daysLeft d left",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Overall Syllabus Progress Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, GeoOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate(Screen.Progress.route) }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Syllabus Progress",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "$completedChapters / $totalChapters Chapters",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = GeoPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { syllabusProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = GeoPrimary,
                        trackColor = GeoOutline
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${(syllabusProgress * 100).toInt()}% completed across all subjects",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Pending Tasks Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pending Tasks ($pendingCount)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { navController.navigate(Screen.Tasks.route) }) {
                    Text("View All", color = GeoPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StudySuccess,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "All tasks completed! Great work!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            items(pendingTasks.take(3)) { task ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { viewModel.toggleTaskComplete(task) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(2.dp, GeoOutline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Circle outline for unchecked
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (task.dueDate.isNotBlank()) {
                                Text(
                                    text = "Due: ${task.dueDate} ${task.dueTime}".trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (task.priority) {
                                    "HIGH" -> GeoTertiaryContainer
                                    "MEDIUM" -> GeoOrangeAccent.copy(alpha = 0.15f)
                                    else -> GeoPrimaryContainer
                                }
                            )
                        ) {
                            Text(
                                text = task.priority,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (task.priority) {
                                    "HIGH" -> GeoOnTertiaryContainer
                                    "MEDIUM" -> Color(0xFFC2410C)
                                    else -> GeoOnPrimaryContainer
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ----------------- Dialogs for Quick Actions -----------------

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, code, teacher, target ->
                viewModel.addSubject(name, code, teacher, target, "#4F46E5")
                showAddSubjectDialog = false
            }
        )
    }

    // Add Task Dialog
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

    // Mark Attendance Dialog
    if (showMarkAttendanceDialog) {
        MarkAttendanceDialog(
            subjects = subjects,
            onDismiss = { showMarkAttendanceDialog = false },
            onConfirm = { subId, status ->
                viewModel.markAttendance(subId, status)
                showMarkAttendanceDialog = false
            }
        )
    }

    // Add Exam Dialog
    if (showAddExamDialog) {
        AddExamDialog(
            subjects = subjects,
            onDismiss = { showAddExamDialog = false },
            onConfirm = { name, date, desc, selectedSubIds ->
                viewModel.addExam(name, date, desc, selectedSubIds)
                showAddExamDialog = false
            }
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (containerColor == Color.White) BorderStroke(1.dp, GeoOutline) else null,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = contentColor
            )
        }
    }
}

// Dialog: Add Subject
@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, code: String, teacher: String, target: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("75") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Subject", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code (e.g. CS101)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher / Professor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("Target Attendance / Grade %") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val target = targetStr.toDoubleOrNull() ?: 75.0
                        onConfirm(name, code, teacher, target)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Dialog: Add Task
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, subId: Long?, type: String, priority: String, date: String, time: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf<Long?>(subjects.firstOrNull()?.id) }
    var type by remember { mutableStateOf("ASSIGNMENT") }
    var priority by remember { mutableStateOf("HIGH") }
    var dueDate by remember { mutableStateOf(DateUtils.getTodayDateString()) }
    var dueTime by remember { mutableStateOf("23:59") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, desc, selectedSubjectId, type, priority, dueDate, dueTime)
                    }
                }
            ) {
                Text("Save Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Dialog: Mark Attendance
@Composable
fun MarkAttendanceDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (subjectId: Long, status: String) -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 0L) }
    var status by remember { mutableStateOf("PRESENT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mark Attendance", fontWeight = FontWeight.Bold) },
        text = {
            if (subjects.isEmpty()) {
                Text("Please add a subject first before marking attendance.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Subject:", style = MaterialTheme.typography.labelLarge)
                    subjects.forEach { sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selectedSubjectId == sub.id) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .clickable { selectedSubjectId = sub.id }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sub.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedSubjectId == sub.id) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedSubjectId == sub.id) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status:", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { status = "PRESENT" },
                            colors = if (status == "PRESENT") androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = StudySuccess)
                            else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Present")
                        }
                        Button(
                            onClick = { status = "ABSENT" },
                            colors = if (status == "ABSENT") androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = StudyTertiary)
                            else androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Absent")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (subjects.isNotEmpty()) {
                Button(
                    onClick = {
                        if (selectedSubjectId != 0L) {
                            onConfirm(selectedSubjectId, status)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// Dialog: Add Exam
@Composable
fun AddExamDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, date: String, desc: String, subjectIds: List<Long>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(DateUtils.getTodayDateString()) }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exam", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exam Name *") },
                    placeholder = { Text("e.g. Midterm Examination") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Exam Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Notes / Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, date, desc, subjects.map { it.id })
                    }
                }
            ) {
                Text("Save Exam")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
