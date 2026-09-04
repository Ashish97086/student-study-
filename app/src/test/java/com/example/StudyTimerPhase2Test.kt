package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.entity.StudySession
import com.example.data.entity.Subject
import com.example.timer.PomodoroPhase
import com.example.timer.StudyTimerManager
import com.example.timer.TimerMode
import com.example.timer.TimerStatus
import com.example.utils.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudyTimerPhase2Test {

    private lateinit var database: AppDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testStreakCalculation() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val today = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val twoDaysAgo = dateFormat.format(cal.time)

        // 3 consecutive days including today
        val dates3Days = listOf(today, yesterday, twoDaysAgo)
        assertEquals(3, DateUtils.calculateStreak(dates3Days))
        assertEquals(3, DateUtils.calculateBestStreak(dates3Days))
        assertEquals(3, DateUtils.calculateTotalStudyDays(dates3Days))

        // Multiple sessions on the same day should not increase streak
        val duplicateDates = listOf(today, today, yesterday, yesterday, twoDaysAgo)
        assertEquals(3, DateUtils.calculateStreak(duplicateDates))

        // Missed day breaks streak
        cal.add(Calendar.DAY_OF_YEAR, -2) // skipped 3 days ago
        val fiveDaysAgo = dateFormat.format(cal.time)
        val brokenDates = listOf(fiveDaysAgo)
        assertEquals(0, DateUtils.calculateStreak(brokenDates))
    }

    @Test
    fun testDatabaseSessionQueriesAndStats() = runBlocking {
        val subjectDao = database.subjectDao()
        val sessionDao = database.studySessionDao()

        val mathId = subjectDao.insertSubject(Subject(name = "Mathematics", colorHex = "#0061A4"))
        val physicsId = subjectDao.insertSubject(Subject(name = "Physics", colorHex = "#B3261E"))

        val today = DateUtils.getTodayDateString()
        val now = System.currentTimeMillis()

        sessionDao.insertSession(
            StudySession(
                subjectId = mathId,
                date = today,
                startTime = now - 45 * 60 * 1000L,
                endTime = now,
                durationMinutes = 45,
                studyType = "Revision"
            )
        )

        sessionDao.insertSession(
            StudySession(
                subjectId = physicsId,
                date = today,
                startTime = now - 30 * 60 * 1000L,
                endTime = now,
                durationMinutes = 30,
                studyType = "Practice"
            )
        )

        // Verify Today's study time
        val todayMinutes = sessionDao.getTotalMinutesForDate(today).first()
        assertEquals(75, todayMinutes)

        // Verify Sessions Count
        val count = sessionDao.getTotalSessionsCount().first()
        assertEquals(2, count)

        // Verify Total Study Minutes
        val totalMins = sessionDao.getTotalStudyMinutes().first()
        assertEquals(75, totalMins)

        // Verify Most Studied Subject is Mathematics (45 > 30)
        val topSubjectId = sessionDao.getMostStudiedSubjectId().first()
        assertEquals(mathId, topSubjectId)

        // Verify Filtering by Subject
        val mathSessions = sessionDao.getFilteredSessions(mathId, null, null).first()
        assertEquals(1, mathSessions.size)
        assertEquals("Revision", mathSessions[0].studyType)

        // Verify Filtering by Study Type
        val practiceSessions = sessionDao.getFilteredSessions(null, "Practice", null).first()
        assertEquals(1, practiceSessions.size)
        assertEquals(physicsId, practiceSessions[0].subjectId)
    }

    @Test
    fun testStudyTimerManager_ModesAndControls() {
        StudyTimerManager.resetTimer(context)

        // Set to Study Timer mode with 30 minutes
        StudyTimerManager.setTimerMode(TimerMode.STUDY_TIMER)
        StudyTimerManager.setStandardTimerDuration(30)
        assertEquals(30 * 60, StudyTimerManager.remainingSeconds.value)
        assertEquals(TimerStatus.IDLE, StudyTimerManager.timerStatus.value)

        // Start timer
        StudyTimerManager.startTimer(context)
        assertEquals(TimerStatus.RUNNING, StudyTimerManager.timerStatus.value)

        // Pause timer
        StudyTimerManager.pauseTimer(context)
        assertEquals(TimerStatus.PAUSED, StudyTimerManager.timerStatus.value)

        // Resume timer
        StudyTimerManager.resumeTimer(context)
        assertEquals(TimerStatus.RUNNING, StudyTimerManager.timerStatus.value)

        // Stop timer
        StudyTimerManager.stopTimer(context)
        // Reset afterwards
        StudyTimerManager.resetTimer(context)
        assertEquals(TimerStatus.IDLE, StudyTimerManager.timerStatus.value)
    }

    @Test
    fun testPomodoroCycleSwitching() {
        StudyTimerManager.resetTimer(context)
        StudyTimerManager.setTimerMode(TimerMode.POMODORO)
        StudyTimerManager.updatePomodoroSettings(
            studyMins = 25,
            breakMins = 5,
            longBreakMins = 15,
            cycles = 4
        )

        assertEquals(TimerMode.POMODORO, StudyTimerManager.timerMode.value)
        assertEquals(PomodoroPhase.STUDY, StudyTimerManager.pomodoroPhase.value)
        assertEquals(1, StudyTimerManager.currentCycle.value)
        assertEquals(4, StudyTimerManager.totalCycles.value)
        assertEquals(25 * 60, StudyTimerManager.remainingSeconds.value)

        // Skip break test
        StudyTimerManager.skipBreak(context)
        assertEquals(PomodoroPhase.STUDY, StudyTimerManager.pomodoroPhase.value)
    }
}
