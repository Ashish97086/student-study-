package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.StudyRepository

class ViewModelFactory(
    private val application: Application,
    private val repository: StudyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            return StudyViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(StudyTimerViewModel::class.java)) {
            return StudyTimerViewModel(application, repository) as T
        }
        if (modelClass.isAssignableFrom(StudyHistoryViewModel::class.java)) {
            return StudyHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
