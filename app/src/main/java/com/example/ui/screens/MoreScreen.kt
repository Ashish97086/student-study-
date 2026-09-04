package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.navigation.Screen
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StudySuccess
import com.example.ui.theme.StudyTertiary
import com.example.ui.theme.StudyWarning

@Composable
fun MoreScreen(
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "More Features",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1C1B1F)
            )
            Text(
                text = "Academics, attendance, marks, analytics and settings",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.CalendarMonth,
                iconTint = GeoPrimary,
                title = "Timetable",
                subtitle = "Weekly schedule, classes & lecture rooms",
                onClick = { navController.navigate(Screen.Timetable.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.EventNote,
                iconTint = StudyTertiary,
                title = "Exams",
                subtitle = "Exam schedules, syllabus countdown & prep",
                onClick = { navController.navigate(Screen.Exams.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.FactCheck,
                iconTint = StudySuccess,
                title = "Attendance",
                subtitle = "Daily presence & subject attendance percentage",
                onClick = { navController.navigate(Screen.Attendance.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Grade,
                iconTint = StudyWarning,
                title = "Marks & Grades",
                subtitle = "Exam results, test scores & performance",
                onClick = { navController.navigate(Screen.Marks.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconTint = GeoPrimary,
                title = "Analytics & Progress",
                subtitle = "Study streaks, hours logged & syllabus charts",
                onClick = { navController.navigate(Screen.Progress.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.History,
                iconTint = Color(0xFF00897B),
                title = "Study History & Stats",
                subtitle = "Daily/weekly/monthly breakdown & session logs",
                onClick = { navController.navigate(Screen.StudyHistory.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Person,
                iconTint = GeoPrimary,
                title = "Student Profile",
                subtitle = "Name, degree, semester & study targets",
                onClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Storage,
                iconTint = StudySuccess,
                title = "Backup & Restore",
                subtitle = "Offline JSON export & import (Keep your data safe)",
                onClick = { navController.navigate(Screen.BackupRestore.route) }
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Settings,
                iconTint = Color(0xFF64748B),
                title = "Settings",
                subtitle = "Preferences, Pomodoro defaults & app info",
                onClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MoreMenuCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GeoOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("more_menu_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1C1B1F)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
