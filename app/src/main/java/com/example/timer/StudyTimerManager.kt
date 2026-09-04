package com.example.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.database.AppDatabase
import com.example.data.entity.StudySession
import com.example.notification.StudyNotificationHelper
import com.example.receiver.StudyTimerReceiver
import com.example.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TimerMode {
    STUDY_TIMER,
    POMODORO
}

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

enum class PomodoroPhase {
    STUDY,
    SHORT_BREAK,
    LONG_BREAK
}

data class PendingStopSession(
    val subjectId: Long?,
    val chapterId: Long?,
    val studyType: String,
    val elapsedSeconds: Int,
    val elapsedMinutes: Int,
    val startTime: Long,
    val endTime: Long,
    val date: String
)

object StudyTimerManager {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    // State flows
    private val _timerMode = MutableStateFlow(TimerMode.STUDY_TIMER)
    val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

    private val _timerStatus = MutableStateFlow(TimerStatus.IDLE)
    val timerStatus: StateFlow<TimerStatus> = _timerStatus.asStateFlow()

    private val _pomodoroPhase = MutableStateFlow(PomodoroPhase.STUDY)
    val pomodoroPhase: StateFlow<PomodoroPhase> = _pomodoroPhase.asStateFlow()

    private val _currentCycle = MutableStateFlow(1)
    val currentCycle: StateFlow<Int> = _currentCycle.asStateFlow()

    private val _totalCycles = MutableStateFlow(4)
    val totalCycles: StateFlow<Int> = _totalCycles.asStateFlow()

    // Configurable durations (in minutes)
    private val _pomodoroStudyMinutes = MutableStateFlow(25)
    val pomodoroStudyMinutes: StateFlow<Int> = _pomodoroStudyMinutes.asStateFlow()

    private val _pomodoroBreakMinutes = MutableStateFlow(5)
    val pomodoroBreakMinutes: StateFlow<Int> = _pomodoroBreakMinutes.asStateFlow()

    private val _pomodoroLongBreakMinutes = MutableStateFlow(15)
    val pomodoroLongBreakMinutes: StateFlow<Int> = _pomodoroLongBreakMinutes.asStateFlow()

    private val _standardTimerMinutes = MutableStateFlow(25)
    val standardTimerMinutes: StateFlow<Int> = _standardTimerMinutes.asStateFlow()

    // Current session timing
    private val _totalDurationSeconds = MutableStateFlow(25 * 60)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    // Selected metadata
    private val _selectedSubjectId = MutableStateFlow<Long?>(null)
    val selectedSubjectId: StateFlow<Long?> = _selectedSubjectId.asStateFlow()

    private val _selectedSubjectName = MutableStateFlow<String?>("General Study")
    val selectedSubjectName: StateFlow<String?> = _selectedSubjectName.asStateFlow()

    private val _selectedChapterId = MutableStateFlow<Long?>(null)
    val selectedChapterId: StateFlow<Long?> = _selectedChapterId.asStateFlow()

    private val _selectedChapterName = MutableStateFlow<String?>(null)
    val selectedChapterName: StateFlow<String?> = _selectedChapterName.asStateFlow()

    private val _selectedStudyType = MutableStateFlow("Normal Study")
    val selectedStudyType: StateFlow<String> = _selectedStudyType.asStateFlow()

    // Dialog state
    private val _pendingStopSession = MutableStateFlow<PendingStopSession?>(null)
    val pendingStopSession: StateFlow<PendingStopSession?> = _pendingStopSession.asStateFlow()

    private val _completionMessage = MutableStateFlow<String?>(null)
    val completionMessage: StateFlow<String?> = _completionMessage.asStateFlow()

    // Internal timing variables
    private var targetEndTimeMillis: Long = 0L
    private var sessionStartTimeMillis: Long = 0L

    val studyTypes = listOf(
        "Normal Study",
        "Revision",
        "Practice",
        "Homework",
        "Assignment",
        "Exam Preparation"
    )

    fun setTimerMode(mode: TimerMode) {
        if (_timerMode.value != mode) {
            _timerMode.value = mode
            resetTimerInternal()
        }
    }

