package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.database.AppDatabase
import com.example.data.entity.StudySession
import com.example.notification.StudyNotificationHelper
import com.example.timer.StudyTimerManager
import com.example.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyTimerReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TIMER_FINISHED = "com.example.ACTION_STUDY_TIMER_FINISHED"
        const val EXTRA_SUBJECT_ID = "extra_subject_id"
        const val EXTRA_CHAPTER_ID = "extra_chapter_id"
        const val EXTRA_STUDY_TYPE = "extra_study_type"
        const val EXTRA_DURATION_MINUTES = "extra_duration_minutes"
        const val EXTRA_SUBJECT_NAME = "extra_subject_name"
        const val EXTRA_IS_BREAK = "extra_is_break"
        const val EXTRA_SESSION_CYCLE_TEXT = "extra_session_cycle_text"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TIMER_FINISHED) return

        val isBreak = intent.getBooleanExtra(EXTRA_IS_BREAK, false)
        val subjectId = if (intent.hasExtra(EXTRA_SUBJECT_ID)) intent.getLongExtra(EXTRA_SUBJECT_ID, -1L).takeIf { it != -1L } else null
        val chapterId = if (intent.hasExtra(EXTRA_CHAPTER_ID)) intent.getLongExtra(EXTRA_CHAPTER_ID, -1L).takeIf { it != -1L } else null
        val studyType = intent.getStringExtra(EXTRA_STUDY_TYPE) ?: "Normal Study"
        val durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 25)
        val subjectName = intent.getStringExtra(EXTRA_SUBJECT_NAME) ?: "General Study"
        val cycleText = intent.getStringExtra(EXTRA_SESSION_CYCLE_TEXT) ?: ""

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!isBreak) {
                    // Save completed study session
                    val now = System.currentTimeMillis()
                    val session = StudySession(
                        subjectId = subjectId,
                        chapterId = chapterId,
                        date = DateUtils.getTodayDateString(),
                        startTime = now - (durationMinutes * 60 * 1000L),
                        endTime = now,
                        durationMinutes = durationMinutes,
                        studyType = studyType
                    )
                    val database = AppDatabase.getDatabase(context)
                    database.studySessionDao().insertSession(session)

                    // Show notification
                    val title = "Study Session Completed! 🎉"
                    val message = "Great job! Completed $durationMinutes min of $studyType for $subjectName. $cycleText"
                    StudyNotificationHelper.showSessionFinishedNotification(context, title, message)
                } else {
                    // Break finished notification
                    val title = "Break Finished! 🔔"
                    val message = "Your break is complete! Ready to start your next study session."
                    StudyNotificationHelper.showBreakFinishedNotification(context, title, message)
                }

                // Notify in-memory manager
                StudyTimerManager.onAlarmTriggered(context, isBreak)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
