package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marks")
data class Mark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val examName: String,
    val obtainedMarks: Double,
    val maximumMarks: Double,
    val examDate: String // YYYY-MM-DD
)