    fun setSubject(id: Long?, name: String?) {
        _selectedSubjectId.value = id
        _selectedSubjectName.value = name ?: "General Study"
        // Reset chapter when subject changes
        _selectedChapterId.value = null
        _selectedChapterName.value = null
    }

    fun setChapter(id: Long?, name: String?) {
        _selectedChapterId.value = id
        _selectedChapterName.value = name
    }

    fun setStudyType(type: String) {
        _selectedStudyType.value = type
    }

    fun setStandardTimerDuration(minutes: Int) {
        _standardTimerMinutes.value = minutes
        if (_timerMode.value == TimerMode.STUDY_TIMER && _timerStatus.value == TimerStatus.IDLE) {
            _totalDurationSeconds.value = minutes * 60
            _remainingSeconds.value = minutes * 60
        }
    }

    fun updatePomodoroSettings(studyMins: Int, breakMins: Int, longBreakMins: Int, cycles: Int) {
        _pomodoroStudyMinutes.value = studyMins
        _pomodoroBreakMinutes.value = breakMins
        _pomodoroLongBreakMinutes.value = longBreakMins
        _totalCycles.value = cycles
        if (_timerMode.value == TimerMode.POMODORO && _timerStatus.value == TimerStatus.IDLE) {
            resetPomodoroDuration()
        }
    }

    private fun resetPomodoroDuration() {
        val mins = when (_pomodoroPhase.value) {
            PomodoroPhase.STUDY -> _pomodoroStudyMinutes.value
            PomodoroPhase.SHORT_BREAK -> _pomodoroBreakMinutes.value
            PomodoroPhase.LONG_BREAK -> _pomodoroLongBreakMinutes.value
        }
        _totalDurationSeconds.value = mins * 60
        _remainingSeconds.value = mins * 60
    }

    fun startTimer(context: Context) {
        if (_timerStatus.value == TimerStatus.RUNNING) return

        sessionStartTimeMillis = System.currentTimeMillis()
        targetEndTimeMillis = sessionStartTimeMillis + (_remainingSeconds.value * 1000L)
        _timerStatus.value = TimerStatus.RUNNING

        scheduleAlarm(context, targetEndTimeMillis)
        startTicker(context)
    }

    fun pauseTimer(context: Context) {
        if (_timerStatus.value != TimerStatus.RUNNING) return

        cancelAlarm(context)
        stopTicker()
        // Compute exact remaining at this moment
        val now = System.currentTimeMillis()
        val remaining = ((targetEndTimeMillis - now + 999) / 1000).toInt().coerceAtLeast(0)
        _remainingSeconds.value = remaining
        _timerStatus.value = TimerStatus.PAUSED
    }

    fun resumeTimer(context: Context) {
        if (_timerStatus.value != TimerStatus.PAUSED) return

        targetEndTimeMillis = System.currentTimeMillis() + (_remainingSeconds.value * 1000L)
        _timerStatus.value = TimerStatus.RUNNING

        scheduleAlarm(context, targetEndTimeMillis)
        startTicker(context)
    }

    fun stopTimer(context: Context) {
        cancelAlarm(context)
        stopTicker()

        val now = System.currentTimeMillis()
        val elapsedSec = (_totalDurationSeconds.value - _remainingSeconds.value).coerceAtLeast(0)
        val elapsedMins = elapsedSec / 60

        // If in study phase and elapsed time is at least 10 seconds, ask to save
        if ((_timerMode.value == TimerMode.STUDY_TIMER || _pomodoroPhase.value == PomodoroPhase.STUDY) && elapsedSec >= 10) {
            _pendingStopSession.value = PendingStopSession(
                subjectId = _selectedSubjectId.value,
                chapterId = _selectedChapterId.value,
                studyType = _selectedStudyType.value,
                elapsedSeconds = elapsedSec,
                elapsedMinutes = if (elapsedMins > 0) elapsedMins else 1, // log at least 1 min if >= 10s
                startTime = sessionStartTimeMillis.takeIf { it > 0 } ?: (now - elapsedSec * 1000L),
                endTime = now,
                date = DateUtils.getTodayDateString()
            )
        } else {
            resetTimerInternal()
        }
    }

