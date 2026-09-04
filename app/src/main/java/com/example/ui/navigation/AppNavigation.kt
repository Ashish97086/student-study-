package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamsScreen
import com.example.ui.screens.MarksScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudyHistoryScreen
import com.example.ui.screens.StudyTimerScreen
import com.example.ui.screens.SubjectDetailScreen
import com.example.ui.screens.SubjectsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TimetableScreen
import com.example.viewmodel.StudyHistoryViewModel
import com.example.viewmodel.StudyTimerViewModel
import com.example.viewmodel.StudyViewModel

@Composable
fun AppNavigation(
    viewModel: StudyViewModel,
    timerViewModel: StudyTimerViewModel,
    historyViewModel: StudyHistoryViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Subjects.route,
        Screen.StudyTimer.route,
        Screen.Tasks.route,
        Screen.More.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                            ),
                            modifier = Modifier.testTag("nav_${item.title.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Subjects.route) {
                SubjectsScreen(navController = navController, viewModel = viewModel)
            }
            composable(
                route = Screen.SubjectDetail.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                SubjectDetailScreen(subjectId = subjectId, navController = navController, viewModel = viewModel)
            }
            composable(Screen.StudyTimer.route) {
                StudyTimerScreen(navController = navController, viewModel = timerViewModel)
            }
            composable(Screen.StudyHistory.route) {
                StudyHistoryScreen(navController = navController, viewModel = historyViewModel)
            }
            composable(Screen.Tasks.route) {
                TasksScreen(viewModel = viewModel)
            }
            composable(Screen.More.route) {
                MoreScreen(navController = navController)
            }
            composable(Screen.Timetable.route) {
                TimetableScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Exams.route) {
                ExamsScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Attendance.route) {
                AttendanceScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Marks.route) {
                MarksScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Progress.route) {
                ProgressScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.BackupRestore.route) {
                BackupRestoreScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
