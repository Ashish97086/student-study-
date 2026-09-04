package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long? = null,
    val chapterId: Long? = null,
    val date: String, // YYYY-MM-DD
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val studyType: String = "POMODORO" // POMODORO, STOPWATCH, MANUAL
)