    fun confirmSaveStoppedSession(context: Context, database: AppDatabase) {
        val pending = _pendingStopSession.value ?: return
        scope.launch(Dispatchers.IO) {
            val session = StudySession(
                subjectId = pending.subjectId,
                chapterId = pending.chapterId,
                date = pending.date,
                startTime = pending.startTime,
                endTime = pending.endTime,
                durationMinutes = pending.elapsedMinutes,
                studyType = pending.studyType
            )
            database.studySessionDao().insertSession(session)
        }
        _pendingStopSession.value = null
        resetTimerInternal()
    }

    fun discardStoppedSession() {
        _pendingStopSession.value = null
        resetTimerInternal()
    }

    fun resetTimer(context: Context) {
        cancelAlarm(context)
        stopTicker()
        resetTimerInternal()
    }

    fun skipBreak(context: Context) {
        cancelAlarm(context)
        stopTicker()
        if (_pomodoroPhase.value == PomodoroPhase.SHORT_BREAK) {
            _currentCycle.value = (_currentCycle.value + 1).coerceAtMost(_totalCycles.value)
        } else if (_pomodoroPhase.value == PomodoroPhase.LONG_BREAK) {
            _currentCycle.value = 1
        }
        _pomodoroPhase.value = PomodoroPhase.STUDY
        resetPomodoroDuration()
        _timerStatus.value = TimerStatus.IDLE
    }

    fun dismissCompletionMessage() {
        _completionMessage.value = null
    }

    private fun resetTimerInternal() {
        _timerStatus.value = TimerStatus.IDLE
        if (_timerMode.value == TimerMode.STUDY_TIMER) {
            _totalDurationSeconds.value = _standardTimerMinutes.value * 60
            _remainingSeconds.value = _standardTimerMinutes.value * 60
        } else {
            resetPomodoroDuration()
        }
    }

