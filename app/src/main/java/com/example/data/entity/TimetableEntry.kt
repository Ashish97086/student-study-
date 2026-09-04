package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_entries")
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    val subjectId: Long,
    val startTime: String, // e.g. "09:00"
    val endTime: String,   // e.g. "10:00"
    val teacher: String = "",
    val room: String = ""
)
