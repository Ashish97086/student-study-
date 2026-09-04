package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timer.PomodoroPhase
import com.example.timer.TimerMode
import com.example.timer.TimerStatus
import com.example.ui.navigation.Screen
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOrangeAccent
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoStreakGreen
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.StudyWarning
import com.example.viewmodel.StudyTimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTimerScreen(
    navController: NavController,
    viewModel: StudyTimerViewModel
) {
    val context = LocalContext.current

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
    val timerStatus by viewModel.timerStatus.collectAsStateWithLifecycle()
    val pomodoroPhase by viewModel.pomodoroPhase.collectAsStateWithLifecycle()
    val currentCycle by viewModel.currentCycle.collectAsStateWithLifecycle()
    val totalCycles by viewModel.totalCycles.collectAsStateWithLifecycle()

    val pomodoroStudyMins by viewModel.pomodoroStudyMinutes.collectAsStateWithLifecycle()
    val pomodoroBreakMins by viewModel.pomodoroBreakMinutes.collectAsStateWithLifecycle()
    val pomodoroLongBreakMins by viewModel.pomodoroLongBreakMinutes.collectAsStateWithLifecycle()
    val standardTimerMins by viewModel.standardTimerMinutes.collectAsStateWithLifecycle()

    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val totalSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400),
        label = "timer_progress"
    )

    val selectedSubjectId by viewModel.selectedSubjectId.collectAsStateWithLifecycle()
    val selectedSubjectName by viewModel.selectedSubjectName.collectAsStateWithLifecycle()
    val selectedChapterId by viewModel.selectedChapterId.collectAsStateWithLifecycle()
    val selectedChapterName by viewModel.selectedChapterName.collectAsStateWithLifecycle()
    val selectedStudyType by viewModel.selectedStudyType.collectAsStateWithLifecycle()

    val pendingStopSession by viewModel.pendingStopSession.collectAsStateWithLifecycle()
    val completionMessage by viewModel.completionMessage.collectAsStateWithLifecycle()

    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val selectedSubject = allSubjects.find { it.id == selectedSubjectId }

    val chaptersFlow = remember(selectedSubjectId) {
        selectedSubjectId?.let { viewModel.getChaptersForSubject(it) }
    }
    val chapters by (chaptersFlow?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyList()) })

    // UI state
    var showSubjectDropdown by remember { mutableStateOf(false) }
    var showChapterDropdown by remember { mutableStateOf(false) }
    var showCustomDurationDialog by remember { mutableStateOf(false) }
    var showPomodoroSettingsDialog by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("") }

    // Format remaining time
    val formattedTime = remember(remainingSeconds) {
        val hrs = remainingSeconds / 3600
        val mins = (remainingSeconds % 3600) / 60
        val secs = remainingSeconds % 60
        if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Study Timer",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1C1B1F)
                ),
                actions = {
                    IconButton(
                        onClick = { showPomodoroSettingsDialog = true },
                        modifier = Modifier.testTag("study_timer_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pomodoro Settings",
                            tint = Color(0xFF64748B)
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate(Screen.StudyHistory.route) },
                        modifier = Modifier.testTag("study_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Study History",
                            tint = GeoPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Mode Selector Tabs (Pomodoro vs Study Timer)
                TabRow(
                    selectedTabIndex = if (timerMode == TimerMode.POMODORO) 0 else 1,
                    containerColor = GeoSurfaceVariant,
                    contentColor = GeoPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .fillMaxWidth()
                ) {
                    Tab(
                        selected = timerMode == TimerMode.POMODORO,
                        onClick = {
                            viewModel.setTimerMode(TimerMode.POMODORO)
                        },
                        text = {
                            Text(
                                "Pomodoro Mode",
                                fontWeight = FontWeight.Bold,
                                color = if (timerMode == TimerMode.POMODORO) GeoPrimary else Color(0xFF647488)
                            )
                        }
                    )
                    Tab(
                        selected = timerMode == TimerMode.STUDY_TIMER,
                        onClick = {
                            viewModel.setTimerMode(TimerMode.STUDY_TIMER)
                        },
                        text = {
                            Text(
                                "Study Timer",
                                fontWeight = FontWeight.Bold,
                                color = if (timerMode == TimerMode.STUDY_TIMER) GeoPrimary else Color(0xFF64748B)
                            )
                        }
                    )
                }
            }

            // Subject & Chapter Selectors Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Target Subject & Chapter",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF64748B)
                        )

                        // Subject Selector Row
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoSurfaceVariant.copy(alpha = 0.5f))
                                    .border(1.dp, GeoOutline, RoundedCornerShape(12.dp))
                                    .clickable(enabled = timerStatus == TimerStatus.IDLE) {
                                        showSubjectDropdown = true
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                                    .testTag("study_subject_selector"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(
                                                selectedSubject?.colorHex?.let {
                                                    try { Color(android.graphics.Color.parseColor(it)) }
                                                    catch (e: Exception) { GeoPrimary }
                                                } ?: GeoPrimary
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = selectedSubject?.name ?: "General Study (No Subject)",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color(0xFF1C1B1F)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B)
                                )
                            }

                            DropdownMenu(
                                expanded = showSubjectDropdown,
                                onDismissRequest = { showSubjectDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("General Study (No Subject)") },
                                    onClick = {
                                        viewModel.selectSubject(null)
                                        showSubjectDropdown = false
                                    }
                                )
                                allSubjects.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub.name) },
                                        onClick = {
                                            viewModel.selectSubject(sub)
                                            showSubjectDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Chapter Selector Row (Optional)
                        if (selectedSubjectId != null && chapters.isNotEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GeoSurfaceVariant.copy(alpha = 0.5f))
                                        .border(1.dp, GeoOutline, RoundedCornerShape(12.dp))
                                        .clickable(enabled = timerStatus == TimerStatus.IDLE) {
                                            showChapterDropdown = true
                                        }
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                        .testTag("study_chapter_selector"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = GeoPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = selectedChapterName ?: "Select Chapter (Optional)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = if (selectedChapterName != null) Color(0xFF1C1B1F) else Color(0xFF64748B)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF64748B)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showChapterDropdown,
                                    onDismissRequest = { showChapterDropdown = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("None (General Subject Study)") },
                                        onClick = {
                                            viewModel.selectChapter(null)
                                            showChapterDropdown = false
                                        }
                                    )
                                    chapters.forEach { chap ->
                                        DropdownMenuItem(
                                            text = { Text(chap.name) },
                                            onClick = {
                                                viewModel.selectChapter(chap)
                                                showChapterDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Study Type Selector
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Study Type",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.studyTypes.forEach { type ->
                            val isSelected = selectedStudyType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GeoPrimary else Color.White)
                                    .border(1.dp, if (isSelected) GeoPrimary else GeoOutline, RoundedCornerShape(12.dp))
                                    .clickable(enabled = timerStatus == TimerStatus.IDLE) {
                                        viewModel.selectStudyType(type)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
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
            }

            // Pomodoro Cycle Indicator (if Pomodoro mode)
            if (timerMode == TimerMode.POMODORO) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (pomodoroPhase) {
                                PomodoroPhase.STUDY -> GeoPrimaryContainer
                                PomodoroPhase.SHORT_BREAK -> Color(0xFFE8F5E9)
                                PomodoroPhase.LONG_BREAK -> Color(0xFFFFF3E0)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = when (pomodoroPhase) {
                                        PomodoroPhase.STUDY -> "Session $currentCycle of $totalCycles"
                                        PomodoroPhase.SHORT_BREAK -> "☕ Short Break ($pomodoroBreakMins min)"
                                        PomodoroPhase.LONG_BREAK -> "🏆 Long Break ($pomodoroLongBreakMins min)"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = when (pomodoroPhase) {
                                        PomodoroPhase.STUDY -> GeoOnPrimaryContainer
                                        PomodoroPhase.SHORT_BREAK -> Color(0xFF2E7D32)
                                        PomodoroPhase.LONG_BREAK -> Color(0xFFE65100)
                                    }
                                )
                                Text(
                                    text = when (pomodoroPhase) {
                                        PomodoroPhase.STUDY -> "Focus on your tasks with zero distractions"
                                        PomodoroPhase.SHORT_BREAK -> "Stretch, hydrate, and relax your eyes"
                                        PomodoroPhase.LONG_BREAK -> "Awesome job! Take a longer rest"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                            }

                            if (pomodoroPhase != PomodoroPhase.STUDY) {
                                Button(
                                    onClick = { viewModel.skipBreak() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GeoPrimary,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Skip Break",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Skip", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Big Circular Countdown Dial
            item {
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(260.dp)
                ) {
                    // Background track
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = GeoOutline,
                        strokeWidth = 14.dp
                    )

                    // Animated progress indicator
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = when {
                            pomodoroPhase != PomodoroPhase.STUDY && timerMode == TimerMode.POMODORO -> GeoStreakGreen
                            timerStatus == TimerStatus.PAUSED -> StudyWarning
                            else -> GeoPrimary
                        },
                        strokeWidth = 14.dp
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when {
                                timerStatus == TimerStatus.RUNNING -> {
                                    if (timerMode == TimerMode.POMODORO && pomodoroPhase != PomodoroPhase.STUDY) "Break Active"
                                    else "Focus Session Active"
                                }
                                timerStatus == TimerStatus.PAUSED -> "Timer Paused"
                                timerStatus == TimerStatus.COMPLETED -> "Session Complete!"
                                else -> "Ready to Focus"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                timerStatus == TimerStatus.RUNNING -> GeoStreakGreen
                                timerStatus == TimerStatus.PAUSED -> StudyWarning
                                else -> Color(0xFF64748B)
                            }
                        )

                        // Display active subject & study type pill
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GeoSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${selectedSubject?.name ?: "General Study"} • $selectedStudyType",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Duration Presets (Study Timer Mode only, when IDLE)
            if (timerMode == TimerMode.STUDY_TIMER && timerStatus == TimerStatus.IDLE) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Timer Presets",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Red,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 25, 30, 45, 60).forEach { mins ->
                                val isSelected = standardTimerMins == mins
                                Button(
                                    onClick = { viewModel.setStandardTimerDuration(mins) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) GeoPrimary else Color.White,
                                        contentColor = if (isSelected) Color.White else Color(0xFF1C1B1F)
                                    ),
                                    border = BorderStroke(1.dp, if (isSelected) GeoPrimary else GeoOutline)
                                ) {
                                    Text("${mins}m", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            // Custom duration button
                            OutlinedButton(
                                onClick = {
                                    customMinutesInput = standardTimerMins.toString()
                                    showCustomDurationDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, GeoOutline),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = GeoPrimary
                                )
                            ) {
                                Text("Custom", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Control Buttons Row (Start / Pause / Resume / Stop / Reset)
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    // Reset Button
                    IconButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, GeoOutline, CircleShape)
                            .testTag("timer_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Timer",
                            tint = Color(0xFF64748B)
                        )
                    }

                    // Main Action Button (Start / Pause / Resume)
                    Button(
                        onClick = {
                            checkAndRequestNotificationPermission()
                            when (timerStatus) {
                                TimerStatus.IDLE -> viewModel.startTimer()
                                TimerStatus.RUNNING -> viewModel.pauseTimer()
                                TimerStatus.PAUSED -> viewModel.resumeTimer()
                                TimerStatus.COMPLETED -> viewModel.resetTimer()
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (timerStatus) {
                                TimerStatus.RUNNING -> StudyWarning
                                TimerStatus.PAUSED -> GeoStreakGreen
                                else -> GeoPrimary
                            }
                        ),
                        modifier = Modifier
                            .size(76.dp)
                            .testTag("timer_toggle_button")
                    ) {
                        Icon(
                            imageVector = when (timerStatus) {
                                TimerStatus.RUNNING -> Icons.Default.Pause
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = when (timerStatus) {
                                TimerStatus.RUNNING -> "Pause"
                                TimerStatus.PAUSED -> "Resume"
                                else -> "Start"
                            },
                            modifier = Modifier.size(38.dp),
                            tint = Color.White
                        )
                    }

                    // Stop Button (Only enabled when RUNNING or PAUSED)
                    IconButton(
                        onClick = { viewModel.stopTimer() },
                        enabled = timerStatus == TimerStatus.RUNNING || timerStatus == TimerStatus.PAUSED,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (timerStatus == TimerStatus.RUNNING || timerStatus == TimerStatus.PAUSED) Color(0xFFFFEBEE) else GeoSurfaceVariant)
                            .border(
                                1.dp,
                                if (timerStatus == TimerStatus.RUNNING || timerStatus == TimerStatus.PAUSED) Color(0xFFFFCDD2) else GeoOutline,
                                CircleShape
                            )
                            .testTag("timer_stop_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Timer",
                            tint = if (timerStatus == TimerStatus.RUNNING || timerStatus == TimerStatus.PAUSED) Color(0xFFD32F2F) else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Quick Link to Study History
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, GeoOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.StudyHistory.route) }
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "View Study History & Statistics",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF1C1B1F)
                                )
                                Text(
                                    text = "Track today's, weekly and monthly study progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // ----------------- DIALOGS -----------------

    // 1. "Save this study session?" Dialog on Stop
    pendingStopSession?.let { pending ->
        AlertDialog(
            onDismissRequest = { viewModel.discardStoppedSession() },
            title = {
                Text(
                    text = "Save this study session?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You studied for ${pending.elapsedMinutes} minute(s). Would you like to log this session in your study history?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GeoSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Subject: ${selectedSubject?.name ?: "General Study"}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            if (selectedChapterName != null) {
                                Text(
                                    text = "Chapter: $selectedChapterName",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = "Type: ${pending.studyType} • ${pending.elapsedMinutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmSaveStoppedSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    modifier = Modifier.testTag("save_stopped_session_button")
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.discardStoppedSession() },
                    modifier = Modifier.testTag("discard_stopped_session_button")
                ) {
                    Text("Discard", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // 2. Completion Message Celebration Dialog
    completionMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissCompletionMessage() },
            title = {
                Text(
                    text = "Session Finished! 🎉",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissCompletionMessage() },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("Awesome!", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 3. Custom Duration Input Dialog
    if (showCustomDurationDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDurationDialog = false },
            title = { Text("Set Custom Timer", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter duration in minutes (1 - 300):", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = customMinutesInput,
                        onValueChange = { customMinutesInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Minutes") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = customMinutesInput.toIntOrNull() ?: 25
                        val clamped = mins.coerceIn(1, 300)
                        viewModel.setStandardTimerDuration(clamped)
                        showCustomDurationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("Set", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDurationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Pomodoro Settings Configuration Dialog
    if (showPomodoroSettingsDialog) {
        var studyInput by remember { mutableStateOf(pomodoroStudyMins.toString()) }
        var breakInput by remember { mutableStateOf(pomodoroBreakMins.toString()) }
        var longBreakInput by remember { mutableStateOf(pomodoroLongBreakMins.toString()) }
        var cyclesInput by remember { mutableStateOf(totalCycles.toString()) }

        AlertDialog(
            onDismissRequest = { showPomodoroSettingsDialog = false },
            title = { Text("Pomodoro Settings", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = studyInput,
                        onValueChange = { studyInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Study Duration (min)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = breakInput,
                        onValueChange = { breakInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Break Duration (min)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = longBreakInput,
                        onValueChange = { longBreakInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Long Break Duration (min)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cyclesInput,
                        onValueChange = { cyclesInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Sessions before Long Break") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sMins = (studyInput.toIntOrNull() ?: 25).coerceIn(1, 180)
                        val bMins = (breakInput.toIntOrNull() ?: 5).coerceIn(1, 60)
                        val lbMins = (longBreakInput.toIntOrNull() ?: 15).coerceIn(1, 120)
                        val cyc = (cyclesInput.toIntOrNull() ?: 4).coerceIn(1, 12)
                        viewModel.updatePomodoroSettings(sMins, bMins, lbMins, cyc)
                        showPomodoroSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPomodoroSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
