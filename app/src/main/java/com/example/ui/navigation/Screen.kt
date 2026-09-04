package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "") {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard", "Dashboard")
    object Subjects : Screen("subjects", "Subjects")
    object SubjectDetail : Screen("subject_detail/{subjectId}", "Subject Details") {
        fun createRoute(subjectId: Long) = "subject_detail/$subjectId"
    }
    object StudyTimer : Screen("study_timer", "Study")
    object Tasks : Screen("tasks", "Tasks")
    object More : Screen("more", "More")

    // Screens linked from More
    object StudyHistory : Screen("study_history", "Study History")
    object Timetable : Screen("timetable", "Timetable")
    object Exams : Screen("exams", "Exams")
    object Attendance : Screen("attendance", "Attendance")
    object Marks : Screen("marks", "Marks")
    object Progress : Screen("progress", "Analytics & Progress")
    object Profile : Screen("profile", "Profile")
    object BackupRestore : Screen("backup_restore", "Backup & Restore")
    object Settings : Screen("settings", "Settings")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard.route, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Subjects.route, "Subjects", Icons.AutoMirrored.Filled.MenuBook),
    BottomNavItem(Screen.StudyTimer.route, "Study", Icons.Filled.Timer),
    BottomNavItem(Screen.Tasks.route, "Tasks", Icons.Filled.CheckCircle),
    BottomNavItem(Screen.More.route, "More", Icons.Filled.MoreHoriz)
)
