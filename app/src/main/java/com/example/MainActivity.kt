package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.database.AppDatabase
import com.example.data.repository.StudyRepository
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.StudyHistoryViewModel
import com.example.viewmodel.StudyTimerViewModel
import com.example.viewmodel.StudyViewModel
import com.example.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val factory by lazy {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = StudyRepository(database)
        ViewModelFactory(application, repository)
    }

    private val viewModel: StudyViewModel by viewModels { factory }
    private val timerViewModel: StudyTimerViewModel by viewModels { factory }
    private val historyViewModel: StudyHistoryViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation(
                    viewModel = viewModel,
                    timerViewModel = timerViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }
    }
}

