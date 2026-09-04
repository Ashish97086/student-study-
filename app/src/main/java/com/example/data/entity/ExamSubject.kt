package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_subjects")
data class ExamSubject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val examId: Long,
    val subjectId: Long,
    val preparationPercentage: Int = 0
)
