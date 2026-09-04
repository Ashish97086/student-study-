package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.entity.StudySession
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOrangeAccent
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoStreakGreen
import com.example.ui.theme.GeoSurfaceVariant
import com.example.utils.DateUtils
import com.example.viewmodel.SessionDisplayItem
import com.example.viewmodel.StudyHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyHistoryScreen(
    navController: NavController,
    viewModel: StudyHistoryViewModel
) {
    val todayMinutes by viewModel.todayStudyMinutes.collectAsStateWithLifecycle()
    val weeklyMinutes by viewModel.weeklyStudyMinutes.collectAsStateWithLifecycle()
    val monthlyMinutes by viewModel.monthlyStudyMinutes.collectAsStateWithLifecycle()
    val totalMinutes by viewModel.totalStudyMinutes.collectAsStateWithLifecycle()
    val totalSessionsCount by viewModel.totalSessionsCount.collectAsStateWithLifecycle()
    val averageDailyMinutes by viewModel.averageDailyMinutes.collectAsStateWithLifecycle()
    val mostStudiedSubject by viewModel.mostStudiedSubject.collectAsStateWithLifecycle()
    val streakStats by viewModel.streakStats.collectAsStateWithLifecycle()

    val todaySessions by viewModel.todaySessions.collectAsStateWithLifecycle()
    val filteredSessions by viewModel.filteredSessions.collectAsStateWithLifecycle()

    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val filterSubjectId by viewModel.filterSubjectId.collectAsStateWithLifecycle()
    val filterStudyType by viewModel.filterStudyType.collectAsStateWithLifecycle()

    var sessionToDelete by remember { mutableStateOf<StudySession?>(null) }
    var showSubjectFilterMenu by remember { mutableStateOf(false) }

    val studyTypes = listOf(
        "Normal Study",
        "Revision",
        "Practice",
        "Homework",
        "Assignment",
        "Exam Preparation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Study History & Stats",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.testTag("study_history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1C1B1F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1C1B1F)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Streak Showcase Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = BorderStroke(1.dp, Color(0xFFFFE082)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFECB3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = GeoOrangeAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "${streakStats.currentStreak} Day Streak 🔥",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFB78103)
                                )
                                Text(
                                    text = "Best: ${streakStats.bestStreak} days • Total days: ${streakStats.totalStudyDays}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF795548)
                                )
                            }
                        }
                    }
                }
            }

            // Statistics Grid: Daily, Weekly, Monthly, Total
            item {
                Text(
                    text = "Study Statistics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1C1B1F)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Today & This Week
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Today",
                            value = DateUtils.formatMinutes(todayMinutes),
                            subtitle = "${todaySessions.size} session(s)",
                            accentColor = GeoPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "This Week",
                            value = DateUtils.formatMinutes(weeklyMinutes),
                            subtitle = "Mon - Sun",
                            accentColor = GeoStreakGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: This Month & Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "This Month",
                            value = DateUtils.formatMinutes(monthlyMinutes),
                            subtitle = "Calendar month",
                            accentColor = Color(0xFF8E24AA),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "All Time",
                            value = DateUtils.formatMinutes(totalMinutes),
                            subtitle = "$totalSessionsCount total sessions",
                            accentColor = Color(0xFF00897B),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Daily Average & Most Studied Subject
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Daily Average",
                            value = DateUtils.formatMinutes(averageDailyMinutes),
                            subtitle = "Per active study day",
                            accentColor = Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Top Subject",
                            value = mostStudiedSubject?.name ?: "N/A",
                            subtitle = "Most time logged",
                            accentColor = GeoPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Filters Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Study Sessions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1C1B1F)
                    )

                    if (filterSubjectId != null || filterStudyType != null) {
                        TextButton(onClick = { viewModel.clearFilters() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Filters",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFDC2626)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Filters", color = Color(0xFFDC2626), fontSize = 12.sp)
                        }
                    }
                }

                // Horizontal filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Subject Filter Chip
                    Box {
                        val activeSubject = allSubjects.find { it.id == filterSubjectId }
                        val isSubActive = filterSubjectId != null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSubActive) GeoPrimaryContainer else Color.White)
                                .border(1.dp, if (isSubActive) GeoPrimary else GeoOutline, RoundedCornerShape(12.dp))
                                .clickable { showSubjectFilterMenu = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("filter_subject_chip")
                        ) {
                            Text(
                                text = activeSubject?.name?.let { "Subject: $it" } ?: "All Subjects",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSubActive) GeoPrimary else Color(0xFF1C1B1F)
                            )
                        }

                        DropdownMenu(
                            expanded = showSubjectFilterMenu,
                            onDismissRequest = { showSubjectFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Subjects") },
                                onClick = {
                                    viewModel.setFilterSubject(null)
                                    showSubjectFilterMenu = false
                                }
                            )
                            allSubjects.forEach { sub ->
                                DropdownMenuItem(
                                    text = { Text(sub.name) },
                                    onClick = {
                                        viewModel.setFilterSubject(sub.id)
                                        showSubjectFilterMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Study Type Chips
                    studyTypes.forEach { type ->
                        val isSelected = filterStudyType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GeoPrimary else Color.White)
                                .border(1.dp, if (isSelected) GeoPrimary else GeoOutline, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isSelected) viewModel.setFilterStudyType(null)
                                    else viewModel.setFilterStudyType(type)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else Color(0xFF1C1B1F)
                            )
                        }
                    }
                }
            }

            // Sessions List
            if (filteredSessions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GeoOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No study sessions found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1C1B1F)
                            )
                            Text(
                                text = "Complete study sessions with the timer to see them here",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                items(filteredSessions, key = { it.session.id }) { item ->
                    SessionItemCard(
                        item = item,
                        onDelete = { sessionToDelete = item.session }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Delete Session", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete this ${session.durationMinutes}-minute ${session.studyType} session? This will update your statistics.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSession(session)
                        sessionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GeoOutline),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun SessionItemCard(
    item: SessionDisplayItem,
    onDelete: () -> Unit
) {
    val subColor = remember(item.subjectColorHex) {
        try { Color(android.graphics.Color.parseColor(item.subjectColorHex)) }
        catch (e: Exception) { GeoPrimary }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GeoOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Color bar indicator
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(subColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = item.subjectName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1C1B1F)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (item.chapterName != null) {
                            Text(
                                text = item.chapterName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = Color(0xFF475569)
                            )
                            Text(text = "•", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Text(
                            text = item.session.studyType,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = GeoPrimary
                        )
                    }

                    // Date & Time range
                    Text(
                        text = "${item.session.date} | ${DateUtils.formatTimeRange(item.session.startTime, item.session.endTime)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Duration pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GeoPrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${item.session.durationMinutes} min",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GeoOnPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Session",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
