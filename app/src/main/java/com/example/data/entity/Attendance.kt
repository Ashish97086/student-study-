package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendances")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val date: String, // YYYY-MM-DD
    val status: String // PRESENT, ABSENT
)
