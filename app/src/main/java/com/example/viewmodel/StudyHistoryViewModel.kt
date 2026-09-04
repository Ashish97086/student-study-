package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Chapter
import com.example.data.entity.StudySession
import com.example.data.entity.Subject
import com.example.data.repository.StudyRepository
import com.example.utils.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StreakStats(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalStudyDays: Int = 0
)

data class SessionDisplayItem(
    val session: StudySession,
    val subjectName: String,
    val subjectColorHex: String,
    val chapterName: String?
)

@OptIn(ExperimentalCoroutinesApi::class)
class StudyHistoryViewModel(
    private val repository: StudyRepository
) : ViewModel() {

    private val todayDateStr = DateUtils.getTodayDateString()
    private val weekStartStr = DateUtils.getStartOfWeekDateString()
    private val weekEndStr = DateUtils.getEndOfWeekDateString()
    private val monthStartStr = DateUtils.getStartOfMonthDateString()
    private val monthEndStr = DateUtils.getEndOfMonthDateString()

    // Filters
    private val _filterSubjectId = MutableStateFlow<Long?>(null)
    val filterSubjectId: StateFlow<Long?> = _filterSubjectId.asStateFlow()

    private val _filterStudyType = MutableStateFlow<String?>(null)
    val filterStudyType: StateFlow<String?> = _filterStudyType.asStateFlow()

    private val _filterDate = MutableStateFlow<String?>(null)
    val filterDate: StateFlow<String?> = _filterDate.asStateFlow()

    // Subjects and Chapters
    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChapters: StateFlow<List<Chapter>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics from Room queries
    val todayStudyMinutes: StateFlow<Int> = repository.getTotalMinutesForDate(todayDateStr)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyStudyMinutes: StateFlow<Int> = repository.getTotalMinutesBetweenDates(weekStartStr, weekEndStr)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val monthlyStudyMinutes: StateFlow<Int> = repository.getTotalMinutesBetweenDates(monthStartStr, monthEndStr)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStudyMinutes: StateFlow<Int> = repository.totalStudyMinutes
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSessionsCount: StateFlow<Int> = repository.totalSessionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Streak stats
    val streakStats: StateFlow<StreakStats> = repository.studyDates
        .map { dates ->
            StreakStats(
                currentStreak = DateUtils.calculateStreak(dates),
                bestStreak = DateUtils.calculateBestStreak(dates),
                totalStudyDays = DateUtils.calculateTotalStudyDays(dates)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StreakStats())

    // Average daily study time: totalMinutes / totalStudyDays.coerceAtLeast(1)
    val averageDailyMinutes: StateFlow<Int> = combine(totalStudyMinutes, streakStats) { totalMins, stats ->
        if (stats.totalStudyDays > 0) {
            totalMins / stats.totalStudyDays
        } else {
            totalMins
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Most studied subject
    val mostStudiedSubject: StateFlow<Subject?> = combine(repository.mostStudiedSubjectId, allSubjects) { id, subjects ->
        id?.let { subId -> subjects.find { it.id == subId } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Today's sessions
    val todaySessions: StateFlow<List<SessionDisplayItem>> = combine(
        repository.getSessionsForDate(todayDateStr),
        allSubjects,
        allChapters
    ) { sessions, subjects, chapters ->
        sessions.map { session ->
            val sub = subjects.find { it.id == session.subjectId }
            val chap = chapters.find { it.id == session.chapterId }
            SessionDisplayItem(
                session = session,
                subjectName = sub?.name ?: "General Study",
                subjectColorHex = sub?.colorHex ?: "#0061A4",
                chapterName = chap?.name
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered sessions
    val filteredSessions: StateFlow<List<SessionDisplayItem>> = combine(
        _filterSubjectId,
        _filterStudyType,
        _filterDate
    ) { subId, type, date ->
        Triple(subId, type, date)
    }.flatMapLatest { (subId, type, date) ->
        repository.getFilteredSessions(subId, type, date)
    }.combine(allSubjects) { sessions, subjects ->
        Pair(sessions, subjects)
    }.combine(allChapters) { (sessions, subjects), chapters ->
        sessions.map { session ->
            val sub = subjects.find { it.id == session.subjectId }
            val chap = chapters.find { it.id == session.chapterId }
            SessionDisplayItem(
                session = session,
                subjectName = sub?.name ?: "General Study",
                subjectColorHex = sub?.colorHex ?: "#0061A4",
                chapterName = chap?.name
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilterSubject(id: Long?) {
        _filterSubjectId.value = id
    }

    fun setFilterStudyType(type: String?) {
        _filterStudyType.value = type
    }

    fun setFilterDate(date: String?) {
        _filterDate.value = date
    }

    fun clearFilters() {
        _filterSubjectId.value = null
        _filterStudyType.value = null
        _filterDate.value = null
    }

    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