    private fun startTicker(context: Context) {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (_timerStatus.value == TimerStatus.RUNNING) {
                delay(500L)
                val now = System.currentTimeMillis()
                val left = ((targetEndTimeMillis - now + 999) / 1000).toInt()
                if (left <= 0) {
                    _remainingSeconds.value = 0
                    handleTimerCompletion(context)
                    break
                } else {
                    _remainingSeconds.value = left
                }
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun handleTimerCompletion(context: Context) {
        cancelAlarm(context)
        stopTicker()
        _timerStatus.value = TimerStatus.COMPLETED

        val database = AppDatabase.getDatabase(context)
        val now = System.currentTimeMillis()

        if (_timerMode.value == TimerMode.STUDY_TIMER) {
            val mins = _totalDurationSeconds.value / 60
            scope.launch(Dispatchers.IO) {
                val session = StudySession(
                    subjectId = _selectedSubjectId.value,
                    chapterId = _selectedChapterId.value,
                    date = DateUtils.getTodayDateString(),
                    startTime = sessionStartTimeMillis.takeIf { it > 0 } ?: (now - mins * 60 * 1000L),
                    endTime = now,
                    durationMinutes = mins,
                    studyType = _selectedStudyType.value
                )
                database.studySessionDao().insertSession(session)
            }
            StudyNotificationHelper.showSessionFinishedNotification(
                context,
                "Study Session Completed! 🎉",
                "Awesome job! You completed $mins minutes of ${_selectedStudyType.value}."
            )
            _completionMessage.value = "🎉 Awesome! Completed $mins minutes of ${_selectedStudyType.value}."
            _remainingSeconds.value = _totalDurationSeconds.value
            _timerStatus.value = TimerStatus.IDLE

        } else {
            // Pomodoro Mode
            when (_pomodoroPhase.value) {
                PomodoroPhase.STUDY -> {
                    val mins = _totalDurationSeconds.value / 60
                    scope.launch(Dispatchers.IO) {
                        val session = StudySession(
                            subjectId = _selectedSubjectId.value,
                            chapterId = _selectedChapterId.value,
                            date = DateUtils.getTodayDateString(),
                            startTime = sessionStartTimeMillis.takeIf { it > 0 } ?: (now - mins * 60 * 1000L),
                            endTime = now,
                            durationMinutes = mins,
                            studyType = _selectedStudyType.value
                        )
                        database.studySessionDao().insertSession(session)
                    }

                    if (_currentCycle.value >= _totalCycles.value) {
                        // Advance to long break
                        _pomodoroPhase.value = PomodoroPhase.LONG_BREAK
                        resetPomodoroDuration()
                        _completionMessage.value = "🏆 Cycle ${_totalCycles.value} of ${_totalCycles.value} complete! Enjoy a ${_pomodoroLongBreakMinutes.value}-min Long Break."
                        StudyNotificationHelper.showSessionFinishedNotification(
                            context,
                            "Pomodoro Cycle Completed! 🏆",
                            "Session ${_totalCycles.value} done! Time for a well-deserved ${_pomodoroLongBreakMinutes.value}-min Long Break."
                        )
                    } else {
                        // Advance to short break
                        _pomodoroPhase.value = PomodoroPhase.SHORT_BREAK
                        resetPomodoroDuration()
                        _completionMessage.value = "☕ Session ${_currentCycle.value} of ${_totalCycles.value} complete! Take a ${_pomodoroBreakMinutes.value}-min Break."
                        StudyNotificationHelper.showSessionFinishedNotification(
                            context,
                            "Study Session Complete! ☕",
                            "Great focus! Take a ${_pomodoroBreakMinutes.value}-minute break."
                        )
                    }
                    _timerStatus.value = TimerStatus.IDLE
                }

                PomodoroPhase.SHORT_BREAK -> {
                    _currentCycle.value = (_currentCycle.value + 1).coerceAtMost(_totalCycles.value)
                    _pomodoroPhase.value = PomodoroPhase.STUDY
                    resetPomodoroDuration()
                    _completionMessage.value = "🔔 Break is over! Ready for Session ${_currentCycle.value} of ${_totalCycles.value}."
                    StudyNotificationHelper.showBreakFinishedNotification(
                        context,
                        "Break Finished! 🔔",
                        "Ready for Session ${_currentCycle.value} of ${_totalCycles.value}."
                    )
                    _timerStatus.value = TimerStatus.IDLE
                }

                PomodoroPhase.LONG_BREAK -> {
                    _currentCycle.value = 1
                    _pomodoroPhase.value = PomodoroPhase.STUDY
                    resetPomodoroDuration()
                    _completionMessage.value = "✨ Long break finished! Starting a fresh Pomodoro cycle."
                    StudyNotificationHelper.showBreakFinishedNotification(
                        context,
                        "Long Break Finished! ✨",
                        "Refreshed and recharged! Ready for a new study cycle."
                    )
                    _timerStatus.value = TimerStatus.IDLE
                }
            }
        }
    }

    fun onAlarmTriggered(context: Context, isBreak: Boolean) {
        scope.launch(Dispatchers.Main) {
            if (_timerStatus.value == TimerStatus.RUNNING) {
                handleTimerCompletion(context)
            }
        }
    }

    private fun scheduleAlarm(context: Context, targetEndTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, StudyTimerReceiver::class.java).apply {
            action = StudyTimerReceiver.ACTION_TIMER_FINISHED
            putExtra(StudyTimerReceiver.EXTRA_IS_BREAK, _timerMode.value == TimerMode.POMODORO && _pomodoroPhase.value != PomodoroPhase.STUDY)
            putExtra(StudyTimerReceiver.EXTRA_SUBJECT_ID, _selectedSubjectId.value ?: -1L)
            putExtra(StudyTimerReceiver.EXTRA_CHAPTER_ID, _selectedChapterId.value ?: -1L)
            putExtra(StudyTimerReceiver.EXTRA_STUDY_TYPE, _selectedStudyType.value)
            putExtra(StudyTimerReceiver.EXTRA_DURATION_MINUTES, _totalDurationSeconds.value / 60)
            putExtra(StudyTimerReceiver.EXTRA_SUBJECT_NAME, _selectedSubjectName.value ?: "General Study")
            putExtra(StudyTimerReceiver.EXTRA_SESSION_CYCLE_TEXT, if (_timerMode.value == TimerMode.POMODORO) "Session ${_currentCycle.value} of ${_totalCycles.value}" else "")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    targetEndTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetEndTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback if exact alarms permission is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                targetEndTime,
                pendingIntent
            )
        }
    }

    private fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, StudyTimerReceiver::class.java).apply {
            action = StudyTimerReceiver.ACTION_TIMER_FINISHED
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
