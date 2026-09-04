package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.Chapter
import com.example.data.entity.Subject
import com.example.data.repository.StudyRepository
import com.example.timer.PendingStopSession
import com.example.timer.PomodoroPhase
import com.example.timer.StudyTimerManager
import com.example.timer.TimerMode
import com.example.timer.TimerStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StudyTimerViewModel(
    application: Application,
    private val repository: StudyRepository
) : AndroidViewModel(application) {

    val timerMode: StateFlow<TimerMode> = StudyTimerManager.timerMode
    val timerStatus: StateFlow<TimerStatus> = StudyTimerManager.timerStatus
    val pomodoroPhase: StateFlow<PomodoroPhase> = StudyTimerManager.pomodoroPhase
    val currentCycle: StateFlow<Int> = StudyTimerManager.currentCycle
    val totalCycles: StateFlow<Int> = StudyTimerManager.totalCycles

    val pomodoroStudyMinutes: StateFlow<Int> = StudyTimerManager.pomodoroStudyMinutes
    val pomodoroBreakMinutes: StateFlow<Int> = StudyTimerManager.pomodoroBreakMinutes
    val pomodoroLongBreakMinutes: StateFlow<Int> = StudyTimerManager.pomodoroLongBreakMinutes
    val standardTimerMinutes: StateFlow<Int> = StudyTimerManager.standardTimerMinutes

    val remainingSeconds: StateFlow<Int> = StudyTimerManager.remainingSeconds
    val totalDurationSeconds: StateFlow<Int> = StudyTimerManager.totalDurationSeconds

    val progress: StateFlow<Float> = combine(remainingSeconds, totalDurationSeconds) { remaining, total ->
        if (total > 0) {
            ((total - remaining).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val selectedSubjectId: StateFlow<Long?> = StudyTimerManager.selectedSubjectId
    val selectedSubjectName: StateFlow<String?> = StudyTimerManager.selectedSubjectName
    val selectedChapterId: StateFlow<Long?> = StudyTimerManager.selectedChapterId
    val selectedChapterName: StateFlow<String?> = StudyTimerManager.selectedChapterName
    val selectedStudyType: StateFlow<String> = StudyTimerManager.selectedStudyType

    val pendingStopSession: StateFlow<PendingStopSession?> = StudyTimerManager.pendingStopSession
    val completionMessage: StateFlow<String?> = StudyTimerManager.completionMessage

    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTypes: List<String> = StudyTimerManager.studyTypes

    fun getChaptersForSubject(subjectId: Long): StateFlow<List<Chapter>> {
        return repository.getChaptersForSubject(subjectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setTimerMode(mode: TimerMode) {
        StudyTimerManager.setTimerMode(mode)
    }

    fun selectSubject(subject: Subject?) {
        StudyTimerManager.setSubject(subject?.id, subject?.name)
    }

    fun selectChapter(chapter: Chapter?) {
        StudyTimerManager.setChapter(chapter?.id, chapter?.name)
    }

    fun selectStudyType(type: String) {
        StudyTimerManager.setStudyType(type)
    }

    fun setStandardTimerDuration(minutes: Int) {
        StudyTimerManager.setStandardTimerDuration(minutes)
    }

    fun updatePomodoroSettings(studyMins: Int, breakMins: Int, longBreakMins: Int, cycles: Int) {
        StudyTimerManager.updatePomodoroSettings(studyMins, breakMins, longBreakMins, cycles)
    }

    fun startTimer() {
        StudyTimerManager.startTimer(getApplication())
    }

    fun pauseTimer() {
        StudyTimerManager.pauseTimer(getApplication())
    }

    fun resumeTimer() {
        StudyTimerManager.resumeTimer(getApplication())
    }

    fun stopTimer() {
        StudyTimerManager.stopTimer(getApplication())
    }

    fun confirmSaveStoppedSession() {
        val database = AppDatabase.getDatabase(getApplication())
        StudyTimerManager.confirmSaveStoppedSession(getApplication(), database)
    }

    fun discardStoppedSession() {
        StudyTimerManager.discardStoppedSession()
    }

    fun resetTimer() {
        StudyTimerManager.resetTimer(getApplication())
    }

    fun skipBreak() {
        StudyTimerManager.skipBreak(getApplication())
    }

    fun dismissCompletionMessage() {
        StudyTimerManager.dismissCompletionMessage()
    }
}
