package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Attendance
import com.example.data.entity.Chapter
import com.example.data.entity.Exam
import com.example.data.entity.ExamSubject
import com.example.data.entity.Mark
import com.example.data.entity.StudentProfile
import com.example.data.entity.StudySession
import com.example.data.entity.Subject
import com.example.data.entity.Task
import com.example.data.entity.TimetableEntry
import com.example.data.repository.StudyRepository
import com.example.utils.DateUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class StudyViewModel(private val repository: StudyRepository) : ViewModel() {

    // Profile & Onboarding State
    val studentProfile: StateFlow<StudentProfile?> = repository.studentProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Subjects
    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Chapters
    val allChapters: StateFlow<List<Chapter>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalChaptersCount: StateFlow<Int> = repository.totalChaptersCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedChaptersCount: StateFlow<Int> = repository.completedChaptersCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Tasks
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTasks: StateFlow<List<Task>> = repository.pendingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTaskCount: StateFlow<Int> = repository.pendingTaskCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Today's Study Sessions & Time
    val todayDate = DateUtils.getTodayDateString()
    val todaySessions: StateFlow<List<StudySession>> = repository.getSessionsForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudySessions: StateFlow<List<StudySession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayStudyMinutes: StateFlow<Int?> = repository.getTotalMinutesForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStudyMinutes: StateFlow<Int?> = repository.totalStudyMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val studyDates: StateFlow<List<String>> = repository.studyDates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timetable
    val currentDay = DateUtils.getCurrentDayOfWeek()
    val todayTimetableEntries: StateFlow<List<TimetableEntry>> = repository.getEntriesForDay(currentDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTimetableEntries: StateFlow<List<TimetableEntry>> = repository.allTimetableEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Exams
    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingExam: StateFlow<Exam?> = repository.getUpcomingExam(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Attendance
    val allAttendance: StateFlow<List<Attendance>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Marks
    val allMarks: StateFlow<List<Mark>> = repository.allMarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timer State
    private val _timerDurationSeconds = MutableStateFlow(25 * 60) // 25 min default
    val timerDurationSeconds: StateFlow<Int> = _timerDurationSeconds.asStateFlow()

    private val _timerSecondsLeft = MutableStateFlow(25 * 60)
    val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerMode = MutableStateFlow("POMODORO") // POMODORO or STOPWATCH
    val timerMode: StateFlow<String> = _timerMode.asStateFlow()

    private val _stopwatchSeconds = MutableStateFlow(0)
    val stopwatchSeconds: StateFlow<Int> = _stopwatchSeconds.asStateFlow()

    private val _selectedSubjectIdForStudy = MutableStateFlow<Long?>(null)
    val selectedSubjectIdForStudy: StateFlow<Long?> = _selectedSubjectIdForStudy.asStateFlow()

    private var timerJob: Job? = null

    // Backup & Restore message state
    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    // ----------------- Profile Operations -----------------
    fun saveOnboardingProfile(name: String, course: String, semester: String, targetHours: Int) {
        viewModelScope.launch {
            val profile = StudentProfile(
                name = name.trim(),
                course = course.trim(),
                semester = semester.trim(),
                dailyStudyTarget = targetHours * 60
            )
            repository.saveProfile(profile)
        }
    }

    fun updateProfile(profile: StudentProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    // ----------------- Subject Operations -----------------
    fun addSubject(name: String, code: String, teacher: String, targetPercentage: Double, colorHex: String) {
        viewModelScope.launch {
            val subject = Subject(
                name = name.trim(),
                code = code.trim(),
                teacher = teacher.trim(),
                targetPercentage = targetPercentage,
                colorHex = colorHex
            )
            repository.insertSubject(subject)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            repository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // ----------------- Chapter Operations -----------------
    fun getChaptersForSubject(subjectId: Long): StateFlow<List<Chapter>> {
        return repository.getChaptersForSubject(subjectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addChapter(subjectId: Long, name: String, description: String, priority: String) {
        viewModelScope.launch {
            val chapter = Chapter(
                subjectId = subjectId,
                name = name.trim(),
                description = description.trim(),
                priority = priority,
                progressPercentage = 0,
                status = "NOT_STARTED"
            )
            repository.insertChapter(chapter)
        }
    }

    fun updateChapterProgress(chapter: Chapter, newPercentage: Int) {
        viewModelScope.launch {
            val status = when {
                newPercentage >= 100 -> "COMPLETED"
                newPercentage > 0 -> "IN_PROGRESS"
                else -> "NOT_STARTED"
            }
            val completedAt = if (status == "COMPLETED") System.currentTimeMillis() else null
            repository.updateChapter(
                chapter.copy(
                    progressPercentage = newPercentage.coerceIn(0, 100),
                    status = status,
                    completedAt = completedAt
                )
            )
        }
    }

    fun toggleChapterComplete(chapter: Chapter) {
        viewModelScope.launch {
            val isNowCompleted = chapter.status != "COMPLETED"
            val updated = chapter.copy(
                status = if (isNowCompleted) "COMPLETED" else "NOT_STARTED",
                progressPercentage = if (isNowCompleted) 100 else 0,
                completedAt = if (isNowCompleted) System.currentTimeMillis() else null
            )
            repository.updateChapter(updated)
        }
    }

    fun deleteChapter(chapter: Chapter) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
        }
    }

    // ----------------- Timer Operations -----------------
    fun setTimerMode(mode: String) {
        if (_timerMode.value != mode) {
            pauseTimer()
            _timerMode.value = mode
            if (mode == "POMODORO") {
                _timerSecondsLeft.value = _timerDurationSeconds.value
            } else {
                _stopwatchSeconds.value = 0
            }
        }
    }

    fun setTimerDurationMinutes(minutes: Int) {
        _timerDurationSeconds.value = minutes * 60
        if (!_isTimerRunning.value && _timerMode.value == "POMODORO") {
            _timerSecondsLeft.value = minutes * 60
        }
    }

    fun setSelectedSubjectForStudy(subjectId: Long?) {
        _selectedSubjectIdForStudy.value = subjectId
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000L)
                if (_timerMode.value == "POMODORO") {
                    if (_timerSecondsLeft.value > 0) {
                        _timerSecondsLeft.value -= 1
                    } else {
                        // Timer completed
                        _isTimerRunning.value = false
                        saveStudySession(
                            durationMinutes = _timerDurationSeconds.value / 60,
                            type = "POMODORO"
                        )
                        _timerSecondsLeft.value = _timerDurationSeconds.value
                        break
                    }
                } else {
                    // Stopwatch mode
                    _stopwatchSeconds.value += 1
                }
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        if (_timerMode.value == "POMODORO") {
            _timerSecondsLeft.value = _timerDurationSeconds.value
        } else {
            _stopwatchSeconds.value = 0
        }
    }

    fun finishAndSaveStopwatch() {
        val seconds = _stopwatchSeconds.value
        pauseTimer()
        if (seconds >= 60) {
            val minutes = seconds / 60
            saveStudySession(durationMinutes = minutes, type = "STOPWATCH")
        }
        _stopwatchSeconds.value = 0
    }

    fun saveManualStudySession(subjectId: Long?, durationMinutes: Int) {
        if (durationMinutes <= 0) return
        saveStudySession(durationMinutes, "MANUAL", subjectId)
    }

    private fun saveStudySession(durationMinutes: Int, type: String, subjectId: Long? = _selectedSubjectIdForStudy.value) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val session = StudySession(
                subjectId = subjectId,
                date = DateUtils.getTodayDateString(),
                startTime = now - (durationMinutes * 60 * 1000),
                endTime = now,
                durationMinutes = durationMinutes,
                studyType = type
            )
            repository.insertSession(session)
        }
    }

    // ----------------- Task Operations -----------------
    fun addTask(
        title: String,
        description: String,
        subjectId: Long?,
        type: String,
        priority: String,
        dueDate: String,
        dueTime: String
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                description = description.trim(),
                subjectId = subjectId,
                type = type,
                priority = priority,
                dueDate = dueDate,
                dueTime = dueTime,
                completed = false
            )
            repository.insertTask(task)
        }
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            val isNowCompleted = !task.completed
            val updated = task.copy(
                completed = isNowCompleted,
                completedAt = if (isNowCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // ----------------- Timetable Operations -----------------
    fun addTimetableEntry(
        dayOfWeek: String,
        subjectId: Long,
        startTime: String,
        endTime: String,
        teacher: String,
        room: String
    ) {
        viewModelScope.launch {
            val entry = TimetableEntry(
                dayOfWeek = dayOfWeek,
                subjectId = subjectId,
                startTime = startTime,
                endTime = endTime,
                teacher = teacher.trim(),
                room = room.trim()
            )
            repository.insertTimetableEntry(entry)
        }
    }

    fun deleteTimetableEntry(entry: TimetableEntry) {
        viewModelScope.launch {
            repository.deleteTimetableEntry(entry)
        }
    }

    // ----------------- Exam Operations -----------------
    fun addExam(name: String, examDate: String, description: String, subjectIds: List<Long>) {
        viewModelScope.launch {
            val exam = Exam(
                name = name.trim(),
                examDate = examDate,
                description = description.trim()
            )
            val examId = repository.insertExam(exam)
            subjectIds.forEach { subId ->
                repository.insertExamSubject(
                    ExamSubject(examId = examId, subjectId = subId, preparationPercentage = 0)
                )
            }
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    // ----------------- Attendance Operations -----------------
    fun markAttendance(subjectId: Long, status: String, date: String = DateUtils.getTodayDateString()) {
        viewModelScope.launch {
            val attendance = Attendance(
                subjectId = subjectId,
                date = date,
                status = status
            )
            repository.insertAttendance(attendance)
        }
    }

    fun deleteAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.deleteAttendance(attendance)
        }
    }

    // ----------------- Marks Operations -----------------
    fun addMark(
        subjectId: Long,
        examName: String,
        obtainedMarks: Double,
        maximumMarks: Double,
        examDate: String
    ) {
        viewModelScope.launch {
            val mark = Mark(
                subjectId = subjectId,
                examName = examName.trim(),
                obtainedMarks = obtainedMarks,
                maximumMarks = maximumMarks,
                examDate = examDate
            )
            repository.insertMark(mark)
        }
    }

    fun deleteMark(mark: Mark) {
        viewModelScope.launch {
            repository.deleteMark(mark)
        }
    }

    // ----------------- Backup & Restore -----------------
    fun exportBackupJson(): String {
        return try {
            val root = JSONObject()
            // Export subjects
            val subs = JSONArray()
            allSubjects.value.forEach { s ->
                val obj = JSONObject().apply {
                    put("name", s.name)
                    put("code", s.code)
                    put("teacher", s.teacher)
                    put("targetPercentage", s.targetPercentage)
                    put("colorHex", s.colorHex)
                }
                subs.put(obj)
            }
            root.put("subjects", subs)

            // Export tasks
            val tasksArr = JSONArray()
            allTasks.value.forEach { t ->
                val obj = JSONObject().apply {
                    put("title", t.title)
                    put("description", t.description)
                    put("type", t.type)
                    put("priority", t.priority)
                    put("dueDate", t.dueDate)
                    put("dueTime", t.dueTime)
                    put("completed", t.completed)
                }
                tasksArr.put(obj)
            }
            root.put("tasks", tasksArr)

            // Export profile
            studentProfile.value?.let { p ->
                val pObj = JSONObject().apply {
                    put("name", p.name)
                    put("course", p.course)
                    put("college", p.college)
                    put("semester", p.semester)
                    put("academicYear", p.academicYear)
                    put("dailyStudyTarget", p.dailyStudyTarget)
                }
                root.put("profile", pObj)
            }

            root.toString(2)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun importBackupJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val root = JSONObject(jsonString)
                if (root.has("profile")) {
                    val pObj = root.getJSONObject("profile")
                    val profile = StudentProfile(
                        name = pObj.optString("name", "Student"),
                        course = pObj.optString("course", ""),
                        college = pObj.optString("college", ""),
                        semester = pObj.optString("semester", "1"),
                        academicYear = pObj.optString("academicYear", ""),
                        dailyStudyTarget = pObj.optInt("dailyStudyTarget", 120)
                    )
                    repository.saveProfile(profile)
                }
                if (root.has("subjects")) {
                    val subs = root.getJSONArray("subjects")
                    for (i in 0 until subs.length()) {
                        val obj = subs.getJSONObject(i)
                        repository.insertSubject(
                            Subject(
                                name = obj.getString("name"),
                                code = obj.optString("code", ""),
                                teacher = obj.optString("teacher", ""),
                                targetPercentage = obj.optDouble("targetPercentage", 75.0),
                                colorHex = obj.optString("colorHex", "#4F46E5")
                            )
                        )
                    }
                }
                if (root.has("tasks")) {
                    val tasks = root.getJSONArray("tasks")
                    for (i in 0 until tasks.length()) {
                        val obj = tasks.getJSONObject(i)
                        repository.insertTask(
                            Task(
                                title = obj.getString("title"),
                                description = obj.optString("description", ""),
                                type = obj.optString("type", "ASSIGNMENT"),
                                priority = obj.optString("priority", "MEDIUM"),
                                dueDate = obj.optString("dueDate", ""),
                                dueTime = obj.optString("dueTime", ""),
                                completed = obj.optBoolean("completed", false)
                            )
                        )
                    }
                }
                _backupStatusMessage.value = "Backup restored successfully!"
            } catch (e: Exception) {
                _backupStatusMessage.value = "Failed to restore: ${e.localizedMessage}"
            }
        }
    }

    fun clearBackupStatusMessage() {
        _backupStatusMessage.value = null
    }

    fun exportAllDataAsJson(): String = exportBackupJson()

    suspend fun restoreDataFromJson(jsonString: String): Boolean {
        return try {
            importBackupJson(jsonString)
            true
        } catch (e: Exception) {
            false
        }
    }
}
